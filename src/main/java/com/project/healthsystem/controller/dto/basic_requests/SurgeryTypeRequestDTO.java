package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SurgeryTypeRequestDTO", description = "Dados enviados pelo frontend. SurgeryTypeRequestDTO.")
public class SurgeryTypeRequestDTO {

    @NotBlank(message = "O tipo não pode estar em branco!")
    @Schema(description = "Descrição livre do tipo de cirurgia; não é enum fechado.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

}
