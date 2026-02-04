package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Surgery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SurgerySpecs {
    public static Specification<Surgery> dateTimeEqual(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("dateTime"), dateTime);
    }

    public static Specification<Surgery> personNameLike(String personName) {
        if (personName == null || personName.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("personName")),
                        "%" + personName.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> surgeryRiskLike(String surgeryRisk) {
        if (surgeryRisk == null || surgeryRisk.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("surgeryRisk")),
                        "%" + surgeryRisk.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> locationLike(String location) {
        if (location == null || location.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("location")),
                        "%" + location.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> conclusionLike(String conclusion) {
        if (conclusion == null || conclusion.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("conclusion")),
                        "%" + conclusion.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> susEasyLike(String susEasy) {
        if (susEasy == null || susEasy.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("susEasy")),
                        "%" + susEasy.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> sesapLike(String sesap) {
        if (sesap == null || sesap.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("sesap")),
                        "%" + sesap.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> procedureDateEqual(LocalDate procedureDate) {
        if (procedureDate == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("procedureDate"), procedureDate);
    }

    public static Specification<Surgery> anesthesicRiskLike(String anesthesicRisk) {
        if (anesthesicRisk == null || anesthesicRisk.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("anesthesicRisk")),
                        "%" + anesthesicRisk.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> observationLike(String observation) {
        if (observation == null || observation.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("observation")),
                        "%" + observation.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Surgery> surgeryTypeIdEqual(Long id) {
        if (id == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("surgeryType").get("id"), id);
    }

}
