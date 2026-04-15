package com.project.healthsystem.repository.specs;

import com.project.healthsystem.utils.SpecificationsUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SpecsCommon {

    public static  <T> Specification<T> addSpec(Specification<T> base, Specification<T> next) {
        return (base == null) ? next : base.and(next);
    }

    public static Predicate likeIgnoreCaseUnaccent(
        CriteriaBuilder cb,
        Expression<String> field,
        String value
    ){
        // Mudar para cb.conjunction no futuro para evitar problemas
        if (value == null || value.isBlank()) return null;
        String normalized = SpecificationsUtils.normalize(value);

        return cb.like(
            cb.function(
            "unaccent",
                String.class,
                cb.upper(field)
            ),
            "%" + normalized + "%"
        );
    }
}
