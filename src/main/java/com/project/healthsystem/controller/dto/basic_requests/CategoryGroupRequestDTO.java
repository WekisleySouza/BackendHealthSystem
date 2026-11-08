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

@Schema(name = "CategoryGroupRequestDTO", description = "Dados enviados pelo frontend. CategoryGroupRequestDTO.")
public class CategoryGroupRequestDTO {
    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    
    @Schema(description = "Nome de exibição do registro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String name;
}
