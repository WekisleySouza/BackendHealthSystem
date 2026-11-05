package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.datasus.Cid10Repository;
import com.project.healthsystem.repository.datasus.Ciap2Repository;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ProfessionalRepository professionalRepository;
    private final InstituitionRepository instituitionRepository;
    private final AgreementRepository agreementRepository;

    private final AppointmentsMapper appointmentsMapper;
    private final Cid10Repository cid10Repository;
    private final Ciap2Repository ciap2Repository;

    public Appointment validateSave(AppointmentRequestDTO appointmentRequestDTO){
        validateRegulationFields(appointmentRequestDTO);
        validateClinicalReasonCatalog(appointmentRequestDTO, null);
        Appointment appointment = appointmentsMapper.toEntity(appointmentRequestDTO);

        Patient patient = patientRepository.findById(appointmentRequestDTO.getPatientId())
            .orElseThrow(() -> new InvalidDataException("Patient inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional requestingProfessional = professionalRepository.findById(appointmentRequestDTO.getRequestingProfessionalId())
            .orElseThrow(() -> new InvalidDataException("CnesProfessional requisitante inválido!"));

        if(
            appointmentRequestDTO.getAgreementId() != null &&
            appointmentRequestDTO.getAgreementId() != -1
        ){
            Agreement agreement = agreementRepository.findById(appointmentRequestDTO.getAgreementId())
                .orElseThrow(() -> new InvalidDataException("Agreement(convênio) inválida!"));
            appointment.setAgreement(agreement);
        }

        if(appointmentRequestDTO.getResponsibleProfessionalId() != null &&
            appointmentRequestDTO.getResponsibleProfessionalId() != -1
        ){
            Professional responsibleProfessional = professionalRepository.findById(appointmentRequestDTO.getResponsibleProfessionalId())
                .orElseThrow(() -> new InvalidDataException("CnesProfessional responsável inválido!"));
            appointment.setResponsibleProfessional(responsibleProfessional);
        }

        if(appointmentRequestDTO.getInstituitionId() != null &&
            appointmentRequestDTO.getInstituitionId() != -1
        ){
            Instituition instituition = instituitionRepository.findById(appointmentRequestDTO.getInstituitionId())
                .orElseThrow(() -> new InvalidDataException("Instituition inválida!"));
            appointment.setInstituition(instituition);
        }

        appointment.setRequestingProfessional(requestingProfessional);
        appointment.setServiceType(serviceType);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);
        setInstituitionRoles(appointment, appointmentRequestDTO);
        appointment.createdNow();

        return appointment;
    }

    public Appointment validateUpdate(AppointmentRequestDTO appointmentRequestDTO, long id){
        validateRegulationFields(appointmentRequestDTO);
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
        return updateValidated(appointmentRequestDTO, appointment);
    }

    public Appointment validateUpdate(AppointmentRequestDTO appointmentRequestDTO, Appointment appointment) {
        validateRegulationFields(appointmentRequestDTO);
        return updateValidated(appointmentRequestDTO, appointment);
    }

    private Appointment updateValidated(AppointmentRequestDTO appointmentRequestDTO, Appointment appointment) {
        validateClinicalReasonCatalog(appointmentRequestDTO, appointment);
        Patient patient = patientRepository.findById(appointmentRequestDTO.getPatientId())
            .orElseThrow(() -> new InvalidDataException("Patient inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));

        if(appointmentRequestDTO.getAgreementId() != null &&
            appointmentRequestDTO.getAgreementId() != -1
        ){
            Agreement agreement = agreementRepository.findById(appointmentRequestDTO.getAgreementId())
                    .orElseThrow(() -> new InvalidDataException("Agreement(convênio) inválida!"));
            appointment.setAgreement(agreement);
        }

        if(appointmentRequestDTO.getResponsibleProfessionalId() != null &&
            appointmentRequestDTO.getResponsibleProfessionalId() != -1
        ){
            Professional responsibleProfessional = professionalRepository.findById(appointmentRequestDTO.getResponsibleProfessionalId())
                .orElseThrow(() -> new InvalidDataException("CnesProfessional responsável inválido!"));
            appointment.setResponsibleProfessional(responsibleProfessional);
        }
        Professional requestingProfessional = professionalRepository.findById(appointmentRequestDTO.getRequestingProfessionalId())
            .orElseThrow(() -> new InvalidDataException("CnesProfessional requisitante inválido!"));

        if(appointmentRequestDTO.getInstituitionId() != null &&
            appointmentRequestDTO.getInstituitionId() != -1
        ){
            Instituition instituition = instituitionRepository.findById(appointmentRequestDTO.getInstituitionId())
                .orElseThrow(() -> new InvalidDataException("Instituition inválida!"));
            appointment.setInstituition(instituition);
        }

        setInstituitionRoles(appointment, appointmentRequestDTO);
        appointment = appointmentsMapper.toEntityWhenHasId(appointment, appointmentRequestDTO);
        appointment.setServiceType(serviceType);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);
        appointment.setRequestingProfessional(requestingProfessional);

        return appointment;
    }

    private void validateRegulationFields(AppointmentRequestDTO dto) {
        CareModality.fromLabel(dto.getCareModality());
        RequestCharacter.fromLabel(dto.getRequestCharacter());
        if (dto.getRequestedAt() != null && dto.getRequestedAt().isAfter(java.time.LocalDateTime.now())) {
            throw new InvalidDataException("A data da solicitação não pode estar no futuro!");
        }
        if (dto.getExecutingCbo() != null && !dto.getExecutingCbo().matches("[0-9]{6}")) {
            throw new InvalidDataException("O CBO executante deve conter seis dígitos!");
        }
        String system = dto.getClinicalReasonSystem();
        String code = dto.getClinicalReasonCode();
        if ((system == null) != (code == null)) {
            throw new InvalidDataException("Informe o sistema e o código do motivo clínico juntos!");
        }
        if (system != null) {
            if (!system.equals("CID-10") && !system.equals("CIAP-2")) {
                throw new InvalidDataException("Sistema do motivo clínico inválido: utilize CID-10 ou CIAP-2!");
            }
            if (code.isBlank() || code.length() > 20) {
                throw new InvalidDataException("O código do motivo clínico deve ter entre 1 e 20 caracteres!");
            }
        }
    }

    private void validateClinicalReasonCatalog(AppointmentRequestDTO dto, Appointment existing) {
        String system = dto.getClinicalReasonSystem();
        String code = dto.getClinicalReasonCode();
        if (system == null) return;
        if (existing != null
            && system.equals(existing.getClinicalReasonSystem())
            && code.equals(existing.getClinicalReasonCode())) {
            return;
        }
        boolean exists = system.equals("CID-10")
            ? cid10Repository.existsByCode(code)
            : ciap2Repository.existsByCode(code);
        if (!exists) {
            throw new InvalidDataException(
                "Código '" + code + "' não encontrado no catálogo " + system + "!");
        }
    }

    private void setInstituitionRoles(Appointment appointment, AppointmentRequestDTO dto) {
        Instituition requesting = resolveInstituition(dto.getRequestingInstituitionId(),
            appointment.getRequestingInstituition(), "solicitante");
        Instituition regulating = resolveInstituition(dto.getRegulatingInstituitionId(),
            appointment.getRegulatingInstituition(), "reguladora");
        Instituition author = resolveInstituition(dto.getAuthorInstituitionId(),
            appointment.getAuthorInstituition(), "autora");
        Instituition executing = resolveInstituition(dto.getExecutingInstituitionId(),
            appointment.getExecutingInstituition(), "executante");
        appointment.setRequestingInstituition(requesting);
        appointment.setRegulatingInstituition(regulating);
        appointment.setAuthorInstituition(author);
        appointment.setExecutingInstituition(executing);
    }

    private Instituition resolveInstituition(Long id, Instituition current, String role) {
        if (id == null) {
            return current;
        }
        if (id <= 0) {
            throw new InvalidDataException("Instituição " + role + " inválida!");
        }
        return instituitionRepository.findById(id)
            .orElseThrow(() -> new InvalidDataException("Instituição " + role + " inválida!"));
    }

    public AppointmentGetByIdProjection validateFindById(long id){
        return appointmentRepository.findAppointmentById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
    }

    public Appointment validateDelete(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
    }

    public Appointment validateSetNewStatusTo(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Um dos exames/especialidades não foi encontrado!"));
    }
}
