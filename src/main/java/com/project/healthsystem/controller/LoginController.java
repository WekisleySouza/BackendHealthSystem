package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.LoginRequestDTO;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "Login")
public class LoginController {
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PutMapping()
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT)
    @Operation(summary = "Update password", description = "Update logged-in user's password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully.")
    })
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
