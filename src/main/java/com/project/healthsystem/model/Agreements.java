package com.project.healthsystem.model;

public enum Agreements {
    PRIVATE("Particular"),
    INTERN("Interno"),
    CISALV("CISALV"),
    UNDEFINED("Indefinido");

    private final String label;

    Agreements(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static Agreements fromLabel(String label) {
        for (Agreements r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Tipo de responsável inválido: " + label);
    }
}
