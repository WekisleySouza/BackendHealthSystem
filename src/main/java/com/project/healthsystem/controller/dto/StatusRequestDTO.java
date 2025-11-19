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
public class StatusRequestDTO {

    private long id;
    @NotBlank(message = "A especificação é obrigatória!")
    private String specification;
}
