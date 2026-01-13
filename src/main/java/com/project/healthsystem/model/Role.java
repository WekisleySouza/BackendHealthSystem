package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_roles")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends IDAbstraction {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, unique = true)
    private Roles role;

    public Role(Roles role){
        this.role = role;
    }
}
