package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AppointmentSpecs {
    public static Specification<Appointment> notesEqual(String notes){
        if(notes == null || notes.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("notes"), notes);
    }

    public static Specification<Appointment> scheduledAtEqual(LocalDateTime scheduledAt){
        if(scheduledAt == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("scheduledAt"), scheduledAt);
    }

    public static Specification<Appointment> createdAtEqual(LocalDateTime createdAt){
        if(createdAt == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("createdAt"), createdAt);
    }

    public static Specification<Appointment> prioritEqual(String priorit){
        if(priorit == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("priorit"), priorit);
    }

    public static Specification<Appointment> statusEqual(String status){
        if(status == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("status"), Status.fromLabel(status));
    }
}
