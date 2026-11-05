package com.project.healthsystem.service.datasus;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SigtapFileProcessor {
  private static final String FTP_HOST = "ftp2.datasus.gov.br";
  private static final String FTP_DIRECTORY = "/pub/sistemas/tup/downloads/";
  private static final String FTP_URL = "ftp://ftp2.datasus.gov.br/pub/sistemas/tup/downloads/";
  
  public Path downloadLatest() throws IOException {
    
    FTPClient ftp = new FTPClient();
    
    try {
      ftp.connect(FTP_HOST);
      ftp.login("anonymous", "");
      
      ftp.enterLocalPassiveMode();
      
      ftp.changeWorkingDirectory(FTP_DIRECTORY);
      
      FTPFile[] files = ftp.listFiles();
      
      FTPFile latest = Arrays.stream(files)
        .filter(FTPFile::isFile)
        .filter(file -> file.getName().startsWith("TabelaUnificada_"))
        .filter(file -> file.getName().endsWith(".zip"))
        .max(Comparator.comparing(FTPFile::getName))
        .orElseThrow(() ->
          new FileNotFoundException("Nenhum arquivo SIGTAP encontrado.")
        );
      
      Path destination = Files.createTempFile(
        "sigtap-",
        ".zip"
      );
      
      try (OutputStream output = Files.newOutputStream(destination)) {
        
        boolean success = ftp.retrieveFile(
          latest.getName(),
          output
        );
        
        if (!success) {
          throw new IOException(
            "Falha ao baixar " + latest.getName()
          );
        }
      }
      
      return destination;
      
    } finally {
      if (ftp.isConnected()) {
        ftp.logout();
        ftp.disconnect();
      }
    }
  }
  
  public Path download(String fileName) throws IOException {
    
    Path destination = Files.createTempFile(
      "sigtap-",
      ".zip"
    );
    
    URI uri = URI.create(FTP_URL + fileName);
    
    try (InputStream inputStream = uri.toURL().openStream()) {
      Files.copy(
        inputStream,
        destination,
        StandardCopyOption.REPLACE_EXISTING
      );
    }
    
    return destination;
  }
  
  public Path extract(Path zipPath) throws IOException {
    Path destination = Files.createTempDirectory("sigtap-");
    
    try (ZipInputStream zipInputStream =
           new ZipInputStream(Files.newInputStream(zipPath))) {
      
      ZipEntry entry;
      
      while ((entry = zipInputStream.getNextEntry()) != null) {
        
        if (entry.isDirectory()) {
          continue;
        }
        
        Path output = destination.resolve(entry.getName()).normalize();
        
        if (!output.startsWith(destination)) {
          throw new IOException("Invalid ZIP entry: " + entry.getName());
        }
        
        Files.createDirectories(output.getParent());
        
        Files.copy(
          zipInputStream,
          output,
          StandardCopyOption.REPLACE_EXISTING
        );
      }
    }
    
    return destination;
  }
  
  public void deleteZip(Path zipPath) throws IOException {
    Files.deleteIfExists(zipPath);
  }
  
  public void deleteDirectory(Path directory) throws IOException {
    if(!Files.exists(directory)){
      return;
    }
    
    try(Stream<Path> paths = Files.walk(directory)){
      paths.sorted(Comparator.reverseOrder())
        .forEach(path -> {
          try{
            Files.delete(path);
          } catch(IOException e){
            throw new UncheckedIOException(e);
          }
        });
    } catch(UncheckedIOException e){
      throw e.getCause();
    }
  }
}
