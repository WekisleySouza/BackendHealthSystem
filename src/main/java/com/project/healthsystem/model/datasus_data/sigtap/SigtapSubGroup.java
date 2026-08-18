package com.project.healthsystem.model.datasus_data.sigtap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sigtap_sub_groups")
@Data
@NoArgsConstructor
public class SigtapSubGroup extends IDAbstraction {
  private String code;
  private String name;
  private String competence;
  
  @ManyToOne
  private SigtapGroup group;
  
  public static SigtapSubGroup fromLine(String line) {
    SigtapSubGroup subGroup = new SigtapSubGroup();
    
    String groupCode = line.substring(0, 2);
    subGroup.code = line.substring(2, 4);
    subGroup.name = line.substring(4, 104).trim();
    subGroup.competence = line.substring(104, 110);
    
    SigtapGroup group = new SigtapGroup();
    group.setCode(groupCode);
    
    subGroup.group = group;
    
    return subGroup;
  }
}
