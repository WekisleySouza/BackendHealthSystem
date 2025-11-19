package com.project.healthsystem.model;

public enum Roles {
    ADMIN("Administrador"),
    MANAGER("Gerente"),
    EMPLOYEE("Funcionário"),
    USER("Paciente");

    private final String label;

    Roles(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Roles fromLabel(String label) {
        for (Roles r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + label);
    }
}
