package com.project.healthsystem.model;

public enum ServiceTypes {
    EXAM("EXAM"),
    SPECIALTY("SPECIALTY");

    private final String label;

    ServiceTypes(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ServiceTypes fromLabel(String label) {
        for (ServiceTypes r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Tipo de serviço inválido: " + label);
    }
}
