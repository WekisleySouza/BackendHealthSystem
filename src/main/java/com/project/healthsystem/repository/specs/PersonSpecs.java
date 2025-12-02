package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Person;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PersonSpecs {

    public static Specification<Person> cnsEqual(String cns){
        if(cns == null || cns.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("cns"), cns);
    }

    public static Specification<Person> motherNameEqual(String motherName){
        if(motherName == null || motherName.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("motherName"), motherName);
    }

    public static Specification<Person> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Person> cpfEqual(String cpf){
        if(cpf == null || cpf.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("cpf"), cpf);
    }

    public static Specification<Person> phoneEqual(String phone){
        if(phone == null || phone.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("phone"), phone);
    }

    public static Specification<Person> birthdayEqual(LocalDate birthday){
        if(birthday == null) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("birthday"), birthday);
    }

    public static Specification<Person> emailEqual(String email){
        if(email == null || email.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.like(
            criteriaBuilder.upper(root.get("email")),
            "%" + email.toUpperCase() + "%"
        );
    }
}
