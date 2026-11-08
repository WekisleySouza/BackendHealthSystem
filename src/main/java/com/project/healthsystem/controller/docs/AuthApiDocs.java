package com.project.healthsystem.controller.docs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.basic_requests.AuthRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AuthResponseDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.ProfileResponseDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

/** Contrato documental; rotas, segurança e implementação permanecem no controller. */
@Tag(name = "Autenticação", description = "Operações de autenticação.")
public interface AuthApiDocs {
    @Operation(summary = "Autenticar usuário", description = "Envie login e password. A resposta contém access_token, refresh_token, login e roles. Use access_token no Bearer das demais requisições. Não exige token de acesso.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AuthResponseDTO> authenticateUser(
        @Valid AuthRequestDTO authRequestDTO
    );

    @Operation(summary = "Renovar acesso", description = "Envie {\"refreshToken\":\"...\"}; recebe {\"accessToken\":\"...\"}. Atenção: estas chaves usam camelCase, diferentemente da resposta de /auth/login. Não exige token de acesso.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<?> refreshToken(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Objeto com refreshToken; use o refresh_token recebido no login.", content = @Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"refreshToken\":\"token-de-renovacao\"}"))) Map<String, String> request
    );

    @Operation(summary = "Encerrar sessão", description = "Envie refreshToken no corpo JSON para revogar o token de renovação. Resposta sem corpo. Não exige token de acesso.")
    @SecurityRequirements
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operação concluída, sem corpo.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<?> logout(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Objeto com refreshToken; use o refresh_token recebido no login.", content = @Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"refreshToken\":\"token-de-renovacao\"}"))) Map<String, String> request
    );

    @Operation(summary = "Consultar meu perfil", description = "Retorna o perfil identificado pelo token enviado no cabeçalho Authorization. Exige token de acesso válido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<ProfileResponseDTO> profile(
        @Parameter(hidden = true) String authHeader
    );

}
