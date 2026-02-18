package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.model.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PatientSpecs {
    public static Specification<Patient> teamNameLike(String teamName) {
        if (teamName == null || teamName.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("teamName")),
                        "%" + teamName.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> teamINELike(String teamINE) {
        if (teamINE == null || teamINE.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("teamINE")),
                        "%" + teamINE.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> microAreaLike(String microArea) {
        if (microArea == null || microArea.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("microArea")),
                        "%" + microArea.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> originLike(String origin) {
        if (origin == null || origin.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("origin")),
                        "%" + origin.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> sexEqual(String sexLabel) {
        if (sexLabel == null || sexLabel.isBlank()) return null;

        Sex sex = Sex.fromLabel(sexLabel);

        return (root, query, cb) ->
                cb.equal(root.get("person").get("sex"), sex);
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

    public static Specification<Patient> motherNameLike(String motherName) {
        if (motherName == null || motherName.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("motherName")),
                        "%" + motherName.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> genderEqual(String genderLabel) {
        if (genderLabel == null || genderLabel.isBlank()) return null;

        Gender gender = Gender.fromLabel(genderLabel);

        return (root, query, cb) ->
                cb.equal(root.get("person").get("gender"), gender);
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
        if (email == null || email.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("email")),
                        "%" + email.trim().toUpperCase() + "%"
                );
    }

}
