package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BasicResponsesInstituitionResponseDTO", description = "Dados retornados pela API. InstituitionResponseDTO.")
public class InstituitionResponseDTO{
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private Long id;
    @Schema(description = "Nome de exibição do registro.")
    private String name;
    @Schema(description = "Código postal como texto; preserve zeros à esquerda.")
    private String cep;
    @Schema(description = "Nome do município.")
    private String cityName;
    @Schema(description = "Endereço completo informado no cadastro.")
    private String address;
    @Schema(description = "Telefone da instituição.")
    private String phone;
    @Schema(description = "URL do logotipo da instituição.")
    private String linkLogo;
    @Schema(description = "Código CNES opcional da instituição; quando informado, deve conter sete dígitos.", example = "0000123")
    private String cnes;

}
