package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.ServiceType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ServiceTypeSpecs {
    public static Specification<ServiceType> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<ServiceType> valueEqual(BigDecimal value) {
        if (value == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("value"), value);
    }

    public static Specification<ServiceType> typeLike(String type) {
        if (type == null || type.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("type")),
                        "%" + type.trim().toUpperCase() + "%"
                );
    }

}
