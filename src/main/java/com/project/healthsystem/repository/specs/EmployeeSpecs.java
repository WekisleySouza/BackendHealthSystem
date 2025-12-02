package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecs {
    public static Specification<Employee> nameEqual(String name){
        if(name == null || name.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Employee> cpfEqual(String cpf){
        if(cpf == null || cpf.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("cpf"), cpf);
    }

    public static Specification<Employee> phoneEqual(String phone){
        if(phone == null || phone.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("phone"), phone);
    }

    public static Specification<Employee> birthdayEqual(LocalDate birthday){
        if(birthday == null) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("birthday"), birthday);
    }

    public static Specification<Employee> emailEqual(String email){
        if(email == null || email.isBlank()) return null;
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                criteriaBuilder.upper(root.get("email")),
                "%" + email.toUpperCase() + "%"
        );
    }
}
