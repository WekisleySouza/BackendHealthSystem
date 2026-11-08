package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "ServiceTypeResponseDTO", description = "Dados retornados pela API. ServiceTypeResponseDTO.")
public class ServiceTypeResponseDTO {
    @Schema(description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.")
    private long id;
    @Schema(description = "ID do grupo de categorias. Consulte GET /category-groups.")
    private long categoryGroupId;

    @Schema(description = "Nome de exibição do registro.")
    private String name;
    @Schema(description = "Código SIGTAP do procedimento, como texto. Consulte /datasus/sigtap/procedures; não envie o ID interno do catálogo.", example = "0301010072")
    private String sigtapCode;
    @Schema(description = "Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais.", example = "100.00")
    private BigDecimal value;
    @Schema(description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", allowableValues = {"Exame", "Especialidade"}, example = "Exame")
    private String type;
}
