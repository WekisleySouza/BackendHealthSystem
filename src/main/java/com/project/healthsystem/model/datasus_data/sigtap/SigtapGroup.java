package com.project.healthsystem.model.datasus_data.sigtap;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_sigtap_groups")
@Data
@NoArgsConstructor
public class SigtapGroup extends IDAbstraction {
  private String code;
  private String name;
  private String competence;
  
  public static SigtapGroup fromLine(String line) {
    SigtapGroup group = new SigtapGroup();
    
    group.code = line.substring(0, 2);
    group.name = line.substring(2, 102).trim();
    group.competence = line.substring(102, 108);
    
    return group;
  }
}
