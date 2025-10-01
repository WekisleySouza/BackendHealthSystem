package com.project.healthsystem.model;

import java.util.Arrays;

public enum Roles {
    ADMIN,
    MANAGER,
    USER;

    public static Roles fromString(String value) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(USER);
    }
}
