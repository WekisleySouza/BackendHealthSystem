package com.project.healthsystem.utils;

import java.text.Normalizer;

public class SpecificationsUtils {

    public static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[\\-–—]", " ")
            .replaceAll("\\s+", " ")
            .toUpperCase()
            .trim();
    }
}
