package com.project.healthsystem.unit.model.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapSubGroup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SigtapSubGroupGroupRepositoryTest {
  
  @Test
  void shouldParseSigtapSubGroup() throws IOException {
    List<String> lines = Files.readAllLines(
      Path.of("D:/Wekisley/Trabalho/APIs Saúde Pública/Testes/SIGTAP/TabelaUnificada_202608_v2608141139/tb_sub_grupo.txt"),
      StandardCharsets.ISO_8859_1
    );
    
    String line = lines.get(0);
    
    assertEquals(110, line.length());
    
    SigtapSubGroup subGroup = SigtapSubGroup.fromLine(line);
    
    assertEquals("09", subGroup.getGroup().getCode());
    assertEquals("01", subGroup.getCode());
    assertEquals("Atenção em Oncologia", subGroup.getName());
    assertEquals("202608", subGroup.getCompetence());
  }
}
