package com.project.healthsystem.model;

public enum Gender {
    MALE("Homem Cisgênero"),
    MALE_TRANS("Homem Transgênero"),
    FEMALE("Mulher Cisgênero"),
    FEMALE_TRANS("Mulher Transgênero"),
    OTHER("Outro");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Gender fromLabel(String label) {
        for (Gender r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Gênero inválido: " + label);
    }
}
