package com.project.healthsystem.controller.common;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.FieldErrorResponseDTO;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<FieldErrorResponseDTO> fieldErrorResponseDTOS = fieldErrors
            .stream()
            .map(
                fe -> new FieldErrorResponseDTO(
                    fe.getField(),
                    fe.getDefaultMessage()
                )
            )
            .toList();
        return new ErrorResponseDTO(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Erro ao validar campo!",
                fieldErrorResponseDTOS
        );
    }

    @ExceptionHandler(DuplicatedRegisterException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleDuplicatedRegisterException(DuplicatedRegisterException e){
        return ErrorResponseDTO.conflict(e.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleInconsistentDataException(InvalidDataException e){
        return ErrorResponseDTO.invalidData(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFoundException(NotFoundException e){
        return ErrorResponseDTO.notFound(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleAccesDeniedException(NotFoundException e){
        return new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Você não tem permissão para executar esta operação!",
            List.of());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthorizationDenied(AuthorizationDeniedException e){
        return new ErrorResponseDTO(
            HttpStatus.UNAUTHORIZED.value(),
            "Autorização negada!",
            List.of()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleInvalidCredentials(BadCredentialsException e){
        return new ErrorResponseDTO(
            HttpStatus.UNAUTHORIZED.value(),
            "Credenciais inválidas, verifique se seu login e senha estão corretos!",
            List.of());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleUnhandledErrorException(InternalAuthenticationServiceException e){
        return new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "Credenciais inválidas, verifique se seu login e senha estão corretos!",
                List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleUnhandledErrorException(RuntimeException e){
        System.out.println("Erro inesperado: " + e);
        return new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Ops! Houve um erro inesperado. Entre em contato com nossa equipe.",
            List.of());
    }
}
