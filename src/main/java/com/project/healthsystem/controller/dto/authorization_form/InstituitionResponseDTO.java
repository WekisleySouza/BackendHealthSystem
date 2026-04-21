package com.project.healthsystem.controller.dto.authorization_form;

public record InstituitionResponseDTO(
    String name,
    String cep,
    String cityName,
    String address,
    String phone,
    String linkLogo
) {
}
