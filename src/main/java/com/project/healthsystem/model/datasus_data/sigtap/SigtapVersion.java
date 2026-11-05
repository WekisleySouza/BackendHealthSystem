package com.project.healthsystem.model.datasus_data.sigtap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Data
@Table(name = "tb_sigtap_version")
@NoArgsConstructor
@AllArgsConstructor
public class SigtapVersion extends IDAbstraction {
  @Column(nullable = false, unique = true)
  private String competence;
  @Column(nullable = false)
  private String fileName;
  @Column(nullable = false)
  private LocalDate importedAt;
  
  private static final Pattern SIGTAP_FILE_PATTERN =
    Pattern.compile("TabelaUnificada_(\\d{6})_.*");
  
  public SigtapVersion(String competence){
    this.competence = competence;
  }
  
  public static SigtapVersion build(Path localZipFile){
    String fileName = localZipFile.getFileName().toString();
    
    SigtapVersion version = new SigtapVersion();
    
    version.fileName = fileName;
    version.competence = version.extractCompetence(fileName);
    version.importedAt = LocalDate.now();
    
    return version;
  }
  
  private String extractCompetence(String fileName) {
    Matcher matcher = SIGTAP_FILE_PATTERN.matcher(fileName);
    
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
        "Nome de arquivo SIGTAP inválido: " + fileName
      );
    }
    
    return matcher.group(1);
  }
}
