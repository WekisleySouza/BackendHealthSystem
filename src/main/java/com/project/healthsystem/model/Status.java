package com.project.healthsystem.model;

public enum Status {
    COMPLETED("completo"),
    CREATED("Criado");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Status fromLabel(String label) {
        for (Status s : values()) {
            if (s.getLabel().equalsIgnoreCase(label)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + label);
    }
}
