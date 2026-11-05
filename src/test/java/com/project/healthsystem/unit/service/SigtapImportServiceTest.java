package com.project.healthsystem.unit.service;

import builders.SigtapFixtures;
import com.project.healthsystem.service.datasus.SigtapImportService;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.model.datasus_data.sigtap.*;
import com.project.healthsystem.exceptions.InvalidDataException;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SigtapImportServiceTest {
    @TempDir Path directory;
    SigtapGroupRepository groups = mock(SigtapGroupRepository.class);
    SigtapSubGroupRepository subs = mock(SigtapSubGroupRepository.class);
    SigtapOrganizationRepository orgs = mock(SigtapOrganizationRepository.class);
    SigtapProcedureRepository procs = mock(SigtapProcedureRepository.class);
    SigtapVersionRepository versions = mock(SigtapVersionRepository.class);
    EntityManager em = mock(EntityManager.class);
    Query lock = mock(Query.class);
    SigtapImportService importer = new SigtapImportService(groups, subs, orgs, procs, versions, em);
    SigtapVersion version = SigtapVersion.build(Path.of("TabelaUnificada_202611_v2611051200.zip"));

    @BeforeEach void setup() throws Exception {
        SigtapFixtures.write(directory);
        when(em.createNativeQuery(anyString())).thenReturn(lock);
        when(lock.getSingleResult()).thenReturn(true);
    }

    @Test void preservesFullHierarchyAndReusesVersion() throws Exception {
        SigtapVersion existing = new SigtapVersion("202611");
        existing.setId(7);
        when(versions.findByCompetence("202611")).thenReturn(Optional.of(existing));
        var result = importer.importData(directory, version, true);
        assertEquals(2, result.procedures());
        verify(procs).saveAllAndFlush(argThat(entries -> {
            List<SigtapProcedure> values = new ArrayList<>();
            entries.forEach(values::add);
            return values.size() == 2 && values.stream().allMatch(p ->
                p.getCode().startsWith(p.getOrganization().getSubGroup().getGroup().getCode()));
        }));
        verify(versions).saveAndFlush(same(existing));
        assertEquals(version.getFileName(), existing.getFileName());
    }

    @Test void missingFileDoesNotDeleteData() throws Exception {
        Files.delete(directory.resolve("tb_procedimento.txt"));
        assertThrows(java.io.IOException.class, () -> importer.importData(directory, version, true));
        verifyNoInteractions(groups, subs, orgs, procs);
    }

    @Test void unknownParentDoesNotDeleteData() throws Exception {
        Path path = directory.resolve("tb_sub_grupo.txt");
        String content = Files.readString(path);
        Files.writeString(path, "99" + content.substring(2));
        assertThrows(InvalidDataException.class, () -> importer.importData(directory, version, true));
        verifyNoInteractions(groups, subs, orgs, procs);
    }

    @Test void mismatchingCompetenceDoesNotDeleteData() throws Exception {
        version.setCompetence("202612");
        assertThrows(InvalidDataException.class, () -> importer.importData(directory, version, true));
        verifyNoInteractions(groups, subs, orgs, procs);
    }

    @Test void concurrentDatabaseImportRejected() {
        when(lock.getSingleResult()).thenReturn(false);
        assertThrows(InvalidDataException.class, () -> importer.importData(directory, version, true));
        verifyNoInteractions(groups, subs, orgs, procs, versions);
    }
}
