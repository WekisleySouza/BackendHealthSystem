package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.authorization_form.*;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.NewCommonStatusDTO;
import com.project.healthsystem.controller.dto.basic_requests.PreSchedulingRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AppointmentResponseDTO;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportField;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportFilterDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportReponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.ReportAppointmentGraphResponseDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.custom.AppointmentRepositoryCustom;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import com.project.healthsystem.repository.specs.AppointmentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentRepositoryCustom repositoryCustom;

    private final AppointmentValidator appointmentValidator;
    private final AppointmentsMapper appointmentsMapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final PatientReportPdfService patientReportPdfService;
    private final AppointmentStatusHistoryService statusHistoryService;

    private static final int REPORT_PDF_BATCH_SIZE = 500;

    @Value("${app.reports.pdf.max-rows}")
    private long reportPdfMaxRows;

    @Transactional
    public Appointment save(AppointmentRequestDTO appointmentRequestDTO, String token){
        Appointment appointment = appointmentValidator.validateSave(appointmentRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        if(appointmentRequestDTO.getCreatedAt() == null){
            appointment.createdNow();
        } else {
            appointment.setCreatedAt(appointmentRequestDTO.getCreatedAt());
        }
        appointment.setCreatedBy(currentEditor);
        appointment.setLastModifiedBy(currentEditor);

        Appointment saved = repository.save(appointment);
        statusHistoryService.record(saved, null, currentEditor, appointmentRequestDTO.getStatusChangeReason());
        return saved;
    }

    public int switchServiceTypeId(long id, long newId){
        return repository.updateServiceTypeId(id, newId);
    }

    @Transactional
    public Appointment update(AppointmentRequestDTO appointmentRequestDTO, long id, String token){
        Appointment appointment = findForUpdate(id);
        Status previousStatus = appointment.getStatus();
        appointment = appointmentValidator.validateUpdate(appointmentRequestDTO, appointment);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        appointment.setLastModifiedBy(currentEditor);
        appointment.updatedNow();
        Appointment saved = repository.save(appointment);
        statusHistoryService.record(saved, previousStatus, currentEditor, appointmentRequestDTO.getStatusChangeReason());
        return saved;
    }

    private Appointment findForUpdate(long id) {
        return repository.findByIdForUpdate(id)
            .orElseThrow(() -> new NotFoundException("Atendimento não encontrado!"));
    }

    public Page<AppointmentResponseDTO> getAll(
        String serviceType,
        String serviceTypeName,
        String categoryName,
        Integer pageNumber,
        Integer pageLength,
        String notes,
        LocalDateTime scheduledAt,
        LocalDateTime scheduledAtStart,
        LocalDateTime scheduledAtEnd,
        LocalDateTime createdAt,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd,
        LocalDateTime schedulingForecast,
        LocalDateTime schedulingForecastStart,
        LocalDateTime schedulingForecastEnd,
        String priorit,
        String status,
        String agreement,
        String responsibleProfessionalName,
        String requestingProfessionalName,
        String employeeName,
        String patientName,
        String instituitionName,
        boolean isSortedByName,
        boolean isDescending,
        Boolean isReturn
    ){
        String field = isSortedByName ? "serviceType.name" : "createdAt";
        Sort sort = Sort.by(
            isDescending ? Sort.Direction.DESC : Sort.Direction.ASC,
            field
        );

        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.notesLike(notes));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtBetween(scheduledAtStart, scheduledAtEnd));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtEqual(createdAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtBetween(createdAtStart, createdAtEnd));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.schedulingForecastEqual(schedulingForecast));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.schedulingForecastBetween(schedulingForecastStart, schedulingForecastEnd));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritLike(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusLike(status));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.agreementsLike(agreement));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(serviceType));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeNameLike(serviceTypeName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeCategoryGroupNameLike(categoryName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.instituitionNameLike(instituitionName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.responsibleProfessionalNameLike(responsibleProfessionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.requestingProfessionalNameLike(requestingProfessionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.employeeNameLike(employeeName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.isReturnEqual(isReturn));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.patientNameLike(patientName));
        return repository
            .findAll(specification, pageRequest)
            .map(appointmentsMapper::toDto);
    }

    public AppointmentGetByIdResponseDTO findById(long id){
        AppointmentGetByIdProjection appointmentProjection = appointmentValidator.validateFindById(id);
        return new AppointmentGetByIdResponseDTO(
            appointmentProjection.getId(),
            new ProfessionalInfoResponseDTO(
                appointmentProjection.getResponsibleProfessionalIdSafe(),
                appointmentProjection.getResponsibleProfessionalNameSafe()
            ),
            new ProfessionalInfoResponseDTO(
                appointmentProjection.getRequestingProfessionalIdSafe(),
                appointmentProjection.getRequestingProfessionalNameSafe()
            ),
            new EmployeeInfoResponseDTO(
                appointmentProjection.getEmployee().getId(),
                appointmentProjection.getEmployee().getPerson().getName()
            ),
            new PatientInfoResponseDTO(
                appointmentProjection.getPatient().getId(),
                appointmentProjection.getPatient().getPerson().getName()
            ),
            new ServiceTypeInfoResponseDTO(
                appointmentProjection.getServiceType().getId(),
                appointmentProjection.getServiceType().getType().getLabel(),
                appointmentProjection.getServiceType().getName()
            ),
            new CategoryGroupInfoResponseDTO(
                appointmentProjection.getServiceType().getCategoryGroupId(),
                appointmentProjection.getServiceType().getCategoryGroupName()
            ),
            new InstituitionInfoResponseDTO(
                appointmentProjection.getInstituitionIdSafe(),
                appointmentProjection.getInstituitionNameSafe()
            ),
            appointmentProjection.getStatus().getLabel(),
            appointmentProjection.getAgreementNameSafe(),
            appointmentProjection.getNotes(),
            appointmentProjection.getPriorit().getLabel(),
            appointmentProjection.getIsReturn(),

            appointmentProjection.getScheduledAt(),
            appointmentProjection.getCreatedAt(),
            appointmentProjection.getSchedulingForecast(),
            toInstituitionInfo(appointmentProjection.getRequestingInstituition()),
            toInstituitionInfo(appointmentProjection.getRegulatingInstituition()),
            toInstituitionInfo(appointmentProjection.getAuthorInstituition()),
            toInstituitionInfo(appointmentProjection.getExecutingInstituition()),
            appointmentProjection.getCareModality() == null ? null : appointmentProjection.getCareModality().getLabel(),
            appointmentProjection.getRequestCharacter() == null ? null : appointmentProjection.getRequestCharacter().getLabel(),
            appointmentProjection.getClinicalReasonSystem(),
            appointmentProjection.getClinicalReasonCode(),
            appointmentProjection.getRequestedAt(),
            appointmentProjection.getExecutingCbo()
        );
    }

    private InstituitionInfoResponseDTO toInstituitionInfo(AppointmentGetByIdProjection.InstituitionInfo instituition) {
        return instituition == null ? null : new InstituitionInfoResponseDTO(instituition.getId(), instituition.getName());
    }

    @Transactional
    public void delete(long id){
        Appointment appointment = findForUpdate(id);
        statusHistoryService.deleteForAppointment(id);
        repository.delete(appointment);
    }
    
    private Specification<Appointment> getReportSpecs (
      String patientName,
      String patientMotherName,
      String responsibleProfessionalName,
      String requestingProfessionalName,
      String status,
      String agreement,
      String priorit,
      String type,
      String serviceName,
      String instituitionName,
      LocalDateTime scheduledAt,
      LocalDateTime scheduledAtStart,
      LocalDateTime scheduledAtEnd,
      LocalDateTime createdAt,
      LocalDateTime createdAtStart,
      LocalDateTime createdAtEnd
    ){
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.patientNameLike(patientName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.patientMotherNameLike(patientMotherName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.responsibleProfessionalNameLike(responsibleProfessionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.requestingProfessionalNameLike(requestingProfessionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusLike(status));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.agreementsLike(agreement));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritLike(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(type));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeNameLike(serviceName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.instituitionNameLike(instituitionName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtBetween(scheduledAtStart, scheduledAtEnd));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtEqual(createdAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtBetween(createdAtStart, createdAtEnd));
        return specification;
    }

    public ReportAppointmentGraphResponseDTO getPatientReportGraphInfo(
        String patientName,
        String patientMotherName,
        String responsibleProfessionalName,
        String requestingProfessionalName,
        String status,
        String agreement,
        String priorit,
        String type,
        String serviceName,
        String instituitionName,
        LocalDateTime scheduledAt,
        LocalDateTime scheduledAtStart,
        LocalDateTime scheduledAtEnd,
        LocalDateTime createdAt,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd
    ){
        Specification<Appointment> specification = this.getReportSpecs(
          patientName,
          patientMotherName,
          responsibleProfessionalName,
          requestingProfessionalName,
          status,
          agreement,
          priorit,
          type,
          serviceName,
          instituitionName,
          scheduledAt,
          scheduledAtStart,
          scheduledAtEnd,
          createdAt,
          createdAtStart,
          createdAtEnd
        );
        
        Long total = repository.count(specification);
        List<AppointmentSummaryDTO> summaryList = repositoryCustom.getSummary(specification);
        Map<Status, Long> map = summaryList.stream()
            .collect(Collectors.toMap(
                AppointmentSummaryDTO::status,
                AppointmentSummaryDTO::total
            ));
        ReportAppointmentGraphResponseDTO dto =  new ReportAppointmentGraphResponseDTO();
        dto.setTotal(total);
        dto.setTotalPendingScheduling(map.getOrDefault(Status.PENDING_SCHEDULING, 0L));
        dto.setTotalCompleted(map.getOrDefault(Status.COMPLETED, 0L));
        dto.setTotalCanceled(map.getOrDefault(Status.CANCELED, 0L));
        dto.setTotalExcusedAbsense(map.getOrDefault(Status.EXCUSED_ABSENSE, 0L));
        dto.setTotalUnjustifiededAbsense(map.getOrDefault(Status.UNJUSTIFIED_ABSENSE, 0L));
        dto.setTotalNoShow(map.getOrDefault(Status.NO_SHOW, 0L));
        dto.setTotalScheduled(map.getOrDefault(Status.SCHEDULED, 0L));
        dto.setTotalPreScheduled(map.getOrDefault(Status.PRE_SCHEDULED, 0L));
        dto.setTotalOverdue(map.getOrDefault(Status.OVERDUE, 0L));
        return dto;
    }

    public Page<PatientReportReponseDTO> getPatientReportPages(
        Integer pageNumber,
        Integer pageLength,
        String patientName,
        String patientMotherName,
        String responsibleProfessionalName,
        String requestingProfessionalName,
        String status,
        String agreement,
        String priorit,
        String type,
        String serviceName,
        String instituitionName,
        LocalDateTime scheduledAt,
        LocalDateTime scheduledAtStart,
        LocalDateTime scheduledAtEnd,
        LocalDateTime createdAt,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd
    ){
        
        Specification<Appointment> specification = this.getReportSpecs(
          patientName,
          patientMotherName,
          responsibleProfessionalName,
          requestingProfessionalName,
          status,
          agreement,
          priorit,
          type,
          serviceName,
          instituitionName,
          scheduledAt,
          scheduledAtStart,
          scheduledAtEnd,
          createdAt,
          createdAtStart,
          createdAtEnd
        );
        
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(specification, pageRequest)
            .map(appointment -> new PatientReportReponseDTO(
                appointment.getPatient().getPerson().getName(),
                appointment.getPatient().getMotherName(),
                appointment.getScheduledAt(),
                appointment.getCreatedAt(),
                appointment.getStatus().getLabel(),
                appointment.getResponsibleProfessionalName(),
                appointment.getRequestingProfessionalName(),
                appointment.getPriorit().getLabel(),
                appointment.getServiceTypeName(),
                appointment.getServiceType().getType().getLabel()
            ));
    }

    /**
     * Validates the parameters and the row limit before anything is written,
     * so errors can still be returned as JSON. The returned writer streams the PDF in batches.
     */
    public Consumer<OutputStream> preparePatientReportPdf(
        PatientReportFilterDTO filter,
        List<String> fields,
        String sort
    ){
        List<PatientReportField> columns = PatientReportField.fromKeys(fields);

        String[] sortParts = (sort == null || sort.isBlank() ? "scheduledAt,desc" : sort).split(",");
        if (sortParts.length > 2) {
            throw new InvalidDataException("Ordenação inválida: " + sort + ". Use o formato campo,asc ou campo,desc.");
        }
        PatientReportField sortField = PatientReportField.fromKey(sortParts[0]);
        String direction = sortParts.length == 2 ? sortParts[1].trim() : "asc";
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new InvalidDataException("Direção de ordenação inválida: " + direction + ". Use asc ou desc.");
        }
        boolean descending = direction.equalsIgnoreCase("desc");

        Specification<Appointment> specification = this.getReportSpecs(filter);

        long total = repository.count(specification);
        if (total > reportPdfMaxRows) {
            throw new InvalidDataException(String.format(
                "A busca retornou %d registros e o limite para gerar o PDF é %d. Restrinja mais os filtros.",
                total,
                reportPdfMaxRows
            ));
        }

        var statistics = repositoryCustom.findPatientReportStatistics(specification);

        return outputStream -> patientReportPdfService.write(
            outputStream,
            columns,
            statistics,
            offset -> repositoryCustom.findPatientReportRows(
                specification,
                sortField,
                descending,
                offset,
                REPORT_PDF_BATCH_SIZE
            )
        );
    }

    private Specification<Appointment> getReportSpecs(PatientReportFilterDTO filter){
        return this.getReportSpecs(
            filter.patientName(),
            filter.patientMotherName(),
            filter.responsibleProfessionalName(),
            filter.requestingProfessionalName(),
            filter.status(),
            filter.agreement(),
            filter.priorit(),
            filter.type(),
            filter.serviceName(),
            filter.instituitionName(),
            filter.scheduledAt(),
            filter.scheduledAtStart(),
            filter.scheduledAtEnd(),
            filter.createdAt(),
            filter.createdAtStart(),
            filter.createdAtEnd()
        );
    }

    public Page<PatientAppointmentResponseDTO> getAllPatientAppointments(
        String token,
        Integer pageNumber,
        Integer pageLength
    ){
        Person person = jwtTokenProvider.getPerson(token);
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository.getAllAppointmentsOfPatient(person.getId(), pageRequest);
    }

    public List<PatientInfoAppointmentProjection> findAppointmentsByPatientId(long patientId){
        return repository.findByPatient_Id(patientId);
    }

    @Transactional
    public void setPreSchedulingTo(PreSchedulingRequestDTO preScheduling, String token){
        changeStatuses(preScheduling.getAppointmentsId(), Status.PRE_SCHEDULED,
            preScheduling.getStatusChangeReason(), jwtTokenProvider.getPerson(token));
    }

    @Transactional
    public void setNewStatusTo(NewCommonStatusDTO preScheduling, String token){
        Status newStatus = Status.fromLabel(preScheduling.getStatus());
        if(newStatus.getLabel().equals(Status.PRE_SCHEDULED.getLabel())){
            throw new InvalidDataException("Não é possível usar este status nesta rota!");
        }
        changeStatuses(preScheduling.getAppointmentsId(), newStatus,
            preScheduling.getStatusChangeReason(), jwtTokenProvider.getPerson(token));
    }

    private void changeStatuses(Long[] ids, Status newStatus, String reason, Person editor) {
        if (ids == null || Arrays.stream(ids).anyMatch(java.util.Objects::isNull)) {
            throw new InvalidDataException("Informe os IDs dos atendimentos!");
        }
        // Stable lock order prevents deadlocks between overlapping batches.
        for (Long id : Arrays.stream(ids).distinct().sorted().toList()) {
            Appointment appointment = findForUpdate(id);
            Status previousStatus = appointment.getStatus();
            if (previousStatus == newStatus) continue;
            appointment.setStatus(newStatus);
            appointment.setLastModifiedBy(editor);
            appointment.updatedNow();
            repository.save(appointment);
            statusHistoryService.record(appointment, previousStatus, editor, reason);
        }
    }

    public AuthorizationFormResponseDTO getAuthorizationForm(
        Long appointmentId
    ){
        Appointment appointment = this.repository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Agendamento não encontrado!"));
        if(appointment.getScheduledAt() == null){
            throw new InvalidDataException("Para gerar uma guia, é necessário uma data de agendamento!");
        }
        if(appointment.getInstituition() == null){
            throw new InvalidDataException("Para gerar uma guia, é necessário definir a instituição de destino!");
        }
        return new AuthorizationFormResponseDTO(
            new PatientDataResponseDTO(
                appointment.getPatient().getPerson().getName(),
                appointment.getPatient().getCns(),
                appointment.getPatient().getPerson().getSex(),
                appointment.getPatient().getPerson().getBirthday(),
                appointment.getPatient().getPerson().getAge(),
                appointment.getPatient().getPerson().getAddress()
            ),
            new AuthorizationIndentifyingResponseDTO(
                "Paiva/MG",
                appointment.getInstituitionCityNameSafe(),
                appointment.getScheduledAt(),
                new InstituitionResponseDTO(
                    appointment.getInstituitionNameSafe(),
                    appointment.getInstituitionCepSafe(),
                    appointment.getInstituitionCityNameSafe(),
                    appointment.getInstituitionAddressSafe(),
                    appointment.getInstituitionPhoneSafe(),
                    appointment.getInstituitionLinkLogoSafe()
                )
            ),
            new ProceduringDataResponseDTO(
                appointment.getServiceTypeName(),
                appointment.getServiceType().getType().getLabel(),
                appointment.getServiceType().getSigtapCode(),
                appointment.getServiceType().getCategoryGroupName()
            ),
            appointment.getNotes(),
            appointment.getEmployeeName(),
            LocalDate.now()
        );
    }
}
