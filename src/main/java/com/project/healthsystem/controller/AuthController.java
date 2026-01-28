package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.AuthRequestDTO;
import com.project.healthsystem.controller.dto.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import com.project.healthsystem.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @PreAuthorize(Permissions.PERMIT_ALL)
    public ResponseEntity<AuthResponseDTO> authenticateUser(
        @RequestBody @Valid AuthRequestDTO authRequestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.authenticateUser(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/refresh")
    @PreAuthorize(Permissions.PERMIT_ALL)
    public ResponseEntity<?> refreshToken(
        @RequestBody Map<String, String> request
    ){
        String refreshToken = request.get("refreshToken");
        String newAccessToken = authService.getNewAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    @PreAuthorize(Permissions.PERMIT_ALL)
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        authService.revokeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @PreAuthorize(Permissions.PERMIT_ALL)
    public ResponseEntity<ProfileResponseDTO> profile(
        @RequestHeader("Authorization") String authHeader
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        ProfileResponseDTO profileResponseDTO = authService.getMyProfile(accessToken);
        return ResponseEntity.ok(profileResponseDTO);
    }
}
