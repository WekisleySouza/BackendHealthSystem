package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.SurgeryType;
import org.springframework.data.jpa.domain.Specification;

public class SurgeryTypeSpecs {

    public static Specification<SurgeryType> typeEqual(String type) {
        if (type == null || type.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("type")), "%" + type.toUpperCase() + "%");
    }
}
