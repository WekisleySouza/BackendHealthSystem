package com.project.healthsystem.unit.service;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapVersion;
import com.project.healthsystem.repository.datasus.*;
import com.project.healthsystem.service.datasus.SigtapFileProcessor;
import com.project.healthsystem.service.datasus.SigtapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SigtapServiceTest {
  
  private final SigtapFileProcessor fileProcessor = new SigtapFileProcessor();
  
  @Mock
  private SigtapGroupRepository sigtapGroupRepository;
  
  @Mock
  private SigtapSubGroupRepository sigtapSubGroupRepository;
  
  @Mock
  private SigtapOrganizationRepository sigtapOrganizationRepository;
  
  @Mock
  private SigtapProcedureRepository sigtapProcedureRepository;
  
  @Mock
  private SigtapVersionRepository sigtapVersionRepository;
  
  @Mock
  private SigtapFileProcessor sigtapFileProcessor;
  
  @Spy
  @InjectMocks
  private SigtapService sigtapService;
  
  
  @Test
  void shouldNotImportSigtapWhenCurrentCompetenceAlreadyExists()
    throws IOException {
    
    String currentCompetence = YearMonth.now()
      .format(DateTimeFormatter.ofPattern("yyyyMM"));
    
    when(sigtapVersionRepository.findFirstByOrderByCompetenceDesc())
      .thenReturn(Optional.of(new SigtapVersion(currentCompetence)));
    
    sigtapService.importSigtapIfNecessary();
    
    verify(sigtapFileProcessor, never()).downloadLatest();
    verify(sigtapVersionRepository, never()).save(any());
  }
  
  
  @Test
  void shouldImportSigtapWhenCurrentCompetenceDoesNotExist()
    throws IOException {
    
    String currentCompetence = YearMonth.now()
      .format(DateTimeFormatter.ofPattern("yyyyMM"));
    
    doNothing()
      .when(sigtapService)
      .importSigtap();
    
    when(sigtapService.findLatestCompetence())
      .thenReturn("202607");
    
    sigtapService.importSigtapIfNecessary();
    
    verify(sigtapService).importSigtap();
  }
  
  @Test
  void shouldDownloadFile() throws IOException {
    Path file = fileProcessor.download("sigtap-setup-1.4.1703301403.exe");
    
    assertNotNull(file);
    assertTrue(Files.exists(file));
    assertTrue(Files.isRegularFile(file));
    assertTrue(Files.size(file) > 0);
  }
  
  @Test
  void shouldDownloadProcedureTable() throws IOException {
    Path file = fileProcessor.download("TabelaUnificada_202608_v2608141139.zip");
    
    assertTrue(Files.exists(file));
    assertTrue(Files.isRegularFile(file));
    assertTrue(Files.size(file) > 0);
  }
  
  @Test
  void shouldExtractSigtapTable() throws IOException {
    
    Path zip = Path.of(
      "D:/Wekisley/Trabalho/APIs Saúde Pública/Testes/SIGTAP/TabelaUnificada_202608_v2608141139.zip"
    );
    
    Path directory = fileProcessor.extract(zip);
    
    assertTrue(Files.exists(directory));
    assertTrue(Files.isDirectory(directory));
    
    assertTrue(Files.exists(
      directory.resolve("tb_procedimento.txt")
    ));
    
    assertTrue(Files.exists(
      directory.resolve("tb_grupo.txt")
    ));
    
    assertTrue(Files.exists(
      directory.resolve("tb_sub_grupo.txt")
    ));
  }
}
