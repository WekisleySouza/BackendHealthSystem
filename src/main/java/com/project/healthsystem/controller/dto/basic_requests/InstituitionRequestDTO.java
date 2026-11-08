package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "InstituitionRequestDTO", description = "Dados enviados pelo frontend. InstituitionRequestDTO.")
public class InstituitionRequestDTO {
    @NotBlank(message = "O nome da instituição é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    @Schema(description = "Nome de exibição do registro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String name;
    @NotBlank(message = "O cep da instituição é obrigatório!")
    @Size(max = 8, message = "O CEP não pode ultrapassar 8 caracteres!")
    @Schema(description = "Código postal como texto; preserve zeros à esquerda.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 8)
    private String cep;
    @Size(max = 150, message = "O nome da cidade não pode ultrapassar 150 caracteres!")
    @Schema(description = "Nome do município.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 150, nullable = true)
    private String cityName;
    @NotBlank(message = "O endereço da instituição é obrigatório!")
    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    @Schema(description = "Endereço completo informado no cadastro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 255)
    private String address;
    @Size(max = 20, message = "O telefone não pode ultrapassar 20 caracteres!")
    @Schema(description = "Telefone da instituição.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 20, nullable = true)
    private String phone;
    @Size(max = 2048, message = "O link não pode ultrapassar 2048 caracteres!")
    @Schema(description = "URL do logotipo da instituição.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 2048, nullable = true)
    private String linkLogo;
    @Schema(description = "Código CNES opcional da instituição; quando informado, deve conter sete dígitos.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "0000123", nullable = true)
    private String cnes;
}
