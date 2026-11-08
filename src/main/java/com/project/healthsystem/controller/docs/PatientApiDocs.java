package com.project.healthsystem.controller.docs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ConditionRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientCPFRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import com.project.healthsystem.controller.dto.basic_responses.PatientResponseDTO;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.pec_sync.PersonsInfoList;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedInfoDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedResponseDTO;
import com.project.healthsystem.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
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
@Tag(name = "Pacientes", description = "Operações de pacientes.")
public interface PatientApiDocs {
    @Operation(summary = "Cadastrar registro", description = "Cria um registro de pacientes. Acesso: ADMIN.")
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
        @Valid PatientRequestDTO person
    );

    @Operation(summary = "Atualizar registro", description = "Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Acesso: ADMIN.")
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
        @Valid PatientRequestDTO patientRequestDTO
    );

    @Operation(summary = "Consultar registro por ID", description = "Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PatientResponseDTO> read(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Consultar ficha do paciente", description = "Retorna dados pessoais, vínculos, condições e atendimentos do paciente. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PatientInfoResponseDTO> getPatientInfoById(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Consultar lista resumida de pacientes", description = "Retorna uma lista sem paginação com identificação, nome, CPF e nome da mãe. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<List<PatientSimplifiedInfoDTO>> getAllPatientInfo(
        
    );

    @Operation(summary = "Listar pacientes", description = "Consulta pacientes com os filtros informados. Resposta resumida para seleção no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<PatientSimplifiedResponseDTO>> readAllSimplified(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength
    );

    @Operation(summary = "Listar pacientes", description = "Consulta pacientes com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<PatientResponseDTO>> readAll(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength,
        @Parameter(name = "name", in = ParameterIn.QUERY, description = "Nome de exibição do registro.", required = false) String name,
        @Parameter(name = "gender", in = ParameterIn.QUERY, description = "Identidade de gênero informada no cadastro. Campo textual; não é um enum do Vitalya.", required = false) String gender,
        @Parameter(name = "cpf", in = ParameterIn.QUERY, description = "CPF como texto; preserve zeros à esquerda. No cadastro é validado e normalizado pelo backend.", required = false) String cpf,
        @Parameter(name = "birthday", in = ParameterIn.QUERY, description = "Data de nascimento. Formato AAAA-MM-DD.", required = false) LocalDate birthday,
        @Parameter(name = "email", in = ParameterIn.QUERY, description = "Endereço de e-mail de contato.", required = false) String email,
        @Parameter(name = "cns", in = ParameterIn.QUERY, description = "Número do Cartão Nacional de Saúde como texto; preserve zeros à esquerda.", required = false) String cns,
        @Parameter(name = "mother-name", in = ParameterIn.QUERY, description = "Nome da mãe do paciente.", required = false) String motherName,
        @Parameter(name = "team-name", in = ParameterIn.QUERY, description = "Nome da equipe de saúde.", required = false) String teamName,
        @Parameter(name = "team-ine", in = ParameterIn.QUERY, description = "Identificador Nacional de Equipe (INE), mantido como texto.", required = false) String teamINE,
        @Parameter(name = "micro-area", in = ParameterIn.QUERY, description = "Identificação da microárea de acompanhamento.", required = false) String microArea,
        @Parameter(name = "origin", in = ParameterIn.QUERY, description = "Origem do cadastro ou encaminhamento.", required = false) String origin,
        @Parameter(name = "sex", in = ParameterIn.QUERY, description = "Sexo informado no cadastro ou na fonte de origem. Campo textual; não é um enum do Vitalya.", required = false) String sex,
        @Parameter(name = "cell-phone", in = ParameterIn.QUERY, description = "Telefone celular de contato.", required = false) String cellPhone,
        @Parameter(name = "residential-phone", in = ParameterIn.QUERY, description = "Telefone residencial.", required = false) String residentialPhone,
        @Parameter(name = "contact-phone", in = ParameterIn.QUERY, description = "Telefone de contato alternativo.", required = false) String contactPhone
    );

    @Operation(summary = "Excluir registro", description = "Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN.")
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

    @Operation(summary = "Atualizar CPF e login do paciente", description = "Envie patientId e cpf. A operação usa a identidade do usuário autenticado. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> updatePatientCPF(
        @Parameter(hidden = true) String authHeader,
        @Valid PatientCPFRequestDTO patientCPFRequestDTO
    );

    @Operation(summary = "Sincronizar pacientes do PEC", description = "Recebe um array JSON de PersonBackupInfoDTO. Rota de integração; não recebe um objeto contendo uma lista. Acesso: API.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> syncPatients(
        @Valid List<PersonBackupInfoDTO> patients
    );

}
