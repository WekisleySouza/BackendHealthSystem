package com.project.healthsystem.controller.common;

public final class Permissions {
    public static final String ADMIN_OR_MANAGER =
        "hasAnyRole(" +
            "T(com.project.healthsystem.model.Roles).ADMIN.name()," +
            "T(com.project.healthsystem.model.Roles).MANAGER.name()" +
        ")";

    public static final String ADMIN_OR_MANAGER_OR_EMPLOYEE =
        "hasAnyRole(" +
            "T(com.project.healthsystem.model.Roles).ADMIN.name()," +
            "T(com.project.healthsystem.model.Roles).MANAGER.name()," +
            "T(com.project.healthsystem.model.Roles).EMPLOYEE.name()" +
        ")";

    public static final String ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT =
        "hasAnyRole(" +
            "T(com.project.healthsystem.model.Roles).ADMIN.name()," +
            "T(com.project.healthsystem.model.Roles).MANAGER.name()," +
            "T(com.project.healthsystem.model.Roles).MANAGER.name()," +
            "T(com.project.healthsystem.model.Roles).PATIENT.name()" +
        ")";

    public static final String PERMIT_ALL = "permitAll()";
}
