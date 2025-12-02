package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.CategoryGroup;
import org.springframework.data.jpa.domain.Specification;

public class CategoryGroupSpecs {
    public static Specification<CategoryGroup> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("name"), name);
    }
}
