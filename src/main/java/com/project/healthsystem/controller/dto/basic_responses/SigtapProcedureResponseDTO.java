package com.project.healthsystem.controller.dto.basic_responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SigtapProcedureResponseDTO {
    private Long id;
    private String code;
    private String name;
    private String competence;
    private String complexity;
    private String sex;
    private Integer minimumAge;
    private Integer maximumAge;
    private String groupCode;
    private String groupName;
    private String subGroupCode;
    private String subGroupName;
    private String organizationCode;
    private String organizationName;
}
