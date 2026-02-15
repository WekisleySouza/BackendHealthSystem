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

    private String teamName;
    private String teamINE;
    private String microArea;
    private String origin;

    private Long agentId;
    private Long responsibleId;
    private List<Long> conditionsId;

    private String name;
    private String gender;
    private String sex;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;
    private String motherName;
    private LocalDate birthday;
    private String cns;
    private String cpf;
    private String address;
    private String email;
}
