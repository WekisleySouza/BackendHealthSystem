package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.LoginDTO;
import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login")
@Data
@NoArgsConstructor
public class Login extends IDAbstraction {
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles role;

    public Login(String password, Roles role){
        this.password = password;
        this.role = role;
    }

    public void coppingFromLoginDTO(LoginDTO loginDTO){
        this.password = loginDTO.getPassword();
        this.role = Roles.fromString(loginDTO.getRole());
    }

    public long getPersonId(){
        if(this.person != null){
            return  this.person.getId();
        }
        return -1;
    }

    public long getEmployeeId(){
        if(this.employee != null){
            return  this.employee.getId();
        }
        return -1;
    }
}
