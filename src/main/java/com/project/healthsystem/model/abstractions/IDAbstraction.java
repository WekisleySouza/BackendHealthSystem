package com.project.healthsystem.model.abstractions;

import jakarta.persistence.*;
import lombok.Data;


@MappedSuperclass
@Data
public abstract class IDAbstraction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
}
