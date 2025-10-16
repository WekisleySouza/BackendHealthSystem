package com.project.healthsystem.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeDTO {

    private long id;
    private long categoryGroupId;

    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotNull(message = "O valor é obrigatório!")
    private BigDecimal value;

    @NotBlank(message = "O tipo é obrigatório!")
    private String type;

}
