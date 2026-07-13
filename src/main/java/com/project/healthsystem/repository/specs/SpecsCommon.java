package com.project.healthsystem.repository.specs;

import com.project.healthsystem.utils.SpecificationsUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return Arrays.stream(
                SpecificationsUtils.normalize(text).split("\\s+")
            )
            .filter(token -> token.length() > 2)
            .toList();
    }

    public static Predicate likeTokens(
            CriteriaBuilder cb,
            Expression<String> field,
            String value
    ) {

        List<String> tokens = tokenize(value);

        if (tokens.isEmpty()) {
            return cb.conjunction();
        }

        Expression<String> normalizedField = cb.function(
                "unaccent",
                String.class,
                field
        );

        List<Predicate> predicates = new ArrayList<>();

        for (String token : tokens) {
            predicates.add(
                    cb.like(
                            normalizedField,
                            "%" + token.toUpperCase() + "%"
                    )
            );
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
