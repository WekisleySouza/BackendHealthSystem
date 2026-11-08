package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CnesEstabilishmentResponseDTO", description = "Dados retornados pela API. CnesEstabilishmentResponseDTO.")
public record CnesEstabilishmentResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Código da unidade na base CNES.")
    String unitCode,
    @Schema(description = "Código CNES do estabelecimento com sete dígitos; preserve zeros à esquerda.", example = "0000123")
    String cnesCode,
    @Schema(description = "CNPJ da mantenedora do estabelecimento, como texto.")
    String cnpjMaintainer,
    @Schema(description = "Razão social do estabelecimento.")
    String legalName,
    @Schema(description = "Nome fantasia do estabelecimento.")
    String tradeName,
    @Schema(description = "Código postal como texto; preserve zeros à esquerda.")
    String cep,
    @Schema(description = "Telefone da instituição.")
    String phone,
    @Schema(description = "Endereço de e-mail de contato.", example = "usuario@example.com", format = "email")
    String email,
    @Schema(description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.")
    String cpf,
    @Schema(description = "CNPJ do estabelecimento como texto, preservando zeros à esquerda.")
    String cnpj,
    @Schema(description = "Endereço completo informado no cadastro.")
    String address
) {}
