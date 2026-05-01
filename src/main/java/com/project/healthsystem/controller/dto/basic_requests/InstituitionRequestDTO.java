package com.project.healthsystem.controller.dto.basic_requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstituitionRequestDTO {
    @NotBlank(message = "O nome da instituição é obrigatório!")
    @Size(max = 150, message = "O nome não pode ultrapassar 150 caracteres!")
    private String name;
    @NotBlank(message = "O cep da instituição é obrigatório!")
    @Size(max = 8, message = "O CEP não pode ultrapassar 8 caracteres!")
    private String cep;
    @Size(max = 150, message = "O nome da cidade não pode ultrapassar 150 caracteres!")
    private String cityName;
    @NotBlank(message = "O endereço da instituição é obrigatório!")
    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres!")
    private String address;
    @Size(max = 20, message = "O telefone não pode ultrapassar 20 caracteres!")
    private String phone;
    @Size(max = 2048, message = "O link não pode ultrapassar 2048 caracteres!")
    private String linkLogo;
}
