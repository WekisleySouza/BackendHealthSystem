package com.project.healthsystem.controller.docs;

import com.project.healthsystem.controller.dto.basic_responses.CnesProfessionalResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.CnesEstabilishmentResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.SigtapProcedureResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import java.io.IOException;
import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.Cid10ResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.Ciap2ResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;

/** Contrato documental; rotas, segurança e implementação permanecem no controller. */
@Tag(name = "Catálogos DATASUS", description = "Operações de catálogos datasus.")
public interface DataSusApiDocs {
    @Operation(summary = "Consultar procedimentos SIGTAP", description = "Busca parcial por código ou nome, sem distinguir maiúsculas. Ordenação por code e id. O catálogo importado é distinto dos serviços cadastrados no Vitalya. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<SigtapProcedureResponseDTO>> findSigtapProcedures(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10. Limite: 1 a 100.", required = false) int pageLength,
        @Parameter(name = "search", in = ParameterIn.QUERY, description = "Texto de busca; consulte os campos pesquisados na descrição desta operação.", required = false) String search
    );

    @Operation(summary = "Consultar procedimento SIGTAP por ID", description = "Retorna o procedimento importado e os códigos/nomes da hierarquia. O ID é interno do catálogo. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<SigtapProcedureResponseDTO> findSigtapProcedure(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Forçar importação SIGTAP", description = "Endpoint temporário de teste. Baixa e importa a versão mais recente mesmo se já importada. Responde após o commit com competência e contagens. Acesso: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<SigtapImportResponseDTO> importSigtap(
        
    ) throws IOException;

    @Operation(summary = "Consultar catálogo CID-10", description = "Busca parcial por código ou descrição, sem distinguir maiúsculas. Ordenação por code. Inclui códigos inativos no PEC. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<Cid10ResponseDTO>> findCid10(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10. Limite: 1 a 100.", required = false) int pageLength,
        @Parameter(name = "search", in = ParameterIn.QUERY, description = "Texto de busca; consulte os campos pesquisados na descrição desta operação.", required = false) String search
    );

    @Operation(summary = "Consultar catálogo CIAP-2", description = "Busca parcial por código ou descrição, sem distinguir maiúsculas. Ordenação por code. Não afirma elegibilidade do código na RNDS. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<Ciap2ResponseDTO>> findCiap2(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10. Limite: 1 a 100.", required = false) int pageLength,
        @Parameter(name = "search", in = ParameterIn.QUERY, description = "Texto de busca; consulte os campos pesquisados na descrição desta operação.", required = false) String search
    );

    @Operation(summary = "Importar estabelecimentos CNES", description = "Envie CSV UTF-8 separado por ponto e vírgula no campo multipart file. Valida antes de substituir o catálogo. Resposta confirma catalog e importedRecords após commit. Acesso: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "413", description = "Arquivo maior que o limite configurado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CnesImportResponseDTO> importCnesEstabilishments(
        @Parameter(name = "file", in = ParameterIn.QUERY, description = "Arquivo CSV enviado como multipart/form-data, no campo file.", required = true) MultipartFile file
    );

    @Operation(summary = "Importar vínculos profissionais CNES", description = "Envie CSV UTF-8 separado por ponto e vírgula no campo multipart file. Valida os registros antes de substituir o catálogo. Preserva vínculos repetidos por CNS e CBO alfanumérico. Resposta confirma catalog e importedRecords após commit. Acesso: ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "413", description = "Arquivo maior que o limite configurado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CnesImportResponseDTO> importCnesProfessionals(
        @Parameter(name = "file", in = ParameterIn.QUERY, description = "Arquivo CSV enviado como multipart/form-data, no campo file.", required = true) MultipartFile file
    );

    @Operation(summary = "Consultar vínculos profissionais CNES", description = "Busca parcial por nome ou CNS. Filtros exatos cnes e cbo são combinados com a busca. CBO não distingue maiúsculas. Cada registro é um vínculo: um CNS pode aparecer várias vezes. Ordenação por name e id. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<CnesProfessionalResponseDTO>> findProfessionals(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10. Limite: 1 a 100.", required = false) int pageLength,
        @Parameter(name = "search", in = ParameterIn.QUERY, description = "Texto de busca; consulte os campos pesquisados na descrição desta operação.", required = false) String search,
        @Parameter(name = "cnes", in = ParameterIn.QUERY, description = "Filtro cnes. Filtro exato.", required = false) String cnes,
        @Parameter(name = "cbo", in = ParameterIn.QUERY, description = "Código da Classificação Brasileira de Ocupações, mantido como texto. Filtro exato, sem distinguir maiúsculas.", required = false) String cbo
    );

    @Operation(summary = "Consultar vínculo CNES por ID", description = "Retorna um vínculo profissional importado, não um profissional cadastrado no Vitalya. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CnesProfessionalResponseDTO> findProfessional(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Consultar estabelecimentos CNES", description = "Busca parcial por razão social, nome fantasia, CNES ou CNPJ, sem distinguir maiúsculas. Ordenação por tradeName e id. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<CnesEstabilishmentResponseDTO>> findEstabilishments(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10. Limite: 1 a 100.", required = false) int pageLength,
        @Parameter(name = "search", in = ParameterIn.QUERY, description = "Texto de busca; consulte os campos pesquisados na descrição desta operação.", required = false) String search
    );

    @Operation(summary = "Consultar estabelecimento CNES por ID", description = "Retorna um estabelecimento importado, não uma instituição cadastrada no Vitalya. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CnesEstabilishmentResponseDTO> findEstabilishment(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

}
