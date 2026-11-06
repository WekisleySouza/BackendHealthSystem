package com.project.healthsystem.service.datasus;

import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.model.datasus_data.cnes.*;
import com.project.healthsystem.repository.datasus.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CnesImportService {
    private static final int BATCH_SIZE = 1000;
    private final CnesCsvReader csvReader;
    private final CnesEstabilishmentsRepository establishments;
    private final CnesProfessionalsRepository professionals;
    private final EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class)
    public CnesImportResponseDTO replace(Path file, CnesCsvReader.Catalog catalog, long validatedRows) {
        if (validatedRows <= 0) throw new InvalidDataException("Arquivo CNES sem registros validados.");
        // Separate locks per catalog, shared across backend instances connected to this database.
        boolean locked = Boolean.TRUE.equals(entityManager.createNativeQuery(
            "select pg_try_advisory_xact_lock(:key)").setParameter("key", catalog.lockId()).getSingleResult());
        if (!locked) throw new InvalidDataException("Já existe uma importação de " + catalog + " em andamento.");
        long imported;
        if (catalog == CnesCsvReader.Catalog.ESTABLISHMENTS) {
            imported = replaceRows(file, catalog, establishments, Estabilishment::fromFields);
        } else {
            imported = replaceRows(file, catalog, professionals, CnesProfessional::fromFields);
        }
        if (imported != validatedRows) {
            throw new InvalidDataException("O conteúdo do arquivo CNES mudou após a validação.");
        }
        return new CnesImportResponseDTO(catalog.name(), imported);
    }

    private <T> long replaceRows(Path file, CnesCsvReader.Catalog catalog, JpaRepository<T, Long> repository,
            Function<String[], T> mapper) {
        repository.deleteAllInBatch();
        List<T> batch = new ArrayList<>(BATCH_SIZE);
        long count = csvReader.read(file, catalog, fields -> {
            batch.add(mapper.apply(fields));
            if (batch.size() == BATCH_SIZE) flush(repository, batch);
        });
        if (!batch.isEmpty()) flush(repository, batch);
        return count;
    }

    private <T> void flush(JpaRepository<T, Long> repository, List<T> batch) {
        repository.saveAllAndFlush(batch);
        batch.clear();
        entityManager.clear();
    }
}
