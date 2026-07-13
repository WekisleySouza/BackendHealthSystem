package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecs {

    public static Specification<Employee> sexEqual(String sexLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeTokens(
                        criteriaBuilder,
                        root.get("person").get("sex"),
                        sexLabel
                );
    }

    public static Specification<Employee> cellPhoneLike(String cellPhone) {
        if (cellPhone == null || cellPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("cellPhone")),
                        "%" + cellPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Employee> residentialPhoneLike(String residentialPhone) {
        if (residentialPhone == null || residentialPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("residentialPhone")),
                        "%" + residentialPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Employee> contactPhoneLike(String contactPhone) {
        if (contactPhone == null || contactPhone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("contactPhone")),
                        "%" + contactPhone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Employee> nameLike(String name) {
        return (root, query, cb) ->
            SpecsCommon.likeTokens(
                cb,
                root.get("person").get("name"),
                name
            );
    }

    public static Specification<Employee> genderEqual(String genderLabel) {
        return (root, query, criteriaBuilder) ->
                SpecsCommon.likeTokens(
                        criteriaBuilder,
                        root.get("person").get("gender"),
                        genderLabel
                );
    }

    public static Specification<Employee> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Employee> birthdayEqual(LocalDate birthday) {
        if (birthday == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("person").get("birthday"), birthday);
    }

    public static Specification<Employee> emailLike(String email) {
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("person").get("email"),
                email
            );
    }
}
