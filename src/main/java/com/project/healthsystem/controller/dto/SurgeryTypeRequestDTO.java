package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgeryTypeRequestDTO {

    private long id;
    @NotBlank(message = "O tipo não pode estar em branco!")
    private String type;

}
