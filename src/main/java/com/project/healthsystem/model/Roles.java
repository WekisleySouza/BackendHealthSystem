package com.project.healthsystem.model;

import java.util.ArrayList;
import java.util.List;

public enum Roles {
    API("API"),
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    EMPLOYEE("EMPLOYEE"),
    PATIENT("PATIENT");

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

    public static List<Roles> fromLabel(List<String> labels){
        List<Roles> roles = new ArrayList<>();
        for(String label : labels){
            roles.add(fromLabel(label));
        }
        return roles;
    }

    public static boolean containsRole(List<Roles> roles, Roles role){
        for(Roles currentRole : roles){
            if(currentRole.getLabel().equals(role.getLabel())){
                return true;
            }
        }
        return false;
    }
}
