package com.project.healthsystem.model;

public enum Priority {
    ELECTIVE("Eletivo"),
    PRIORITY("Prioritário"),
    URGENT("Urgente");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Priority fromLabel(String label) {
        for (Priority r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Prioridade inválida: " + label);
    }
}
