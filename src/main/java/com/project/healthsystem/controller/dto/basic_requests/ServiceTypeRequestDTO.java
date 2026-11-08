package com.project.healthsystem.controller.dto.basic_requests;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ServiceTypeRequestDTO", description = "Dados enviados pelo frontend. ServiceTypeRequestDTO.")
public class ServiceTypeRequestDTO {
    @Schema(description = "ID do grupo de categorias. Consulte GET /category-groups.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Long categoryGroupId;

    @Size(max = 20, message = "O código SIGTAP não pode ultrapassar 20 caracteres!")
    @Schema(description = "Código SIGTAP do procedimento, como texto. Consulte /datasus/sigtap/procedures; não envie o ID interno do catálogo.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "0301010072", maxLength = 20, nullable = true)
    private String sigtapCode;

    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    @Schema(description = "Nome de exibição do registro.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String name;

    @NotNull(message = "O valor é obrigatório!")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero!")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 2 casas decimais!")

    @Schema(description = "Valor monetário do serviço, como número decimal. No cadastro, deve ser maior que zero e ter até duas casas decimais.", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal value;

    @NotBlank(message = "O tipo é obrigatório!")
    @Size(max = 50, message = "O tipo não pode ultrapassar 50 caracteres!")
    @Schema(description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Exame", "Especialidade"}, example = "Exame", maxLength = 50)
    private String type;

}
