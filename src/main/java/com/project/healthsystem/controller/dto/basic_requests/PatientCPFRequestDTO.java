package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PatientCPFRequestDTO", description = "Dados enviados pelo frontend. PatientCPFRequestDTO.")
public class PatientCPFRequestDTO {

    @NotNull
    @Schema(description = "ID do paciente no Vitalya. Consulte GET /patients.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long patientId;
    @NotBlank
    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres!")
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 14)
    private String cpf;

    @Schema(description = "CPF normalizado calculado pelo backend; não envie este campo.", accessMode = Schema.AccessMode.READ_ONLY)
    public String getCpfNormalized() {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
