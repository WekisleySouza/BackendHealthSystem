package com.project.healthsystem.model.abstractions;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@MappedSuperclass
@Data
public abstract class UserBasicAbstraction extends IDAbstraction{

    @Column(name = "name", length = 100, nullable = false)
    protected String name;
    @Column(name = "cpf", nullable = false, length = 15)
    protected String cpf;
    @Column(name = "phone")
    protected String phone;
    @Column(name="birthday", nullable = false)
    protected LocalDate birthday;
    @Column(name="email", length=320)
    protected String email;
}
