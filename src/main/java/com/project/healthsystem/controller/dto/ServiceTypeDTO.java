package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.ServiceType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeDTO {

    private long id;
    private long categoryGroupId;

    private String name;
    private BigDecimal value;
    private String type;

    public ServiceTypeDTO(ServiceType serviceType){
        this.id = serviceType.getId();
        this.categoryGroupId = serviceType.getCategoryGroupId();

        this.name = serviceType.getName();
        this.value = serviceType.getValue();
        this.type = serviceType.getType();
    }

    public ServiceType mappingToServiceType(){
        return new ServiceType(this.id, this.name, this.value, this.type);
    }

    public ServiceType mappingToServiceType(CategoryGroup categoryGroup){
        ServiceType serviceType = new ServiceType(
                this.id,
                this.name,
                this.value,
                this.type
        );
        serviceType.setCategoryGroup(categoryGroup);
        return serviceType;
    }
}
