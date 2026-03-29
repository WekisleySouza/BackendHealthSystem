package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Condition;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

public class ConditionSpecs {

    public static Specification<Condition> specificationLike(String specification) {
        if (specification == null || specification.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(specification);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("specification"))
                        ),
                        "%" + normalized + "%"
                );
    }

}
