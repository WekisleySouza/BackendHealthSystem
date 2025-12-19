package com.project.healthsystem.model;

import com.project.healthsystem.exceptions.InvalidDataException;

public enum Status {
    PENDING_SCHEDULING("Sem agendamento"),
    SCHEDULED("Agendado"),
    COMPLETED("Completo"),
    CANCELED("Cancelado"),
    NO_SHOW("Paciente faltou"),
    OVERDUE("Atrasado");

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
        throw new InvalidDataException("Status inválido: " + label);
    }
}
