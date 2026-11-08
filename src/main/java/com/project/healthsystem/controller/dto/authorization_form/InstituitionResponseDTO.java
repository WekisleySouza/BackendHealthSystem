package com.project.healthsystem.controller.dto.authorization_form;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthorizationFormInstituitionResponseDTO", description = "Dados retornados pela API. InstituitionResponseDTO.")
public record InstituitionResponseDTO(
    @Schema(description = "Nome de exibição do registro.")
    String name,
    @Schema(description = "Código postal como texto; preserve zeros à esquerda.")
    String cep,
    @Schema(description = "Nome do município.")
    String cityName,
    @Schema(description = "Endereço completo informado no cadastro.")
    String address,
    @Schema(description = "Telefone da instituição.")
    String phone,
    @Schema(description = "URL do logotipo da instituição.")
    String linkLogo
) {
}
