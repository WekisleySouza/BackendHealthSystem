package com.project.healthsystem.unit.model.datasus;


import com.project.healthsystem.model.datasus_data.sigtap.SigtapProcedure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SigtapProcedureTest {
  
  @Test
  void shouldParseSigtapProcedure() throws IOException {
    List<String> lines = Files.readAllLines(
      Path.of("D:/Wekisley/Trabalho/APIs Saúde Pública/Testes/SIGTAP/TabelaUnificada_202608_v2608141139/tb_procedimento.txt"),
      StandardCharsets.ISO_8859_1
    );
    
    String line = lines.get(0);
    
    assertEquals(336, line.length());
    
    SigtapProcedure procedure = SigtapProcedure.fromLine(line);
    
    assertEquals("0101010010", procedure.getCode());
    assertEquals("ATIVIDADE EDUCATIVA / ORIENTAÇÃO EM GRUPO NA ATENÇÃO PRIMÁRIA", procedure.getName());
    assertEquals("1", procedure.getComplexity());
    assertEquals("N", procedure.getSex());
    assertEquals(9999, procedure.getMinimumAge());
    assertEquals(9999, procedure.getMaximumAge());
    assertEquals("202608", procedure.getCompetence());
  }
}
