package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.model.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ProfessionalSpecs {

    public static Specification<Professional> cnsLike(String cns) {
        if (cns == null || cns.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cns")),
                        "%" + cns.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> cboLike(String cbo) {
        if (cbo == null || cbo.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cbo")),
                        "%" + cbo.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> vinculationLike(String vinculation) {
        if (vinculation == null || vinculation.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("vinculation")),
                        "%" + vinculation.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> descriptionLike(String description) {
        if (description == null || description.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("description")),
                        "%" + description.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> sexEqual(String sexLabel) {
        if (sexLabel == null || sexLabel.isBlank()) return null;

        Sex sex = Sex.fromLabel(sexLabel);

        return (root, query, cb) ->
                cb.equal(root.get("sex"), sex);
    }

    public static Specification<Professional> cellPhoneLike(String cellPhone) {
        if (cellPhone == null || cellPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cellPhone")),
                        "%" + cellPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> residentialPhoneLike(String residentialPhone) {
        if (residentialPhone == null || residentialPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("residentialPhone")),
                        "%" + residentialPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> contactPhoneLike(String contactPhone) {
        if (contactPhone == null || contactPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("contactPhone")),
                        "%" + contactPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Professional> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("name")),
                        "%" + name.toUpperCase() + "%"
                );
    }

    public static Specification<Professional> genderEqual(String genderLabel) {
        if (genderLabel == null || genderLabel.isBlank()) return null;

        Gender gender = Gender.fromLabel(genderLabel);

        return (root, query, cb) ->
                cb.equal(root.get("person").get("gender"), gender);
    }


    public static Specification<Professional> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.toUpperCase() + "%"
                );
    }

    public static Specification<Professional> birthdayEqual(LocalDate birthday) {
        if (birthday == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("person").get("birthday"), birthday);
    }

    public static Specification<Professional> emailLike(String email) {
        if (email == null || email.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("email")),
                        "%" + email.toUpperCase() + "%"
                );
    }

}
