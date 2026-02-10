package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Appointment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AppointmentSpecs {

    // Reports:
    public static Specification<Appointment> appointmentReport() {
        return (root, query, cb) -> {

            root.join("patient").join("person");
            root.join("professional").join("person");

            query.orderBy(cb.desc(root.get("scheduledAt")));

            return cb.conjunction();
        };
    }

    // Other specifications:

    public static Specification<Appointment> notesLike(String notes) {
        if (notes == null || notes.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("notes")),
                        "%" + notes.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> scheduledAtEqual(LocalDateTime scheduledAt) {
        if (scheduledAt == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("scheduledAt"), scheduledAt);
    }

    public static Specification<Appointment> createdAtEqual(LocalDateTime createdAt) {
        if (createdAt == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("createdAt"), createdAt);
    }

    public static Specification<Appointment> prioritLike(String priorit) {
        if (priorit == null || priorit.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("priorit")),
                        "%" + priorit.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> statusLike(String status) {
        if (status == null || status.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("status").as(String.class)),
                        "%" + status.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> serviceTypeLike(String type) {
        if (type == null || type.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("serviceType").get("type").as(String.class)),
                        "%" + type.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> professionalNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("professional").get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> employeeNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("employee").get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> patientNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("patient").get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

}
