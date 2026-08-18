package com.project.healthsystem.integration.service;

import com.project.healthsystem.repository.datasus.SigtapGroupRepository;
import com.project.healthsystem.repository.datasus.SigtapOrganizationRepository;
import com.project.healthsystem.repository.datasus.SigtapProcedureRepository;
import com.project.healthsystem.repository.datasus.SigtapSubGroupRepository;
import com.project.healthsystem.service.datasus.SigtapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SigtapServiceTest {
  @Autowired
  private SigtapService sigtapService;
  @Autowired
  private SigtapGroupRepository sigtapGroupRepository;
  @Autowired
  private SigtapSubGroupRepository sigtapSubGroupRepository;
  @Autowired
  private SigtapOrganizationRepository sigtapOrganizationRepository;
  @Autowired
  private SigtapProcedureRepository sigtapProcedureRepository;
  
  @Test
  void shouldImportSigtap() throws IOException {
    sigtapService.importSigtap();
    
    assertTrue(sigtapGroupRepository.count() > 0);
    assertTrue(sigtapSubGroupRepository.count() > 0);
    assertTrue(sigtapOrganizationRepository.count() > 0);
    assertTrue(sigtapProcedureRepository.count() > 0);
  }
}
