package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.controller.dto.reports_patients.ReportAppointmentGraphResponseDTO;
import com.project.healthsystem.controller.dto.reports_professional.NumberAppointmentsByStatusAndProfessionalDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import com.project.healthsystem.repository.projections.PatientInfoAppointmentProjection;
import com.project.healthsystem.repository.specs.AppointmentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    private final AppointmentValidator appointmentValidator;
    private final AppointmentsMapper appointmentsMapper;

    private final JwtTokenProvider jwtTokenProvider;

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

        return this.repository.save(appointment);
    }

    public Appointment update(AppointmentRequestDTO appointmentRequestDTO, long id, String token){
        Appointment appointment = appointmentValidator.validateUpdate(appointmentRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        currentEditor.setLastModifiedBy(currentEditor);
        currentEditor.updatedNow();
        return repository.save(appointment);
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
            String professionalName,
            String employeeName,
            String patientName,
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
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(serviceType));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeNameLike(serviceTypeName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeCategoryGroupNameLike(categoryName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.professionalNameLike(professionalName));
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
                appointmentProjection.getProfessional().getId(),
                appointmentProjection.getProfessional().getPerson().getName()
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
            appointmentProjection.getStatus().getLabel(),
            appointmentProjection.getNotes(),
            appointmentProjection.getPriorit().getLabel(),
            appointmentProjection.getIsReturn(),

            appointmentProjection.getScheduledAt(),
            appointmentProjection.getCreatedAt(),
            appointmentProjection.getSchedulingForecast()
        );
    }

    public void delete(long id){
        Appointment apointment = appointmentValidator.validateDelete(id);
        repository.delete(apointment);
    }

    public ReportAppointmentGraphResponseDTO getPatientReportGraphInfo(
        String patientName,
        String patientMotherName,
        String professionalName,
        String status,
        String priorit,
        String type,
        String serviceName,
        LocalDateTime scheduledAt
    ){
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.patientNameLike(patientName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.patientMotherNameLike(patientMotherName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.professionalNameLike(professionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusLike(status));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritLike(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(type));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeNameLike(serviceName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        Long total = repository.count(specification);
        List<AppointmentSummaryDTO> summaryList = repository.getSummary(
//            patientName,
//            patientMotherName,
//            professionalName,
//            status,
//            priorit,
//            type,
//            serviceName,
//            scheduledAt
        );
        Map<Status, Long> map = summaryList.stream()
            .collect(Collectors.toMap(
                AppointmentSummaryDTO::status,
                AppointmentSummaryDTO::total
            ));
        ReportAppointmentGraphResponseDTO dto =  new ReportAppointmentGraphResponseDTO();
        dto.setTotal(total);
        dto.setTotalPendingScheduling(map.getOrDefault(Status.PENDING_SCHEDULING, 0L));
        dto.setTotalScheduled(map.getOrDefault(Status.SCHEDULED, 0L));
        dto.setTotalCompleted(map.getOrDefault(Status.COMPLETED, 0L));
        dto.setTotalCanceled(map.getOrDefault(Status.CANCELED, 0L));
        dto.setTotalNoShow(map.getOrDefault(Status.NO_SHOW, 0L));
        dto.setTotalOverdue(map.getOrDefault(Status.OVERDUE, 0L));
        return dto;
    }

    public ReportAppointmentByPatientResponseDTO getPatientReport(
        Integer pageNumber,
        Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Page<AppointmentReportResponseDTO> appointmentReportResponseDTOS = repository.findAppointmentReport(pageRequest);
        List<AppointmentStatusCountResponseDTO> appointmentStatusCountResponseDTOS = repository.countByStatus();
        return new ReportAppointmentByPatientResponseDTO(appointmentReportResponseDTOS, appointmentStatusCountResponseDTOS);
    }

    public Page<NumberAppointmentsByStatusAndProfessionalDTO> countAppointmentsByProfessional(
        Integer pageNumber,
        Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository.countAppointmentsGroupedByProfessional(
            pageRequest
        );
    }

    public Page<NumberSpecialtiesByStatusDTO> countSpecialtiesByStatus(
        Integer pageNumber,
        Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .countSpecialtiesByStatus(pageRequest);
    }

    public Page<NumberExamsByStatusDTO> countExamsByStatus(
            Integer pageNumber,
            Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
                .countExamsByStatus(pageRequest);
    }

    public Page<TestDTO> test(
        Integer pageNumber,
        Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.appointmentReport());
        return repository
            .findAll(specification, pageRequest)
            .map(AppointmentService::toReportDTO);
    }

    public static TestDTO toReportDTO(Appointment appointment) {
        return new TestDTO(
                appointment.getPatient().getPerson().getName(),
                appointment.getPatient().getMotherName(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getProfessional().getPerson().getName()
        );
    }

    public List<PatientInfoAppointmentProjection> findAppointmentsByPatientId(long patientId){
        return repository.findByPatient_Id(patientId);
    }
}
