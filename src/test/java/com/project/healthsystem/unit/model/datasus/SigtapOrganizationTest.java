package com.project.healthsystem.unit.model.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapOrganization;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SigtapOrganizationTest {
  
  
  @Test
  void shouldParseSigtapOrganization() throws IOException {
    List<String> lines = Files.readAllLines(
      Path.of("D:/Wekisley/Trabalho/APIs Saúde Pública/Testes/SIGTAP/TabelaUnificada_202608_v2608141139/tb_forma_organizacao.txt"),
      StandardCharsets.ISO_8859_1
    );
    
    String line = lines.get(0);
    
    assertEquals(112, line.length());
    
    SigtapOrganization organization = SigtapOrganization.fromLine(line);
    
    assertEquals("09", organization.getSubGroup().getGroup().getCode());
    assertEquals("03", organization.getSubGroup().getCode());
    assertEquals("01", organization.getCode());
    assertEquals("Ofertas de Cuidados Integrados em Ortopedia", organization.getName());
    assertEquals("202608", organization.getCompetence());
  }

}
