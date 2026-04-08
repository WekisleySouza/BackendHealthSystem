package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 1000, message = "A especificação não pode ultrapassar 1000 caracteres!")
    private String specification;

}
