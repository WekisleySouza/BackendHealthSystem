package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class AgentResponseDTO {
    private long id;
    private String name;
    private String gender;
    private String sex;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;
    private String phone;
    private String cpf;
    private String address;
    private LocalDate birthday;
    private String email;
}
