package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Condition;
import org.springframework.data.jpa.domain.Specification;

public class ConditionSpecs {

    public static Specification<Condition> specificationLike(String specification) {
        if (specification == null || specification.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("specification")),
                        "%" + specification.trim().toUpperCase() + "%"
                );
    }

}
