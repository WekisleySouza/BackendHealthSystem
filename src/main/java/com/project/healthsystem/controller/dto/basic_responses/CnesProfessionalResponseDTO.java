package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CnesProfessionalResponseDTO", description = "Dados retornados pela API. CnesProfessionalResponseDTO.")
public record CnesProfessionalResponseDTO(
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    long id,
    @Schema(description = "Nome de exibição do registro.")
    String name,
    @Schema(description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda. Na importação CNES, exige 15 dígitos.", example = "000000000000001")
    String cns,
    @Schema(description = "Nome do município.")
    String cityName,
    @Schema(description = "Código da Classificação Brasileira de Ocupações, mantido como texto. Pode conter letras, por exemplo 2231F9. Um CNS pode ter vários vínculos.")
    String cbo,
    @Schema(description = "Descrição da ocupação CBO.")
    String cboDescription,
    @Schema(description = "CNPJ do estabelecimento como texto, preservando zeros à esquerda.")
    String cnpj,
    @Schema(description = "Código CNES do estabelecimento com sete dígitos, como texto.", example = "0000123")
    String cnes,
    @Schema(description = "Nome do estabelecimento do vínculo profissional; grafia legada mantida.")
    String estabilishmentName,
    @Schema(description = "Código da natureza jurídica do estabelecimento.")
    String legalNatureCode,
    @Schema(description = "Descrição da natureza jurídica.")
    String legalNatureDescription,
    @Schema(description = "Indica se o vínculo atende ao SUS.", example = "false")
    Boolean sus,
    @Schema(description = "Indica se o vínculo é de residente.", example = "false")
    Boolean resident,
    @Schema(description = "Indica se o vínculo é de preceptor.", example = "false")
    Boolean preceptor,
    @Schema(description = "Gestão informada na origem; M/E/F são convertidos para Municipal/Estadual/Federal. Outros valores são preservados; não é enum fechado.")
    String management,
    @Schema(description = "Descrição do vínculo com o estabelecimento.")
    String estabilishmentBond,
    @Schema(description = "Descrição do vínculo com o empregador.")
    String employerBond,
    @Schema(description = "Detalhamento do vínculo profissional.")
    String bondDetails
) {}
