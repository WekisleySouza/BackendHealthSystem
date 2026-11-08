package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.LoginApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ForgotPasswordRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.LoginRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.ResetPasswordRequestDTO;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor

public class LoginController implements LoginApiDocs {
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PutMapping()
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT)

    public ResponseEntity<Object> updatePassword(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid LoginRequestDTO loginRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Login login = jwtTokenProvider.getLogin(accessToken);
        loginService.updatePassword(login, loginRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
        @RequestBody ForgotPasswordRequestDTO dto
    ) {
        loginService.forgotPassword(dto.getEmail());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
        @RequestBody ResetPasswordRequestDTO dto
    ) {
        loginService.resetPassword(
            dto.getToken(),
            dto.getNewPassword()
        );

        return ResponseEntity.noContent().build();
    }
}
