package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login")
@Data
@NoArgsConstructor
public class Login extends BasicEntityAbstraction {
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "login", nullable = false, unique = true)
    private  String login;
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles role;

    public long getPersonId(){
        if(this.person != null){
            return  this.person.getId();
        }
        return -1;
    }

    public String getPersonCPF(){
        if(this.person != null){
            return  this.person.getCpf();
        }
        return "";
    }

    public long getEmployeeId(){
        if(this.employee != null){
            return  this.employee.getId();
        }
        return -1;
    }
}
