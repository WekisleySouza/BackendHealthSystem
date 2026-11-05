package com.project.healthsystem.unit.service;

import com.project.healthsystem.service.datasus.SigtapFileProcessor;
import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import org.apache.commons.net.ftp.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.*;
import java.util.zip.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SigtapFileProcessorTest {
    @TempDir Path temp;
    FTPClient ftp = mock(FTPClient.class);
    SigtapFileProcessor processor = new SigtapFileProcessor() {
        @Override protected FTPClient newClient() { return ftp; }
    };
    String name = "TabelaUnificada_202611_v2611051200.zip";
    @BeforeEach void setup() throws Exception {
        when(ftp.getReplyCode()).thenReturn(226);
        when(ftp.login(anyString(), anyString())).thenReturn(true);
        when(ftp.setFileType(FTP.BINARY_FILE_TYPE)).thenReturn(true);
        when(ftp.changeWorkingDirectory(anyString())).thenReturn(true);
    }

    @Test void selectsLatestCompetenceAndRevision() throws Exception {
        when(ftp.listFiles()).thenReturn(new FTPFile[]{
            file("TabelaUnificada_202610_v2610311200.zip"),
            file("TabelaUnificada_202611_v2611011200.zip"), file(name),
            file("TabelaUnificada_202613_v2611051200.zip"), file("unrelated.zip")});
        assertEquals(name, processor.findLatestFileName());
    }

    @Test void downloadPreservesNameAndUsesBinaryMode() throws Exception {
        when(ftp.retrieveFile(eq(name), any(OutputStream.class))).thenAnswer(inv -> {
            ((OutputStream)inv.getArgument(1)).write(new byte[]{0, 13, 10, -1});
            return true;
        });
        Path result = processor.download(name);
        try {
            assertEquals(name, result.getFileName().toString());
            assertArrayEquals(new byte[]{0, 13, 10, -1}, Files.readAllBytes(result));
            assertEquals("202611", SigtapVersion.build(result).getCompetence());
            verify(ftp).setFileType(FTP.BINARY_FILE_TYPE);
            verify(ftp).setConnectTimeout(15000);
        } finally { processor.deleteZip(result); }
        assertFalse(Files.exists(result.getParent()));
    }

    @Test void extractsZipAndRejectsTraversal() throws Exception {
        Path valid = zip("tb_grupo.txt");
        Path extracted = processor.extract(valid);
        try { assertEquals("data", Files.readString(extracted.resolve("tb_grupo.txt"))); }
        finally { processor.deleteDirectory(extracted); }
        assertThrows(IOException.class, () -> processor.extract(zip("../escape.txt")));
    }

    @Test void failedTransferRemovesPartialFile() throws Exception {
        SigtapFileProcessor tracked = spy(processor);
        when(ftp.retrieveFile(eq(name), any(OutputStream.class))).thenReturn(false);
        assertThrows(IOException.class, () -> tracked.download(name));
        var directory = org.mockito.ArgumentCaptor.forClass(Path.class);
        verify(tracked).deleteDirectory(directory.capture());
        assertFalse(Files.exists(directory.getValue()));
    }

    FTPFile file(String name) {
        FTPFile f = new FTPFile();
        f.setName(name);
        f.setType(FTPFile.FILE_TYPE);
        return f;
    }

    Path zip(String entry) throws Exception {
        Path path = Files.createTempFile(temp, "fixture", ".zip");
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(path))) {
            out.putNextEntry(new ZipEntry(entry));
            out.write("data".getBytes());
            out.closeEntry();
        }
        return path;
    }
}
