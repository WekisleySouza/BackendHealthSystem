package com.project.healthsystem.model.datasus_data.sigtap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sigtap_organizations")
@Data
@NoArgsConstructor
public class SigtapOrganization extends IDAbstraction {
  private String code;
  private String name;
  private String competence;
  
  @ManyToOne
  private SigtapSubGroup subGroup;
  
  
  public static SigtapOrganization fromLine(String line) {
    SigtapOrganization organization = new SigtapOrganization();
    
    String groupCode = line.substring(0, 2);
    String subGroupCode = line.substring(2, 4);
    
    organization.code = line.substring(4, 6);
    organization.name = line.substring(6, 106).trim();
    organization.competence = line.substring(106, 112);
    
    SigtapGroup group = new SigtapGroup();
    group.setCode(groupCode);
    
    SigtapSubGroup subGroup = new SigtapSubGroup();
    subGroup.setCode(subGroupCode);
    subGroup.setGroup(group);
    
    organization.subGroup = subGroup;
    
    return organization;
  }
}
