package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.basic_requests.AuthRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import com.project.healthsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Authentication", description = "Operations related to authentication and user session.")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @PreAuthorize(Permissions.PERMIT_ALL)
    @Operation(
            summary = "Login",
            description = "Authenticate a user and return access and refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logged successfully.",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data sent in the request (validation errors).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - authentication service error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - constraint or integrity violation.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - field validation errors.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<AuthResponseDTO> authenticateUser(
            @RequestBody @Valid AuthRequestDTO authRequestDTO
    ) {
        AuthResponseDTO authResponseDTO = authService.authenticateUser(authRequestDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/refresh")
    @PreAuthorize(Permissions.PERMIT_ALL)
    @Operation(
            summary = "Refresh",
            description = "Generate a new access token from a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token generated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Map.class,
                                    description = "Map containing the new access token."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or malformed refresh token payload.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or expired refresh token.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - authentication service error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<?> refreshToken(
            @RequestBody Map<String, String> request
    ){
        String refreshToken = request.get("refreshToken");
        String newAccessToken = authService.getNewAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    @PreAuthorize(Permissions.PERMIT_ALL)
    @Operation(
            summary = "Logout",
            description = "Logout user account and revoke the refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logged out successfully. Token revoked."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid logout request payload.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing refresh token.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user not allowed to logout.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");
        authService.revokeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @PreAuthorize(Permissions.PERMIT_ALL)
    @Operation(
            summary = "Get Profile",
            description = "Get the authenticated user's profile information.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = ProfileResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid authorization header format.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or expired token.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access denied to profile information.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found for the given token.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ProfileResponseDTO> profile(
            @RequestHeader("Authorization") String authHeader
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        ProfileResponseDTO profileResponseDTO = authService.getMyProfile(accessToken);
        return ResponseEntity.ok(profileResponseDTO);
    }
}
