package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

public class CategoryGroupSpecs {
    public static Specification<CategoryGroup> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                criteriaBuilder,
                root.get("name"),
                name
            );
    }

}
