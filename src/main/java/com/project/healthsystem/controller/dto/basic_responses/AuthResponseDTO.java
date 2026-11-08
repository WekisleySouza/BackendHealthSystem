package com.project.healthsystem.controller.dto.basic_responses;

import io.swagger.v3.oas.annotations.media.Schema;

import com.project.healthsystem.model.Role;

import java.util.Set;

@Schema(name = "AuthResponseDTO", description = "Dados retornados pela API. AuthResponseDTO.")
public record AuthResponseDTO(
    @Schema(description = "Identificação de acesso do usuário.")
    String login,
    @Schema(description = "Token de renovação. Envie no campo refreshToken de POST /auth/refresh ou /auth/logout.")
    String refresh_token,
    @Schema(description = "Token JWT de acesso. Envie em Authorization: Bearer <access_token>.")
    String access_token,
    @Schema(description = "Papéis de acesso atribuídos ao usuário. Possibilidades: API, ADMIN, MANAGER, EMPLOYEE, PATIENT.")
    Set<Role> roles
) {}
