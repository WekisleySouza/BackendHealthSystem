package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

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

@Schema(name = "ConditionRequestDTO", description = "Dados enviados pelo frontend. ConditionRequestDTO.")
public class ConditionRequestDTO {
    @NotBlank(message = "A especificação não pode estar em branco!")
    @Size(max = 1000, message = "A especificação não pode ultrapassar 1000 caracteres!")
    
    @Schema(description = "Descrição da condição de saúde cadastrada.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 1000)
    private String specification;

}
