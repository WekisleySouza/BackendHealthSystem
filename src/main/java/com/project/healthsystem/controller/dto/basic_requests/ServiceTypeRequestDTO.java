package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeRequestDTO {
    private Long categoryGroupId;

    @Size(max = 20, message = "O código SIGTAP não pode ultrapassar 20 caracteres!")
    private String sigtapCode;

    @NotBlank(message = "O nome é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    private String name;

    @NotNull(message = "O valor é obrigatório!")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero!")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 2 casas decimais!")

    private BigDecimal value;

    @NotBlank(message = "O tipo é obrigatório!")
    @Size(max = 50, message = "O tipo não pode ultrapassar 50 caracteres!")
    private String type;

}
