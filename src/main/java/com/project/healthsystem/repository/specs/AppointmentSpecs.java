package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.Priority;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.model.Status;
import com.project.healthsystem.utils.SpecificationsUtils;
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

        String normalized = SpecificationsUtils.normalize(notes);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("notes"))
                        ),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> scheduledAtEqual(LocalDateTime scheduledAt) {
        if (scheduledAt == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("scheduledAt"), scheduledAt);
    }

    public static Specification<Appointment> scheduledAtBetween(
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start == null && end == null) return null;

        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("scheduledAt"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("scheduledAt"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("scheduledAt"), end);
            }
        };
    }

    public static Specification<Appointment> createdAtEqual(LocalDateTime createdAt) {
        if (createdAt == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("createdAt"), createdAt);
    }

    public static Specification<Appointment> createdAtBetween(
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start == null && end == null) return null;

        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            }
        };
    }

    public static Specification<Appointment> schedulingForecastEqual(LocalDateTime schedulingForecast) {
        if (schedulingForecast == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("schedulingForecast"), schedulingForecast);
    }

    public static Specification<Appointment> schedulingForecastBetween(
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start == null && end == null) return null;

        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("scheduling_forecast"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("scheduling_forecast"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("scheduling_forecast"), end);
            }
        };
    }

    public static Specification<Appointment> prioritLike(String priorit) {
        if (priorit == null || priorit.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("priorit")),
                        "%" + Priority.fromLabel(priorit).name().trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> statusLike(String status) {
        if (status == null || status.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("status").as(String.class)),
                        "%" + Status.fromLabel(status).name().trim().toUpperCase() + "%"
                );
    }

    public static Specification<Appointment> serviceTypeLike(String type) {
        if (type == null || type.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(
                ServiceTypes.fromLabel(type).name()
        );

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("serviceType").get("type").as(String.class)),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> serviceTypeNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("serviceType").get("name"))
                        ),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> serviceTypeCategoryGroupNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(
                                        root.get("serviceType")
                                                .get("categoryGroup")
                                                .get("name")
                                )
                        ),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> professionalNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("professional").get("person").get("name"))
                        ),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> employeeNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("employee").get("person").get("name"))
                        ),
                        "%" + normalized + "%"
                );
    }

    public static Specification<Appointment> patientNameLike(String name) {
        if (name == null || name.isBlank()) return null;

        String normalized = SpecificationsUtils.normalize(name);

        return (root, query, cb) ->
                cb.like(
                        cb.function("unaccent", String.class,
                                cb.upper(root.get("patient").get("person").get("name"))
                        ),
                        "%" + normalized + "%"
                );
    }

}
