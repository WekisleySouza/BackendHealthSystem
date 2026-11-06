package com.project.healthsystem.unit.service;

import com.project.healthsystem.service.datasus.*;
import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static builders.CnesFixtures.*;
import static com.project.healthsystem.service.datasus.CnesCsvReader.Catalog.*;

class CnesServiceTest {
    CnesCsvReader reader = new CnesCsvReader();
    CnesImportService importer = mock(CnesImportService.class);
    CnesService service = new CnesService(reader, importer);

    MockMultipartFile file(String content) {
        return new MockMultipartFile("file","input.csv","text/csv", content.getBytes(StandardCharsets.UTF_8));
    }
    @Test void invalidFileNeverCallsDatabaseWriter() {
        assertThrows(InvalidDataException.class, () -> service.importDataEstabilishments(file(EST_HEADER)));
        assertThrows(InvalidDataException.class, () -> service.importDataProfessionals(file(
            PROF_HEADER + "\n" + professional("1234","0012345"))));
        verifyNoInteractions(importer);
    }
    @Test void emptyUploadIsRejected() {
        assertThrows(InvalidDataException.class, () -> service.importDataProfessionals(file("")));
        verifyNoInteractions(importer);
    }
    @Test void validatedFileAndCountArePassedAndTemporaryIsRemoved() {
        final Path[] copy = new Path[1];
        when(importer.replace(any(), eq(ESTABLISHMENTS), eq(1L))).thenAnswer(i -> {
            copy[0] = i.getArgument(0);
            assertTrue(Files.exists(copy[0]));
            return new CnesImportResponseDTO("ESTABLISHMENTS",1);
        });
        assertEquals(1,service.importDataEstabilishments(file(EST_HEADER + "\n" + establishment("0012345"))).importedRecords());
        assertFalse(Files.exists(copy[0]));
    }
    @Test void failureCleansTemporaryAndReleasesLock() {
        final Path[] copy = new Path[1];
        when(importer.replace(any(), eq(ESTABLISHMENTS), anyLong())).thenAnswer(i -> {
            copy[0] = i.getArgument(0); throw new IllegalStateException("database failed");
        });
        var file = file(EST_HEADER + "\n" + establishment("0012345"));
        assertThrows(IllegalStateException.class, () -> service.importDataEstabilishments(file));
        assertFalse(Files.exists(copy[0]));
        assertThrows(IllegalStateException.class, () -> service.importDataEstabilishments(file));
        verify(importer,times(2)).replace(any(),eq(ESTABLISHMENTS),eq(1L));
    }
    @Test void sameCatalogIsBlockedWhileOtherCatalogCanImport() throws Exception {
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        when(importer.replace(any(),eq(ESTABLISHMENTS),anyLong())).thenAnswer(i -> {
            entered.countDown();
            if (!release.await(5,TimeUnit.SECONDS)) throw new IllegalStateException("timeout");
            return new CnesImportResponseDTO("ESTABLISHMENTS",1);
        });
        when(importer.replace(any(),eq(PROFESSIONALS),anyLong())).thenReturn(new CnesImportResponseDTO("PROFESSIONALS",1));
        var file = file(EST_HEADER + "\n" + establishment("0012345"));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            var task = executor.submit(() -> service.importDataEstabilishments(file));
            assertTrue(entered.await(5,TimeUnit.SECONDS));
            assertThrows(InvalidDataException.class, () -> service.importDataEstabilishments(file));
            assertEquals(1,service.importDataProfessionals(file(PROF_HEADER + "\n" + professional("000000000000001","0012345"))).importedRecords());
            release.countDown(); task.get(5,TimeUnit.SECONDS);
        } finally { release.countDown(); executor.shutdownNow(); }
    }
}
