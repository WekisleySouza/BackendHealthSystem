package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.AuthRequestDTO;
import com.project.healthsystem.controller.dto.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import com.project.healthsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponseDTO> authenticateUser(
        @RequestBody @Valid AuthRequestDTO authRequestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.authenticateUser(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/refresh")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> refreshToken(
        @RequestBody Map<String, String> request
    ){
        String refreshToken = request.get("refreshToken");
        String newAccessToken = authService.getNewAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        authService.revokeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProfileResponseDTO> profile(
        @RequestHeader("Authorization") String authHeader
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        ProfileResponseDTO profileResponseDTO = authService.getMyProfile(accessToken);
        return ResponseEntity.ok(profileResponseDTO);
    }
}
