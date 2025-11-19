package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.AuthRequestDTO;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginService loginService;

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> authenticateUser(@RequestBody @Valid AuthRequestDTO authRequestDTO) {
        try {
            // Autentica com o AuthenticationManager do Spring
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getLogin(), authRequestDTO.getPassword())
            );

            // Busca o login no banco
            Login login = loginService.findByLogin(authRequestDTO.getLogin());
            if (login == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuário não encontrado"));
            }

            // Gera o token JWT
            String token = jwtTokenProvider.generateToken(login.getLogin(), login.getRole().name());

            // Retorna os dados básicos e o token
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "login", login.getLogin(),
                    "role", login.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));
        }
    }
}
