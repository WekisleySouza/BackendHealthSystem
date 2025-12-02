package com.project.healthsystem.controller.dto;

public record AuthResponseDTO(String login, String refresh_token, String access_token, String role) {}
