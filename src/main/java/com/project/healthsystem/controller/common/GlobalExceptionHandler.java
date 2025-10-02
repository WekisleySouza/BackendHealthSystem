package com.project.healthsystem.controller.common;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.FieldErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<FieldErrorDTO> fieldErrorDTOS = fieldErrors
            .stream()
            .map(
                fe -> new FieldErrorDTO(
                    fe.getField(),
                    fe.getDefaultMessage()
                )
            )
            .toList();
        return new ErrorResponseDTO(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Erro ao validar campo!",
            fieldErrorDTOS
        );
    }
}
