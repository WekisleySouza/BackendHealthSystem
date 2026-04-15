package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Condition;
import org.springframework.data.jpa.domain.Specification;

public class ConditionSpecs {

    public static Specification<Condition> specificationLike(String specification) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("specification"),
                specification
            );
    }

}
