package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Agent;
import com.project.healthsystem.model.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AgentSpecs {
    public static Specification<Agent> nameLike(String name) {
        if (name == null || name.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> genderEqual(String genderLabel) {
        if (genderLabel == null || genderLabel.isBlank()) return null;

        Gender gender = Gender.fromLabel(genderLabel);

        return (root, query, cb) ->
                cb.equal(root.get("person").get("gender"), gender);
    }

    public static Specification<Agent> cpfLike(String cpf) {
        if (cpf == null || cpf.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("cpf")),
                        "%" + cpf.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> phoneLike(String phone) {
        if (phone == null || phone.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("phone")),
                        "%" + phone.trim().toUpperCase() + "%"
                );
    }

    public static Specification<Agent> birthdayEqual(LocalDate birthday) {
        if (birthday == null) return null;

        return (root, query, cb) ->
                cb.equal(root.get("person").get("birthday"), birthday);
    }

    public static Specification<Agent> emailLike(String email) {
        if (email == null || email.isBlank()) return null;

        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("person").get("email")),
                        "%" + email.trim().toUpperCase() + "%"
                );
    }

}
