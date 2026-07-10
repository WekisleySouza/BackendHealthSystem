package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Agent;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AgentSpecs {

    public static Specification<Agent> sexEqual(String sexLabel) {
        return (root, query, criteriaBuilder) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                criteriaBuilder,
                root.get("person").get("sex"),
                sexLabel
            );
    }

    public static Specification<Agent> genderEqual(String genderLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        criteriaBuilder,
                        root.get("person").get("gender"),
                        genderLabel
                );
    }

    public static Specification<Agent> cellPhoneLike(String cellPhone) {
        if (cellPhone == null || cellPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cellPhone")),
                        "%" + cellPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> residentialPhoneLike(String residentialPhone) {
        if (residentialPhone == null || residentialPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("residentialPhone")),
                        "%" + residentialPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> contactPhoneLike(String contactPhone) {
        if (contactPhone == null || contactPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("contactPhone")),
                        "%" + contactPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> nameLike(String name) {
        return (root, query, criteriaBuilder) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                criteriaBuilder,
                root.get("person").get("name"),
                name
            );
    }

    public static Specification<Agent> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> birthdayEqual(LocalDate birthday) {
        if (birthday == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("person").get("birthday"), birthday);
    }

    public static Specification<Agent> emailLike(String email) {
        return (root, query, criteriaBuilder) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                criteriaBuilder,
                root.get("person").get("email"),
                email
            );
    }

}
