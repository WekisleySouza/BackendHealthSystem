package com.project.healthsystem.unit.model.datasus;

import com.project.healthsystem.model.datasus_data.sigtap.SigtapGroup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SigtapGroupRepositoryTest {
  
  @Test
  void shouldParseSigtapGroup() throws IOException {
    List<String> lines = Files.readAllLines(
      Path.of("D:/Wekisley/Trabalho/APIs Saúde Pública/Testes/SIGTAP/TabelaUnificada_202608_v2608141139/tb_grupo.txt"),
      StandardCharsets.ISO_8859_1
    );
    
    String line = lines.get(0);
    
    assertEquals(108, line.length());
    
    SigtapGroup group = SigtapGroup.fromLine(line);
    
    assertEquals("01", group.getCode());
    assertEquals("Ações de promoção e prevenção em saúde", group.getName());
    assertEquals("202608", group.getCompetence());
  }
}
