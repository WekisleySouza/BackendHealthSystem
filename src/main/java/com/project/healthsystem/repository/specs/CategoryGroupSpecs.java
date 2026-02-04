package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.CategoryGroup;
import org.springframework.data.jpa.domain.Specification;

public class CategoryGroupSpecs {
    public static Specification<CategoryGroup> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

}
