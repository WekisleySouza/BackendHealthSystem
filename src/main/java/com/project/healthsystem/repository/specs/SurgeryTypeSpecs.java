package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

public class SurgeryTypeSpecs {
    public static Specification<SurgeryType> typeLike(String type) {
        if (type == null || type.isBlank()) return null;

        return (root, query, cb) -> cb.like(
                cb.function("unaccent", String.class,
                        cb.upper(root.get("type"))
                ),
                "%" + SpecificationsUtils.removeAccents(type).toUpperCase() + "%"
        );
    }
}
