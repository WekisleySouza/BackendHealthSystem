package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.repository.datasus.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SigtapImportService {
    private final SigtapGroupRepository groups;
    private final SigtapSubGroupRepository subGroups;
    private final SigtapOrganizationRepository organizations;
    private final SigtapProcedureRepository procedures;
    private final SigtapVersionRepository versions;
    private final EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class)
    public SigtapImportResponseDTO importData(Path directory, SigtapVersion available, boolean force)
            throws IOException {
        // Transaction-scoped lock also coordinates multiple backend instances.
        boolean locked = Boolean.TRUE.equals(entityManager.createNativeQuery(
            "select pg_try_advisory_xact_lock(831904621)").getSingleResult());
        if (!locked) throw new InvalidDataException("Já existe uma importação do SIGTAP em andamento.");
        var current = versions.findFirstByOrderByCompetenceDesc();
        if (current.isPresent() && current.get().getCompetence().compareTo(available.getCompetence()) > 0) {
            throw new InvalidDataException("Não é permitido importar uma competência anterior.");
        }
        if (!force && current.isPresent() && available.getFileName().equals(current.get().getFileName())) {
            return new SigtapImportResponseDTO(false, available.getCompetence(), available.getFileName(), 0, 0, 0, 0);
        }

        // Read and validate the complete hierarchy before deleting existing data.
        var newGroups = SigtapFileReader.read(directory.resolve("tb_grupo.txt"), SigtapGroup::fromLine);
        var newSubGroups = SigtapFileReader.read(directory.resolve("tb_sub_grupo.txt"), SigtapSubGroup::fromLine);
        var newOrganizations = SigtapFileReader.read(directory.resolve("tb_forma_organizacao.txt"), SigtapOrganization::fromLine);
        var newProcedures = SigtapFileReader.read(directory.resolve("tb_procedimento.txt"), SigtapProcedure::fromLine);
        var groupMap = index(newGroups, SigtapGroup::getCode, SigtapGroup::getCompetence, available);
        var subGroupMap = index(newSubGroups, SigtapImportService::subGroupKey, SigtapSubGroup::getCompetence, available);
        var organizationMap = index(newOrganizations, SigtapImportService::organizationKey, SigtapOrganization::getCompetence, available);
        index(newProcedures, SigtapProcedure::getCode, SigtapProcedure::getCompetence, available);
        newSubGroups.forEach(item -> item.setGroup(required(groupMap, item.getGroup().getCode())));
        newOrganizations.forEach(item -> item.setSubGroup(required(subGroupMap, subGroupKey(item.getSubGroup()))));
        newProcedures.forEach(item -> item.setOrganization(required(organizationMap, organizationKey(item.getOrganization()))));

        procedures.deleteAllInBatch();
        organizations.deleteAllInBatch();
        subGroups.deleteAllInBatch();
        groups.deleteAllInBatch();
        groups.saveAllAndFlush(newGroups);
        subGroups.saveAllAndFlush(newSubGroups);
        organizations.saveAllAndFlush(newOrganizations);
        procedures.saveAllAndFlush(newProcedures);

        SigtapVersion version = versions.findByCompetence(available.getCompetence()).orElse(available);
        version.setFileName(available.getFileName());
        version.setImportedAt(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        versions.saveAndFlush(version);
        return new SigtapImportResponseDTO(true, version.getCompetence(), version.getFileName(),
            newGroups.size(), newSubGroups.size(), newOrganizations.size(), newProcedures.size());
    }

    private static String subGroupKey(SigtapSubGroup item) {
        return item.getGroup().getCode() + ":" + item.getCode();
    }

    private static String organizationKey(SigtapOrganization item) {
        return subGroupKey(item.getSubGroup()) + ":" + item.getCode();
    }

    private static <T> T required(Map<String, T> entries, String key) {
        T value = entries.get(key);
        if (value == null) throw new InvalidDataException("Referência SIGTAP inexistente: " + key);
        return value;
    }

    private static <T> Map<String, T> index(List<T> entries, Function<T, String> key,
            Function<T, String> competence, SigtapVersion version) {
        if (entries.isEmpty()) throw new InvalidDataException("Arquivo SIGTAP sem registros.");
        Map<String, T> result = new HashMap<>();
        for (T entry : entries) {
            if (!version.getCompetence().equals(competence.apply(entry))) {
                throw new InvalidDataException("Competência dos dados difere da competência do ZIP.");
            }
            String code = key.apply(entry);
            if (code == null || code.isBlank() || result.putIfAbsent(code, entry) != null) {
                throw new InvalidDataException("Código SIGTAP vazio ou duplicado: " + code);
            }
        }
        return result;
    }
}
