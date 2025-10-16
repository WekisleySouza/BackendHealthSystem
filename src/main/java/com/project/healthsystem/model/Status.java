package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="tb_status")
@Data
@NoArgsConstructor
public class Status extends IDAbstraction {

    @NotBlank(message = "A especificação é obrigatória!")
    @Column(name="specification")
    private String specification;

    @OneToMany(mappedBy = "status")
    private List<Appointment> appointment;
}
