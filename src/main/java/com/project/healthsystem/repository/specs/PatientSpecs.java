package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Patient;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientSpecs {
    public static Specification<Patient> teamNameLike(String teamName) {
        return (root, query, cb) ->
            SpecsCommon.likeTokens(
                cb,
                root.get("teamName"),
                teamName
            );
    }

    public static Specification<Patient> teamINELike(String teamINE) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("teamINE"),
                teamINE
            );
    }

    public static Specification<Patient> microAreaLike(String microArea) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("microArea"),
                microArea
            );
    }

    public static Specification<Patient> originLike(String origin) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("origin"),
                origin
            );
    }

    public static Specification<Patient> sexEqual(String sexLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeTokens(
                        criteriaBuilder,
                        root.get("person").get("sex"),
                        sexLabel
                );
    }

    public static Specification<Patient> cellPhoneLike(String cellPhone) {
        if (cellPhone == null || cellPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cellPhone")),
                        "%" + cellPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> residentialPhoneLike(String residentialPhone) {
        if (residentialPhone == null || residentialPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("residentialPhone")),
                        "%" + residentialPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> contactPhoneLike(String contactPhone) {
        if (contactPhone == null || contactPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("contactPhone")),
                        "%" + contactPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> cnsLike(String cns) {
        if (cns == null || cns.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cns")),
                        "%" + cns.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> nameLike(String name) {
        return (root, query, cb) ->
            SpecsCommon.likeTokens(
                cb,
                root.get("person").get("name"),
                name
            );
    }

    public static Specification<Patient> motherNameLike(String motherName) {
        return (root, query, cb) ->
            SpecsCommon.likeTokens(
                cb,
                root.get("motherName"),
                motherName
            );
    }

    public static Specification<Patient> genderEqual(String genderLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeTokens(
                        criteriaBuilder,
                        root.get("person").get("gender"),
                        genderLabel
                );
    }

    public static Specification<Patient> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> birthdayEqual(LocalDate birthday) {
        if (birthday == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("person").get("birthday"), birthday);
    }

    public static Specification<Patient> emailLike(String email) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("person").get("email"),
                email
            );
    }

}
