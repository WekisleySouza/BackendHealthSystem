package com.project.healthsystem.controller.common;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.FieldErrorResponseDTO;
import com.project.healthsystem.exceptions.CantDeleteException;
import com.project.healthsystem.exceptions.DuplicatedRegisterException;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.security.SignatureException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(CantDeleteException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleCantDeleteException(CantDeleteException e){
        return new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Não foi possível excluir!",
            List.of());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFoundException(NotFoundException e){
        return ErrorResponseDTO.notFound(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        return new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Dados enviados em formato inválido!",
            List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleAccesDeniedException(AccessDeniedException e){
        return new ErrorResponseDTO(
            HttpStatus.FORBIDDEN.value(),
            "Você não tem permissão para executar esta operação!",
            List.of());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleAuthorizationDenied(AuthorizationDeniedException e){
        return new ErrorResponseDTO(
            HttpStatus.FORBIDDEN.value(),
            "Autorização negada!",
            List.of()
        );
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleAuthorizationDenied(SignatureException e){
        return new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Falha na autenticação!",
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
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleUnhandledErrorException(InternalAuthenticationServiceException e){
        return new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciais inválidas, verifique se seu login e senha estão corretos!",
                List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleUnhandledErrorException(ConstraintViolationException e){
        log.error("Erro inesperado: " + e);
        return new ErrorResponseDTO(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Violação de restrição de dados!",
            List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleDataIntegrityViolationErrorException(DataIntegrityViolationException e){
        log.error("Erro inesperado: " + e);
        return new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Integridade de dados ameaçada! O recurso já existe ou viola uma restrição do banco.",
            List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleUnhandledErrorException(RuntimeException e){
        log.error("Erro inesperado: " + e);
        return new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Ops! Houve um erro inesperado. Entre em contato com nossa equipe.",
            List.of());
    }
}
