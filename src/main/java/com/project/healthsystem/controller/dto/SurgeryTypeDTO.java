package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.SurgeryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryTypeDTO {
    private long id;
    private String type;

    public SurgeryTypeDTO(SurgeryType surgeryType){
        this.id = surgeryType.getId();
        this.type = surgeryType.getType();
    }

    public SurgeryType mappingToSurgeryType(){
        return new SurgeryType(this.type);
    }
}
