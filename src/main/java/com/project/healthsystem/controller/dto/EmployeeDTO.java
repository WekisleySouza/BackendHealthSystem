package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private long id;

    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O CPF é obrigatório!")
    @CPF(message = "CPF inválido!")
    private String cpf;

    @NotBlank(message = "A data de nascimento é obrigatória!")
    private String birthday;

    @Email(message = "Formato de e-mail inválido!")
    @Size(max = 320, message = "O e-mail não pode ultrapassar 320 caracteres!")
    private String email;

    private String phone;

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
