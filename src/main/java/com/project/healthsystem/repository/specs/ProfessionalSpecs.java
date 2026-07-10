package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Professional;
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
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("vinculation"),
                vinculation
            );
    }

    public static Specification<Professional> descriptionLike(String description) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("description"),
                description
            );
    }

    public static Specification<Professional> sexEqual(String sexLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        criteriaBuilder,
                        root.get("person").get("sex"),
                        sexLabel
                );
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
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("person").get("name"),
                name
            );
    }

    public static Specification<Professional> genderEqual(String genderLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        criteriaBuilder,
                        root.get("person").get("gender"),
                        genderLabel
                );
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
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("person").get("email"),
                email
            );
    }

}
