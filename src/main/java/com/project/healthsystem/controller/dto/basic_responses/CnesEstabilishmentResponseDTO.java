package com.project.healthsystem.controller.dto.basic_responses;

public record CnesEstabilishmentResponseDTO(long id, String unitCode, String cnesCode, String cnpjMaintainer, String legalName, String tradeName, String cep, String phone, String email, String cpf, String cnpj, String address) {}
