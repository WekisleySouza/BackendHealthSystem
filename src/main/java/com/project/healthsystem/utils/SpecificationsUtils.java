package com.project.healthsystem.utils;

import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;

public class SpecificationsUtils {

    public static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .toUpperCase()
            .trim();
    }

    public static String removeAccents(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static <T> Specification<T> likeIgnoreAccents(String field, String value) {
        if (value == null || value.isBlank()) return null;

        String normalized = normalize(value);

        return (root, query, cb) -> cb.like(
                cb.function("unaccent", String.class,
                        cb.upper(root.get(field))
                ),
                "%" + normalized + "%"
        );
    }

    public static <T> Specification<T> likeIgnoreAccentsNested(String field, String nestedField, String value) {
        if (value == null || value.isBlank()) return null;

        String normalized = normalize(value);

        return (root, query, cb) -> cb.like(
                cb.function("unaccent", String.class,
                        cb.upper(root.get(field).get(nestedField))
                ),
                "%" + normalized + "%"
        );
    }
}
