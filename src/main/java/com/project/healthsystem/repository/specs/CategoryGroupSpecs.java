package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

public class CategoryGroupSpecs {
    public static Specification<CategoryGroup> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("name"))
                        ),
                        "%" + normalized + "%"
                );
    }

}
