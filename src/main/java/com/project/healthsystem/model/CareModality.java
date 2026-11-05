package com.project.healthsystem.model;

import com.project.healthsystem.exceptions.InvalidDataException;

public enum CareModality {
    AMBULATORY("Ambulatorial"),
    HOSPITAL("Hospitalar");

    private final String label;

    CareModality(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CareModality fromLabel(String label) {
        if (label == null) return null;
        for (CareModality value : values()) {
            if (value.label.equalsIgnoreCase(label)) return value;
        }
        throw new InvalidDataException("Valor inválido para CareModality: " + label);
    }
}
