package com.project.healthsystem.model;

import com.project.healthsystem.exceptions.InvalidDataException;

public enum RequestCharacter {
    ELECTIVE("Eletivo"),
    URGENT("Urgente");

    private final String label;

    RequestCharacter(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RequestCharacter fromLabel(String label) {
        if (label == null) return null;
        for (RequestCharacter value : values()) {
            if (value.label.equalsIgnoreCase(label)) return value;
        }
        throw new InvalidDataException("Valor inválido para RequestCharacter: " + label);
    }
}
