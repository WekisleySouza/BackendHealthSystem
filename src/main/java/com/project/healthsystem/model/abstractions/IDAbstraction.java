package com.project.healthsystem.model.abstractions;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public class IDAbstraction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
}
