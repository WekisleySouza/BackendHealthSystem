package com.project.healthsystem.unit.service;

import com.project.healthsystem.service.datasus.*;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.exceptions.InvalidDataException;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static builders.CnesFixtures.*;
import static com.project.healthsystem.service.datasus.CnesCsvReader.Catalog.*;

class CnesImportServiceTest {
    @TempDir Path temp;
    CnesEstabilishmentsRepository establishments = mock(CnesEstabilishmentsRepository.class);
    CnesProfessionalsRepository professionals = mock(CnesProfessionalsRepository.class);
    EntityManager em = mock(EntityManager.class);
    Query lock = mock(Query.class);
    CnesImportService importer = new CnesImportService(new CnesCsvReader(),establishments,professionals,em);

    @BeforeEach void setup() {
        when(em.createNativeQuery(anyString())).thenReturn(lock);
        when(lock.setParameter(eq("key"),any())).thenReturn(lock);
        when(lock.getSingleResult()).thenReturn(true);
    }

    @Test void persistsInBoundedBatchesAndClearsContext() throws Exception {
        StringBuilder content = new StringBuilder(EST_HEADER).append('\n');
        for(int i=0;i<1001;i++) content.append(establishment(String.format("%07d",i))).append('\n');
        Path path = Files.writeString(temp.resolve("data.csv"), content);
        List<Integer> sizes = new ArrayList<>();
        when(establishments.saveAllAndFlush(any())).thenAnswer(i -> {
            sizes.add(((List<?>)i.getArgument(0)).size()); return List.of();
        });
        assertEquals(1001, importer.replace(path,ESTABLISHMENTS,1001).importedRecords());
        assertEquals(List.of(1000,1),sizes);
        verify(em,times(2)).clear();
        verifyNoInteractions(professionals);
    }

    @Test void lockConflictDoesNotDeleteAnything() {
        when(lock.getSingleResult()).thenReturn(false);
        assertThrows(InvalidDataException.class, () -> importer.replace(temp,PROFESSIONALS,1));
        verifyNoInteractions(establishments,professionals);
    }

    @Test void rejectsZeroValidatedRowsBeforeAnyDatabaseAccess() {
        assertThrows(InvalidDataException.class, () -> importer.replace(temp,ESTABLISHMENTS,0));
        verifyNoInteractions(establishments,professionals,em);
    }
}
