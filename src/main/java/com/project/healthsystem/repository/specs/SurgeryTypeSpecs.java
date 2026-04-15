package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

public class SurgeryTypeSpecs {
    public static Specification<SurgeryType> typeLike(String type) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("type"),
                type
            );
    }
}
