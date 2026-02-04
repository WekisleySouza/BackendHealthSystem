package com.project.healthsystem.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PatientResponseDTO {
    private long id;

    private Long agentId;
    private Long responsibleId;
    private List<Long> conditionsId;

    private String name;
    private String gender;
    private String motherName;
    private LocalDate birthday;
    private String cns;
    private String cpf;
    private String address;
    private String phone;
    private String email;
}
