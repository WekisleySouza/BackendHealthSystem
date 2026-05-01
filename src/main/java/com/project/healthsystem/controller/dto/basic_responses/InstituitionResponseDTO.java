package com.project.healthsystem.controller.dto.basic_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstituitionResponseDTO{
    private Long id;
    private String name;
    private String cep;
    private String cityName;
    private String address;
    private String phone;
    private String linkLogo;

}
