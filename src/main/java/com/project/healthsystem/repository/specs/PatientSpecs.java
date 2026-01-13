package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Patient;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PatientSpecs {

    public static Specification<Patient> cnsEqual(String cns){
        if(cns == null || cns.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("cns"), cns);
    }

    public static Specification<Patient> motherNameEqual(String motherName){
        if(motherName == null || motherName.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("motherName"), motherName);
    }

    public static Specification<Patient> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Patient> cpfEqual(String cpf){
        if(cpf == null || cpf.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("cpf"), cpf);
    }

    public static Specification<Patient> phoneEqual(String phone){
        if(phone == null || phone.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("phone"), phone);
    }

    public static Specification<Patient> birthdayEqual(LocalDate birthday){
        if(birthday == null) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.equal(root.get("birthday"), birthday);
    }

    public static Specification<Patient> emailEqual(String email){
        if(email == null || email.isBlank()) return null;
        return (root, query, criteriaBuilder)
            -> criteriaBuilder.like(
            criteriaBuilder.upper(root.get("email")),
            "%" + email.toUpperCase() + "%"
        );
    }
}
