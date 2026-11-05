package com.project.healthsystem.service.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SigtapFileProcessor {
    private static final String FTP_HOST = "ftp2.datasus.gov.br";
    private static final String FTP_DIRECTORY = "/pub/sistemas/tup/downloads/";

    protected FTPClient newClient() { return new FTPClient(); }

    private FTPClient connect() throws IOException {
        FTPClient ftp = newClient();
        ftp.setConnectTimeout(15000);
        ftp.setDefaultTimeout(60000);
        ftp.setDataTimeout(Duration.ofSeconds(60));
        try {
            ftp.connect(FTP_HOST);
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode()) || !ftp.login("anonymous", "")) {
                throw new IOException("Falha na autenticação FTP do DATASUS.");
            }
            ftp.setSoTimeout(60000);
            ftp.enterLocalPassiveMode();
            if (!ftp.setFileType(FTP.BINARY_FILE_TYPE) || !ftp.changeWorkingDirectory(FTP_DIRECTORY)) {
                throw new IOException("Falha ao preparar o download FTP do SIGTAP.");
            }
            return ftp;
        } catch (IOException | RuntimeException e) {
            disconnect(ftp);
            throw e;
        }
    }

    private void disconnect(FTPClient ftp) {
        if (ftp.isConnected()) {
            try { ftp.disconnect(); } catch (IOException ignored) { }
        }
    }

    public String findLatestFileName() throws IOException {
        FTPClient ftp = connect();
        try {
            FTPFile[] files = ftp.listFiles();
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new IOException("Falha ao listar arquivos SIGTAP.");
            }
            return Arrays.stream(files).filter(FTPFile::isFile).map(FTPFile::getName)
                .filter(name -> name.matches("TabelaUnificada_[0-9]{6}_v[0-9]+\\.zip"))
                .filter(name -> {
                    try { SigtapVersion.build(Path.of(name)); return true; }
                    catch (IllegalArgumentException e) { return false; }
                })
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new FileNotFoundException("Nenhum ZIP SIGTAP válido encontrado."));
        } finally {
            disconnect(ftp);
        }
    }

    public Path download(String fileName) throws IOException {
        SigtapVersion.build(Path.of(fileName));
        if (!Path.of(fileName).getFileName().toString().equals(fileName)) {
            throw new IOException("Nome de arquivo SIGTAP inválido.");
        }
        Path directory = Files.createTempDirectory("sigtap-download-");
        Path destination = directory.resolve(fileName);
        FTPClient ftp = null;
        try {
            ftp = connect();
            try (OutputStream output = Files.newOutputStream(destination)) {
                if (!ftp.retrieveFile(fileName, output)) {
                    throw new IOException("Falha ao baixar " + fileName);
                }
            }
            return destination;
        } catch (IOException | RuntimeException e) {
            try { deleteDirectory(directory); } catch (IOException cleanup) { e.addSuppressed(cleanup); }
            throw e;
        } finally {
            if (ftp != null) disconnect(ftp);
        }
    }

    public Path extract(Path zipPath) throws IOException {
        Path destination = Files.createTempDirectory("sigtap-extract-");
        try (ZipInputStream input = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                Path output = destination.resolve(entry.getName()).normalize();
                if (!output.startsWith(destination)) throw new IOException("Invalid ZIP entry: " + entry.getName());
                if (entry.isDirectory()) continue;
                Files.createDirectories(output.getParent());
                Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
            }
            return destination;
        } catch (IOException | RuntimeException e) {
            try { deleteDirectory(destination); } catch (IOException cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
    }

    public void deleteZip(Path zipPath) throws IOException {
        Files.deleteIfExists(zipPath);
        Path parent = zipPath.getParent();
        if (parent != null && parent.getFileName().toString().startsWith("sigtap-download-")) {
            Files.deleteIfExists(parent);
        }
    }

    public void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) return;
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
        }
    }
}
