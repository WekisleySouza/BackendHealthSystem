package com.project.healthsystem.model.datasus_data.cnes;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tb_cnes_professionals")
@Data
@NoArgsConstructor
public class CnesProfessional extends IDAbstraction {
  private String name;
  private String cns;
  private String cityName;
  private String cbo;
  private String cboDescription;
  private String cnpj;
  private String cnes;
  private String estabilishmentName;
  
  private String legalNatureCode;
  private String legalNatureDescription;
  
  private Boolean sus;
  private Boolean resident;
  private Boolean preceptor;
  private String management;
  
  private String estabilishmentBond;
  private String employerBond;
  private String bondDetails;
  
  public static CnesProfessional fromFields(String[] fields) {

    
    if (fields.length != 17) {
      throw new IllegalArgumentException(
        "Invalid professional line. Expected 17 fields, found " + fields.length
      );
    }
    
    CnesProfessional professional = new CnesProfessional();
    
    professional.name = fields[0].trim();
    professional.cns = fields[1].trim();
    professional.cityName = fields[2].trim();
    professional.cbo = fields[3].trim();
    professional.cboDescription = fields[4].trim();
    professional.cnpj = fields[5].trim();
    professional.cnes = fields[6].trim();
    professional.estabilishmentName = fields[7].trim();
    
    professional.legalNatureCode = fields[8].trim();
    professional.legalNatureDescription = fields[9].trim();
    
    professional.sus = "S".equalsIgnoreCase(fields[10].trim());
    professional.resident = "S".equalsIgnoreCase(fields[11].trim());
    professional.preceptor = "S".equalsIgnoreCase(fields[12].trim());
    
    String management = fields[13].trim();
    professional.management = switch (management) {
      case "M" -> "Municipal";
      case "E" -> "Estadual";
      case "F" -> "Federal";
      default -> management;
    };
    
    professional.estabilishmentBond = fields[14].trim();
    professional.employerBond = fields[15].trim();
    professional.bondDetails = fields[16].trim();
    
    return professional;
  }
  
  @Override
  public String toString() {
    return "CnesProfessional {" +
      "\n  name='" + name + '\'' +
      ",\n  cns='" + cns + '\'' +
      ",\n  cityName='" + cityName + '\'' +
      ",\n  cbo='" + cbo + '\'' +
      ",\n  cboDescription='" + cboDescription + '\'' +
      ",\n  cnpj='" + cnpj + '\'' +
      ",\n  cnes='" + cnes + '\'' +
      ",\n  estabilishmentName='" + estabilishmentName + '\'' +
      ",\n  legalNatureCode='" + legalNatureCode + '\'' +
      ",\n  legalNatureDescription='" + legalNatureDescription + '\'' +
      ",\n  sus=" + sus +
      ",\n  resident=" + resident +
      ",\n  preceptor=" + preceptor +
      ",\n  management='" + management + '\'' +
      ",\n  estabilishmentBond='" + estabilishmentBond + '\'' +
      ",\n  employerBond='" + employerBond + '\'' +
      ",\n  bondDetails='" + bondDetails + '\'' +
      "\n}";
  }
}
