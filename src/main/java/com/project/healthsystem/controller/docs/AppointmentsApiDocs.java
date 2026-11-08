package com.project.healthsystem.controller.docs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.authorization_form.AuthorizationFormResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.appointment_get_by_id.AppointmentGetByIdResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.NewCommonStatusDTO;
import com.project.healthsystem.controller.dto.basic_requests.PreSchedulingRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AppointmentResponseDTO;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportFilterDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportReponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.ReportAppointmentGraphResponseDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.model.Appointment;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
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
@Tag(name = "Atendimentos", description = "Operações de atendimentos.")
public interface AppointmentsApiDocs {
    @Operation(summary = "Consultar histórico de status", description = "Eventos do atendimento, do mais recente ao mais antigo (recordedAt e id decrescentes). Use pageNumber e pageLength, sem hífen. Alterações automáticas identificam o autor como Sistema. Paginação: página >= 0, tamanho entre 1 e 100; valores inválidos retornam 400. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Page<com.project.healthsystem.controller.dto.basic_responses.AppointmentStatusHistoryResponseDTO>> getStatusHistory(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id,
        @Parameter(name = "0", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) int pageNumber,
        @Parameter(name = "20", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 20. Limite: 1 a 100.", required = false) int pageLength
    );

    @Operation(summary = "Cadastrar registro", description = "Cria um registro de atendimentos. Instituições dos quatro papéis são independentes do vínculo legado. Campos de regulação omitidos/null são preservados na atualização. Use os rótulos em português para status, priority, careModality e requestCharacter. Acesso: ADMIN, MANAGER, EMPLOYEE.")
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
        @Valid AppointmentRequestDTO appointmentRequestDTO
    );

