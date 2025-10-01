package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Roles;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginDTO {

    private long id;
    private long personId;
    private long employeeId;

    private String name;
    private String password;
    private String role;

    public LoginDTO(Login login){
        this.id = login.getId();
        this.personId = login.getPersonId();
        this.employeeId = login.getEmployeeId();
        this.name = login.getName();
        this.password = login.getPassword();
        this.role = login.getRole().name();
    }

    public Login mappingToLogin(){
        return new Login(
                this.name,
                this.password,
                Roles.fromString(this.role)
        );
    }

    public Login mappingToLogin(Person person){
        Login login = new Login();

        login.setPerson(person);
        login.setName(this.name);
        login.setPassword(this.password);
        login.setRole(Roles.fromString(this.role));

        return login;
    }

    public Login mappingToLogin(Employee employee){
        Login login = new Login();

        login.setEmployee(employee);
        login.setName(this.name);
        login.setPassword(this.password);
        login.setRole(Roles.fromString(this.role));

        return login;
    }
}
