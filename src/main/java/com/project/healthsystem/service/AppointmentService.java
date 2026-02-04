package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.specs.AppointmentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            String status
        ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.notesLike(notes));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtEqual(createdAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritLike(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusLike(status));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.serviceTypeLike(serviceType));
        return repository
            .findAll(specification, pageRequest)
            .map(appointmentsMapper::toDto);
    }

    public AppointmentResponseDTO findById(long id){
        Appointment appointment = appointmentValidator.validateFindById(id);
        return appointmentsMapper.toDto(appointment);
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

    public AppointmentReportByProfessionalResponseDTO getProfessionalReport(
            Integer pageNumber,
            Integer pageLength
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Page<AppointmentReportCountByProfessionalResponseDTO> reportsByProfessional = repository.countAppointmentsByProfessional(
            pageRequest
        );
        Page<AppointmentReportCountByStatusByProfessionalResponseDTO> reportsByStatusAndProfessional = repository.countByProfessionalAndStatus(
            pageRequest
        );
        Page<AppointmentReportCountByServiceTypeByProfessionalResponse> reportsByServiceTypeAndProfessional = repository.countByProfessionalAndServiceType(
            pageRequest
        );
        long professionalsNumber = professionalRepository.countProfessionals();
        return new AppointmentReportByProfessionalResponseDTO(
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

}
