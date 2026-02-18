package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecs {

    public static Specification<Employee> sexEqual(String sexLabel) {
        if (sexLabel == null || sexLabel.isBlank()) return null;

        Sex sex = Sex.fromLabel(sexLabel);

        return (root, query, cb) ->
                cb.equal(root.get("sex"), sex);
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
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Employee> genderEqual(String genderLabel) {
        if (genderLabel == null || genderLabel.isBlank()) return null;

        Gender gender = Gender.fromLabel(genderLabel);

        return (root, query, cb) ->
                cb.equal(root.get("person").get("gender"), gender);
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
        if (email == null || email.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("email")),
                        "%" + email.trim().toUpperCase() + "%"
                );
    }
}
