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
public class ConditionRequestDTO {
    @NotBlank(message = "A especificação não pode estar em branco!")
    private String specification;

}
