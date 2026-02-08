package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecs {
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

    public static Specification<Employee> phoneLike(String phone) {
        if (phone == null || phone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("phone")),
                        "%" + phone.trim().toUpperCase() + "%"
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
