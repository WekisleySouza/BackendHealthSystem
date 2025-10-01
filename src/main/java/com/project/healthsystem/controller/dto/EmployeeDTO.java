package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private long id;
    private String name;
    private String cpf;
    private String phone;
    private String birthday;
    private String email;

    public EmployeeDTO(Employee employee){
        this.id = employee.getId();
        this.name = employee.getName();
        this.cpf = employee.getCpf();
        this.phone = employee.getPhone();
        this.birthday = employee.getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.email = employee.getEmail();
    }

    public Employee mappingToEmployee(){
        Employee employee = new Employee();

        employee.setEmail(this.email);
        employee.setBirthday(LocalDate.parse(this.birthday, DateTimeFormatter.ofPattern("dd/MM/yyy")));
        employee.setCpf(this.cpf);
        employee.setPhone(this.phone);
        employee.setName(this.name);

        return employee;
    }
}
