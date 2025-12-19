package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.LoginRequestDTO;
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
public class LoginController {
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PutMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'USER')")
    public ResponseEntity<Object> updatePassword(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid LoginRequestDTO loginRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Login login = jwtTokenProvider.getLogin(accessToken);
        loginService.updatePassword(login, loginRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
