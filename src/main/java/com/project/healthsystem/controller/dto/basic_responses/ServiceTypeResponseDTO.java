package com.project.healthsystem.controller.dto.basic_responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ServiceTypeResponseDTO {
    private long id;
    private long categoryGroupId;

    private String name;
    private String sigtapCode;
    private BigDecimal value;
    private String type;
}
