package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.model.abstractions.UserBasicAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name="tb_employees")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends UserBasicAbstraction {

    @OneToOne(mappedBy = "employee")
    private Login login;

    public void mappingFromEmployeeDTO(EmployeeDTO employeeDTO){
        this.name = employeeDTO.getName();
        this.cpf = employeeDTO.getCpf();
        this.phone = employeeDTO.getPhone();
        this.birthday = LocalDate.parse(
                employeeDTO.getBirthday(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        this.email = employeeDTO.getEmail();
    }
}
