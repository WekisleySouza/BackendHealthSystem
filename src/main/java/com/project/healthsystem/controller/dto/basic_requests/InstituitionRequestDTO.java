package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstituitionRequestDTO {
    @NotBlank(message = "O nome da instituição é obrigatório!")
    private String name;
    @NotBlank(message = "O cep da instituição é obrigatório!")
    private String cep;
    private String cityName;
    @NotBlank(message = "O endereço da instituição é obrigatório!")
    private String address;
    private String phone;
    private String linkLogo;
}
