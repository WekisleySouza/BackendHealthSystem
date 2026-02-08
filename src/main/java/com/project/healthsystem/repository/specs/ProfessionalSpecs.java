package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Gender;
import com.project.healthsystem.model.Professional;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ProfessionalSpecs {
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

    public static Specification<Professional> phoneLike(String phone) {
        if (phone == null || phone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("phone")),
                        "%" + phone.toUpperCase() + "%"
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
