package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.ServiceType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ServiceTypeSpecs {
    public static Specification<ServiceType> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<ServiceType> valueEqual(BigDecimal value){
        if(value == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("value"), value);
    }

    public static Specification<ServiceType> typeEqual(String type){
        if(type == null || type.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("type"), type);
    }
}