    @Operation(summary = "Atualizar registro", description = "Atualiza o registro identificado por id. Envie os campos do DTO; esta rota não deve ser tratada como PATCH genérico. Instituições dos quatro papéis são independentes do vínculo legado. Campos de regulação omitidos/null são preservados na atualização. Use os rótulos em português para status, priority, careModality e requestCharacter. Acesso: ADMIN, MANAGER, EMPLOYEE.")
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
        @Valid AppointmentRequestDTO appointmentRequestDTO
    );

    @Operation(summary = "Consultar registro por ID", description = "Retorna os dados do registro identificado por id. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AppointmentGetByIdResponseDTO> read(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Listar atendimentos", description = "Consulta atendimentos com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<AppointmentResponseDTO>> readAll(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength,
        @Parameter(name = "notes", in = ParameterIn.QUERY, description = "Observações do atendimento; até 1000 caracteres.", required = false) String notes,
        @Parameter(name = "scheduledAt", in = ParameterIn.QUERY, description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAt,
        @Parameter(name = "scheduled-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtStart,
        @Parameter(name = "scheduled-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtEnd,
        @Parameter(name = "createdAt", in = ParameterIn.QUERY, description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAt,
        @Parameter(name = "created-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtStart,
        @Parameter(name = "created-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtEnd,
        @Parameter(name = "scheduled-forecast", in = ParameterIn.QUERY, description = "Previsão de agendamento usada como filtro. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledForecast,
        @Parameter(name = "scheduled-forecast-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de previsão de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledForecastStart,
        @Parameter(name = "scheduled-forecast-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de previsão de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledForecastEnd,
        @Parameter(name = "priorit", in = ParameterIn.QUERY, description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", required = false) String priorit,
        @Parameter(name = "agreement", in = ParameterIn.QUERY, description = "Nome do convênio usado no relatório/filtro.", required = false) String agreement,
        @Parameter(name = "status", in = ParameterIn.QUERY, description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", required = false) String status,
        @Parameter(name = "responsible-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional responsável usado no filtro.", required = false) String responsibleProfessional,
        @Parameter(name = "requesting-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional solicitante usado no filtro.", required = false) String requestingProfessional,
        @Parameter(name = "employee-name", in = ParameterIn.QUERY, description = "Nome do funcionário usado no filtro.", required = false) String employee,
        @Parameter(name = "patient-name", in = ParameterIn.QUERY, description = "Nome do paciente usado no filtro.", required = false) String patient,
        @Parameter(name = "type", in = ParameterIn.QUERY, description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", required = false) String type,
        @Parameter(name = "category-name", in = ParameterIn.QUERY, description = "Nome da categoria do serviço.", required = false) String categoryName,
        @Parameter(name = "service-type-name", in = ParameterIn.QUERY, description = "Nome do serviço/procedimento cadastrado.", required = false) String serviceTypeName,
        @Parameter(name = "instituition-name", in = ParameterIn.QUERY, description = "Nome da instituição do vínculo legado.", required = false) String instituitionName,
        @Parameter(name = "is-sorted-by-name", in = ParameterIn.QUERY, description = "true ordena pelo nome do serviço; false ordena por createdAt.", required = false) boolean isSortedByName,
        @Parameter(name = "is-return", in = ParameterIn.QUERY, description = "Indica se o atendimento é um retorno. false quando omitido em DTO com boolean primitivo.", required = false) Boolean isReturn,
        @Parameter(name = "is-descending", in = ParameterIn.QUERY, description = "true ordena de forma decrescente; false, crescente.", required = false) boolean isDescending
    );

    @Operation(summary = "Consultar totais de atendimentos", description = "Retorna contagens por situação para os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<ReportAppointmentGraphResponseDTO> reportAppointmentsPatientGraph(
        @Parameter(name = "patient-name", in = ParameterIn.QUERY, description = "Nome do paciente.", required = false) String patientName,
        @Parameter(name = "mother-name", in = ParameterIn.QUERY, description = "Nome da mãe usado para filtrar pacientes.", required = false) String patientMotherName,
        @Parameter(name = "responsible-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional responsável usado no filtro.", required = false) String responsibleProfessional,
        @Parameter(name = "requesting-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional solicitante usado no filtro.", required = false) String requestingProfessional,
        @Parameter(name = "status", in = ParameterIn.QUERY, description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", required = false) String status,
        @Parameter(name = "agreement", in = ParameterIn.QUERY, description = "Nome do convênio usado no relatório/filtro.", required = false) String agreement,
        @Parameter(name = "priorit", in = ParameterIn.QUERY, description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", required = false) String priorit,
        @Parameter(name = "type", in = ParameterIn.QUERY, description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", required = false) String type,
        @Parameter(name = "service-name", in = ParameterIn.QUERY, description = "Nome do serviço/procedimento.", required = false) String serviceName,
        @Parameter(name = "instituition-name", in = ParameterIn.QUERY, description = "Nome da instituição do vínculo legado.", required = false) String instituitionName,
        @Parameter(name = "scheduled-at", in = ParameterIn.QUERY, description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAt,
        @Parameter(name = "scheduled-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtStart,
        @Parameter(name = "scheduled-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtEnd,
        @Parameter(name = "created-at", in = ParameterIn.QUERY, description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAt,
        @Parameter(name = "created-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtStart,
        @Parameter(name = "created-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtEnd
    );

    @Operation(summary = "Consultar relatório paginado de pacientes", description = "Retorna uma página de linhas de relatório com os filtros informados. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<PatientReportReponseDTO>> reportAppointmentsPatientPage(
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength,
        @Parameter(name = "patient-name", in = ParameterIn.QUERY, description = "Nome do paciente.", required = false) String patientName,
        @Parameter(name = "mother-name", in = ParameterIn.QUERY, description = "Nome da mãe usado para filtrar pacientes.", required = false) String patientMotherName,
        @Parameter(name = "responsible-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional responsável usado no filtro.", required = false) String responsibleProfessional,
        @Parameter(name = "requesting-professional-name", in = ParameterIn.QUERY, description = "Nome do profissional solicitante usado no filtro.", required = false) String requestingProfessional,
        @Parameter(name = "status", in = ParameterIn.QUERY, description = "Situação atual do atendimento. Valores: Pré agendado, Completo, Cancelado, Falta justificada, Falta não justificada, Paciente faltou, Agendado, Sem agendamento, Atrasado.", required = false) String status,
        @Parameter(name = "agreement", in = ParameterIn.QUERY, description = "Nome do convênio usado no relatório/filtro.", required = false) String agreement,
        @Parameter(name = "priorit", in = ParameterIn.QUERY, description = "Prioridade do atendimento; grafia legada mantida no contrato. Valores: Eletivo, Prioritário, Urgente.", required = false) String priorit,
        @Parameter(name = "type", in = ParameterIn.QUERY, description = "Tipo/classificação do registro. Valores: Exame, Especialidade.", required = false) String type,
        @Parameter(name = "service-name", in = ParameterIn.QUERY, description = "Nome do serviço/procedimento.", required = false) String serviceName,
        @Parameter(name = "instituition-name", in = ParameterIn.QUERY, description = "Nome da instituição do vínculo legado.", required = false) String instituitionName,
        @Parameter(name = "scheduled-at", in = ParameterIn.QUERY, description = "Data e hora em que o atendimento está agendado. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAt,
        @Parameter(name = "scheduled-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtStart,
        @Parameter(name = "scheduled-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de agendamento. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime scheduledAtEnd,
        @Parameter(name = "created-at", in = ParameterIn.QUERY, description = "Data e hora de criação do cadastro. No POST de atendimento, quando omitida, usa o horário atual; no PUT esse campo é atribuído diretamente. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAt,
        @Parameter(name = "created-at-start", in = ParameterIn.QUERY, description = "Data e hora inicial do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtStart,
        @Parameter(name = "created-at-end", in = ParameterIn.QUERY, description = "Data e hora final do intervalo de criação. Formato AAAA-MM-DDTHH:mm:ss, sem offset.", required = false) LocalDateTime createdAtEnd
    );

    @Operation(summary = "Baixar relatório de pacientes em PDF", description = "Retorna application/pdf como anexo. fields escolhe as colunas e sua ordem; omitido/vazio inclui todas. sort usa campo,asc ou campo,desc. Campo, ordenação ou limite de registros inválido retorna 400. Leia a resposta como Blob no frontend. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    void reportAppointmentsPatientPdf(
        PatientReportFilterDTO filter,
        @Parameter(name = "fields", in = ParameterIn.QUERY, description = "Colunas do PDF, na ordem desejada. Separe por vírgulas ou repita o parâmetro. Omitido/vazio inclui todas. Opções: patientName, motherName, scheduledAt, createdAt, status, responsibleProfessionalName, requestingProfessionalName, priorit, serviceName, serviceType, agreement.", required = false) List<String> fields,
        @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Ordenação no formato campo,asc ou campo,desc. Exemplo: scheduledAt,desc.", required = false) String sort,
        @Parameter(hidden = true) HttpServletResponse response
    ) throws IOException;

    @Operation(summary = "Excluir registro", description = "Exclui o registro identificado por id. Referências existentes podem impedir a exclusão. Acesso: ADMIN, MANAGER, EMPLOYEE.")
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

    @Operation(summary = "Pré-agendar atendimentos em lote", description = "Envie appointmentsId e, opcionalmente, statusChangeReason. Aplica Pré agendado e registra as mudanças no histórico. Acesso: ADMIN, MANAGER.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operação concluída, sem corpo.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> setAppointmentsToPreScheduled(
        @Parameter(hidden = true) String authHeader,
        @Valid PreSchedulingRequestDTO preSchedulingRequestDTO
    );

    @Operation(summary = "Alterar status em lote", description = "Envie appointmentsId, status e justificativa opcional. Não aceita Pré agendado: use a rota específica. IDs repetidos são processados uma vez; status já igual não cria novo evento. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operação concluída, sem corpo.", content = @Content),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "409", description = "Conflito de cadastro ou integridade.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "422", description = "Falha de validação; consulte errors com fieldName e message.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Object> setAppointmentsToOtherStatus(
        @Parameter(hidden = true) String authHeader,
        @Valid NewCommonStatusDTO preSchedulingRequestDTO
    );

    @Operation(summary = "Consultar dados da autorização", description = "Retorna JSON com paciente, instituição e procedimento para montar a autorização no frontend; não retorna PDF. Acesso: ADMIN, MANAGER, EMPLOYEE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "404", description = "Registro não encontrado.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AuthorizationFormResponseDTO> countExamsByServiceType(
        @Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador interno do registro no Vitalya; não é o código do catálogo externo.", required = true) long id
    );

    @Operation(summary = "Consultar meus atendimentos", description = "Consulta os atendimentos do paciente identificado pelo token. Acesso efetivo na configuração atual: ADMIN, MANAGER e PATIENT. A constante de permissão não inclui EMPLOYEE nesta rota.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta/operação concluída.", useReturnTypeSchema = true),
        @ApiResponse(responseCode = "400", description = "Parâmetro ou regra de negócio inválida.", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Credenciais ausentes, inválidas ou expiradas. A camada de segurança pode responder sem corpo."),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão. A camada de segurança pode responder sem corpo.")
    })
    ResponseEntity<Page<PatientAppointmentResponseDTO>> updatePatients(
        @Parameter(hidden = true) String authHeader,
        @Parameter(name = "page-number", in = ParameterIn.QUERY, description = "Número da página, começando em 0. Padrão: 0.", required = false) Integer pageNumber,
        @Parameter(name = "page-length", in = ParameterIn.QUERY, description = "Quantidade de registros por página. Padrão: 10.", required = false) Integer pageLength
    );

}
