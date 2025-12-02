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

    public static Specification<Surgery> personNameEqual(String personName) {
        if (personName == null || personName.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("personName")), "%" + personName.toUpperCase() + "%");
    }

    public static Specification<Surgery> surgeryRiskEqual(String surgeryRisk) {
        if (surgeryRisk == null || surgeryRisk.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.get("surgeryRisk"), surgeryRisk);
    }

    public static Specification<Surgery> locationEqual(String location) {
        if (location == null || location.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("location")), "%" + location.toUpperCase() + "%");
    }

    public static Specification<Surgery> conclusionEqual(String conclusion) {
        if (conclusion == null || conclusion.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("conclusion")), "%" + conclusion.toUpperCase() + "%");
    }

    public static Specification<Surgery> susEasyEqual(String susEasy) {
        if (susEasy == null || susEasy.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.get("susEasy"), susEasy);
    }

    public static Specification<Surgery> sesapEqual(String sesap) {
        if (sesap == null || sesap.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.get("sesap"), sesap);
    }

    public static Specification<Surgery> procedureDateEqual(LocalDate procedureDate) {
        if (procedureDate == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("procedureDate"), procedureDate);
    }

    public static Specification<Surgery> anesthesicRiskEqual(String anesthesicRisk) {
        if (anesthesicRisk == null || anesthesicRisk.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(root.get("anesthesicRisk"), anesthesicRisk);
    }

    public static Specification<Surgery> observationEqual(String observation) {
        if (observation == null || observation.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("observation")), "%" + observation.toUpperCase() + "%");
    }

    public static Specification<Surgery> surgeryTypeIdEqual(Long id) {
        if (id == null) return null;
        return (root, query, cb) ->
                cb.equal(root.get("surgeryType").get("id"), id);
    }
}
