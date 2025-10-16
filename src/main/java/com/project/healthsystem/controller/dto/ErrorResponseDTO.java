package com.project.healthsystem.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponseDTO(int status, String message, List<FieldErrorDTO> errors) {

    public static ErrorResponseDTO defaultResponse(String message){
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponseDTO conflict(String message){
        return new ErrorResponseDTO(HttpStatus.CONFLICT.value(), message, List.of());
    }

    public static ErrorResponseDTO notFound(String message){
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), message, List.of());
    }

    public static ErrorResponseDTO invalidData(String message){
        return new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }
}
