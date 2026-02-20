package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.*;
import com.project.healthsystem.controller.dto.appointment_get_by_id.PatientInfoResponseDTO;
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

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final ProfessionalRepository professionalRepository;

    private final AppointmentValidator appointmentValidator;
    private final AppointmentsMapper appointmentsMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Appointment save(AppointmentRequestDTO appointmentRequestDTO, String token){
        Appointment appointment = appointmentValidator.validateSave(appointmentRequestDTO);

        // Auditory
        Person currentEditor = jwtTokenProvider.getPerson(token);
        appointment.createdNow();
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
            Integer pageNumber,
            Integer pageLength,
            String notes,
            LocalDateTime scheduledAt,
            LocalDateTime createdAt,
            String priorit,
            String status,
            String professionalName,
            String employeeName,
            String patientName
        ){
        Sort sort = Sort.by("serviceType.name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.notesLike(notes));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtEqual(createdAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritLike(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusLike(status));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(serviceType));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.professionalNameLike(professionalName));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.employeeNameLike(employeeName));
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
            appointmentProjection.getStatus().getLabel(),
            appointmentProjection.getNotes(),
            appointmentProjection.getPriorit().getLabel(),
            appointmentProjection.getScheduledAt(),
            appointmentProjection.getCreatedAt()
        );
    }

    public void delete(long id){
        Appointment apointment = appointmentValidator.validateDelete(id);
        repository.delete(apointment);
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

    public ReportAppointmentByProfessionalResponseDTO getProfessionalReport(
            Integer pageNumber,
            Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Page<ReportAppointmentCountByProfessionalResponseDTO> reportsByProfessional = repository.countAppointmentsByProfessional(
            pageRequest
        );
        Page<ReportAppointmentCountByStatusByProfessionalResponseDTO> reportsByStatusAndProfessional = repository.countByProfessionalAndStatus(
            pageRequest
        );
        Page<ReportAppointmentCountByServiceTypeByProfessionalResponse> reportsByServiceTypeAndProfessional = repository.countByProfessionalAndServiceType(
            pageRequest
        );
        long professionalsNumber = professionalRepository.countProfessionals();
        return new ReportAppointmentByProfessionalResponseDTO(
            reportsByProfessional,
            reportsByStatusAndProfessional,
            reportsByServiceTypeAndProfessional,
            professionalsNumber
        );
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
