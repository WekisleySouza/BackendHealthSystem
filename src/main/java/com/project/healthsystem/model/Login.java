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

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;
    @Column(name = "login", nullable = false, unique = true)
    private  String login;
    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active;
}
