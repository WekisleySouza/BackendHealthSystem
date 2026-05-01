package com.project.healthsystem.controller.dto.basic_responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConditionResponseDTO {
    private long id;
    private String specification;
}
