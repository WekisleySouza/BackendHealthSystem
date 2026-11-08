package com.project.healthsystem.controller.dto.authorization_form;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "PatientDataResponseDTO", description = "Dados retornados pela API. PatientDataResponseDTO.")
public record PatientDataResponseDTO(
    @Schema(description = "Nome de exibição do registro.")
    String name,
    @Schema(description = "Identificação do cartão e-SUS usada na autorização.")
    String esusCard,
    @Schema(description = "Sexo informado no cadastro do paciente; a chave JSON tem inicial maiúscula.")
    String Sex,
    @Schema(description = "Data de nascimento. Formato AAAA-MM-DD, por exemplo 2026-11-08.", type = "string", format = "date", example = "1990-05-20")
    LocalDate birthday,
    @Schema(description = "Idade do paciente em anos.")
    Integer age,
    @Schema(description = "Endereço completo informado no cadastro.")
    String address
) {
}
