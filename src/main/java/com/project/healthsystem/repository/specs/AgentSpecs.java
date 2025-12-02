package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Agent;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AgentSpecs {

    public static Specification<Agent> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Agent> cpfEqual(String cpf){
        if(cpf == null || cpf.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("cpf"), cpf);
    }

    public static Specification<Agent> phoneEqual(String phone){
        if(phone == null || phone.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("phone"), phone);
    }

    public static Specification<Agent> birthdayEqual(LocalDate birthday){
        if(birthday == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("birthday"), birthday);
    }

    public static Specification<Agent> emailEqual(String email){
        if(email == null || email.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.like(
                criteriaBuilder.upper(root.get("email")),
        "%" + email.toUpperCase() + "%"
        );
    }
}
