package com.project.healthsystem.unit.service;

import com.project.healthsystem.service.datasus.*;
import com.project.healthsystem.repository.datasus.SigtapVersionRepository;
import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import org.junit.jupiter.api.*;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SigtapServiceTest {
    SigtapVersionRepository versions = mock(SigtapVersionRepository.class);
    SigtapFileProcessor files = mock(SigtapFileProcessor.class);
    SigtapImportService importer = mock(SigtapImportService.class);
    SigtapService service = new SigtapService(versions, files, importer);
    String name = "TabelaUnificada_202611_v2611051200.zip";

    @BeforeEach void setup() throws Exception {
        when(files.findLatestFileName()).thenReturn(name);
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.empty());
    }

    @Test void skipsSameFileWithoutDownload() throws Exception {
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.of(SigtapVersion.build(Path.of(name))));
        assertFalse(service.importSigtapIfNecessary().imported());
        verify(files, never()).download(anyString());
        verifyNoInteractions(importer);
    }

    @Test void forceReimportsSameFile() throws Exception {
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.of(SigtapVersion.build(Path.of(name))));
        prepare();
        assertTrue(service.importSigtap().imported());
        verify(importer).importData(eq(Path.of("extracted")), any(), eq(true));
        verify(files).deleteZip(Path.of(name));
        verify(files).deleteDirectory(Path.of("extracted"));
    }

    @Test void importsRevisionWithinSameCompetence() throws Exception {
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.of(
            SigtapVersion.build(Path.of("TabelaUnificada_202611_v2611011200.zip"))));
        prepare();
        assertTrue(service.importSigtapIfNecessary().imported());
        verify(importer).importData(eq(Path.of("extracted")), any(), eq(false));
    }

    @Test void initialLoadImports() throws Exception {
        prepare();
        assertTrue(service.importSigtapIfNecessary().imported());
    }

    @Test void rejectsDowngradeEvenWhenForced() {
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.of(new SigtapVersion("202612")));
        assertThrows(InvalidDataException.class, service::importSigtap);
        verifyNoInteractions(importer);
    }

    @Test void cleansFilesAndReleasesLockAfterFailure() throws Exception {
        prepare();
        when(importer.importData(any(), any(), anyBoolean())).thenThrow(new IOException("failed"));
        assertThrows(IOException.class, service::importSigtap);
        assertThrows(IOException.class, service::importSigtap);
        verify(files, times(2)).deleteZip(Path.of(name));
        verify(files, times(2)).deleteDirectory(Path.of("extracted"));
    }

    @Test void concurrentTriggerIsRejected() throws Exception {
        CountDownLatch started = new CountDownLatch(1), release = new CountDownLatch(1);
        when(files.findLatestFileName()).thenAnswer(inv -> {
            started.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IOException("timeout");
            return name;
        });
        when(versions.findFirstByOrderByCompetenceDesc()).thenReturn(Optional.of(SigtapVersion.build(Path.of(name))));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> task = executor.submit(() -> { try { service.importSigtapIfNecessary(); } catch (IOException e) { throw new RuntimeException(e); } });
            assertTrue(started.await(5, TimeUnit.SECONDS));
            assertThrows(InvalidDataException.class, service::importSigtap);
            release.countDown();
            task.get(5, TimeUnit.SECONDS);
        } finally { release.countDown(); executor.shutdownNow(); }
    }

    void prepare() throws Exception {
        when(files.download(name)).thenReturn(Path.of(name));
        when(files.extract(Path.of(name))).thenReturn(Path.of("extracted"));
        when(importer.importData(any(), any(), anyBoolean())).thenReturn(
            new SigtapImportResponseDTO(true, "202611", name, 2, 2, 2, 2));
    }
}
