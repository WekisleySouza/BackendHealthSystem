package com.project.healthsystem.controller.dto.basic_responses;

public record CnesProfessionalResponseDTO(long id, String name, String cns, String cityName, String cbo, String cboDescription, String cnpj, String cnes, String estabilishmentName, String legalNatureCode, String legalNatureDescription, Boolean sus, Boolean resident, Boolean preceptor, String management, String estabilishmentBond, String employerBond, String bondDetails) {}
