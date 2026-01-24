package com.project.healthsystem.controller.dto;

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
    private String cpf;
    private String phone;
    private String address;
    private LocalDate birthday;
    private String email;
}
