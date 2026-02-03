package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Patient;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PatientSpecs {
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

    public static Specification<Patient> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Patient> phoneLike(String phone) {
        if (phone == null || phone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("phone")),
                        "%" + phone.trim().toUpperCase() + "%"
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
