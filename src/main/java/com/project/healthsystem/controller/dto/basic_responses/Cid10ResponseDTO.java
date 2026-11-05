package com.project.healthsystem.controller.dto.basic_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cid10ResponseDTO {
    private Long id;
    private String code;
    private String description;
    private Long pecId;
    private String sex;
    private Boolean activeInPec;
}
