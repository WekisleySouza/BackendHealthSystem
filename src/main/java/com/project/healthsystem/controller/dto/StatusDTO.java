package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {

    private long id;
    private String specification;

    public StatusDTO(Status status){
        this.id = status.getId();
        this.specification = status.getSpecification();
    }

    public Status mappingToStatus(){
        return new Status(this.specification);
    }

}
