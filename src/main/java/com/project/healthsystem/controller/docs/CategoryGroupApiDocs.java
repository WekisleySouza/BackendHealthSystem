package com.project.healthsystem.controller.docs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.basic_requests.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.CategoryGroupSimplifiedResponseDTO;
import com.project.healthsystem.model.CategoryGroup;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
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
@Tag(name = "Categorias", description = "Operações de categorias.")
public interface CategoryGroupApiDocs {
    @Operation(summary = "Cadastrar registro", description = "Cria um registro de categorias. Acesso: ADMIN, MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado. Corpo vazio; o cabeçalho Location informa a URL do recurso.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> save(
        @Parameter(hidden = true) String authHeader,
        @Valid CategoryGroupRequestDTO categoryGroupRequestDTO
    );

    @Operation(summary = "Atualizar registro", description = "Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN, MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operação concluída, sem corpo.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> update(
        @Parameter(hidden = true) String authHeader,
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id,
        @Valid CategoryGroupRequestDTO categoryGroupRequestDTO
    );

    @Operation(summary = "Consultar registro por ID", description = "Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> read(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Listar categorias", description = "Consulta categorias com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<CategoryGroupSimplifiedResponseDTO>> readAllSimplified(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 20.", required = false) Integer pageLength
    );

    @Operation(summary = "Listar categorias", description = "Consulta categorias com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<CategoryGroupResponseDTO>> readAll(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength,
        @Parameter(name = "name", in = ParameterIn.QUERY, description = "Nome de exibição do registro.", required = false) String name
    );

    @Operation(summary = "Excluir registro", description = "Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operação concluída, sem corpo.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> delete(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

}
