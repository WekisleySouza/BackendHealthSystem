package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.utils.SpecificationsUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ServiceTypeSpecs {
    public static Specification<ServiceType> codeLike(String code) {
        if (code == null || code.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("sigtapCode")),
                        "%" + code.trim().toUpperCase() + "%"
                );
    }

    public static Specification<ServiceType> nameLike(String name) {
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

    public static Specification<ServiceType> valueEqual(BigDecimal value) {
        if (value == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("value"), value);
    }

    public static Specification<ServiceType> typeLike(String type) {
        if (type == null || type.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(type);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("type"))
                        ),
                        "%" + normalized + "%"
                );
    }

}
