package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Role;

import java.util.Set;

public record AuthResponseDTO(String login, String refresh_token, String access_token, Set<Role> roles) {}
