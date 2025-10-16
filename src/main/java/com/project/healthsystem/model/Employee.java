package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.EmployeeDTO;
import com.project.healthsystem.model.abstractions.UserBasicAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name="tb_employees")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employee extends UserBasicAbstraction {

    @OneToOne(mappedBy = "employee")
    private Login login;
    @OneToMany(mappedBy = "employee")
    private List<Appointment> appointments;
}
