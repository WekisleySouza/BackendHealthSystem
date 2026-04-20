package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.ServiceType;
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

    public static Specification<ServiceType> categoryGroupNameLike(String name) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("categoryGroup").get("name"),
                name
            );
    }

    public static Specification<ServiceType> nameLike(String name) {
        return (root, query, cb) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        cb,
                        root.get("name"),
                        name
                );
    }

    public static Specification<ServiceType> valueEqual(BigDecimal value) {
        if (value == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("value"), value);
    }

    public static Specification<ServiceType> typeLike(String type) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("type"),
                type
            );
    }
}
