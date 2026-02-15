package com.project.healthsystem.model;

public enum Sex {
    MALE("Masculino"),
    UNKNOW("Indeterminado"),
    FEMALE("Feminino");

    private final String label;

    Sex(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Sex fromLabel(String label) {
        for (Sex r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Sexo inválido: " + label);
    }
}
