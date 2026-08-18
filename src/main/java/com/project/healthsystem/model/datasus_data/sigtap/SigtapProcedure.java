package com.project.healthsystem.model.datasus_data.sigtap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sigtap_procedures")
@Data
@NoArgsConstructor
public class SigtapProcedure extends IDAbstraction {
  private String code;
  private String name;
  private String complexity;
  private String sex;
  private Integer minimumAge;
  private Integer maximumAge;
  private String competence;
  
  @ManyToOne
  private SigtapOrganization organization;
  
  public static SigtapProcedure fromLine(String line) {
    if (line == null || line.length() < 336) {
      throw new IllegalArgumentException(
        "Linha inválida do SIGTAP: tamanho esperado: 336"
      );
    }
    
    SigtapProcedure procedure = new SigtapProcedure();
    String groupCode = line.substring(0, 2);
    String subGroupCode = line.substring(2, 4);
    String organizationCode = line.substring(4, 6).trim();
    
    procedure.code = line.substring(0, 10).trim();
    procedure.name = line.substring(10, 260).trim();
    procedure.complexity = line.substring(260, 261).trim();
    procedure.sex = line.substring(261, 262).trim();
    procedure.minimumAge = parseInteger(line.substring(274, 278));
    procedure.maximumAge = parseInteger(line.substring(278, 282));
    procedure.competence = line.substring(330, 336).trim();
    
    SigtapGroup group = new SigtapGroup();
    group.setCode(groupCode);
    
    SigtapSubGroup subGroup = new SigtapSubGroup();
    subGroup.setCode(subGroupCode);
    subGroup.setGroup(group);
    
    SigtapOrganization organization = new SigtapOrganization();
    organization.setCode(organizationCode);
    organization.setSubGroup(subGroup);
    
    procedure.setOrganization(organization);
    
    return procedure;
  }
  
  private static Integer parseInteger(String value) {
    String trimmedValue = value.trim();
    
    if (trimmedValue.isEmpty()) {
      return null;
    }
    
    return Integer.valueOf(trimmedValue);
  }
}
