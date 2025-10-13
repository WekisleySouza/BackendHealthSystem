package com.project.healthsystem.model;

import com.project.healthsystem.controller.dto.AgentDTO;
import com.project.healthsystem.model.abstractions.UserBasicAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name="tb_agent")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Agent extends UserBasicAbstraction {

    @OneToMany(mappedBy = "agent")
    private List<Person> persons;
}
