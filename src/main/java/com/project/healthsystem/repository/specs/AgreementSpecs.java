package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Agreement;
import org.springframework.data.jpa.domain.Specification;

public class AgreementSpecs {

    public static Specification<Agreement> nameLike(String name) {
        return (root, query, cb) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        cb,
                        root.get("name"),
                        name
                );
    }
}
