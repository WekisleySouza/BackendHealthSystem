package builders;

import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.model.*;

import java.time.LocalDateTime;

public class AppointmentBuilder {

    private String notes;
    private Priority priorit;
    private Status status;
    private boolean isReturn;
    private LocalDateTime schedulingForecast;
    private LocalDateTime scheduledAt;
    private Agreement agreement;
    private Instituition instituition;
    private ServiceType serviceType;
    private Professional requestingProfessional;
    private Professional responsibleProfessional;
    private Employee employee;
    private Patient patient;

    public static AppointmentBuilder builder(){
        return new AppointmentBuilder();
    }
    
    public AppointmentBuilder withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public AppointmentBuilder withPriorit(Priority priorit) {
        this.priorit = priorit;
        return this;
    }

    public AppointmentBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public AppointmentBuilder withReturn(boolean isReturn) {
        this.isReturn = isReturn;
        return this;
    }

    public AppointmentBuilder withSchedulingForecast(LocalDateTime schedulingForecast) {
        this.schedulingForecast = schedulingForecast;
        return this;
    }

    public AppointmentBuilder withScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
        return this;
    }

    public AppointmentBuilder withAgreement(Agreement agreement) {
        this.agreement = agreement;
        return this;
    }

    public AppointmentBuilder withInstituition(Instituition instituition) {
        this.instituition = instituition;
        return this;
    }

    public AppointmentBuilder withServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public AppointmentBuilder withRequestingProfessional(Professional requestingProfessional) {
        this.requestingProfessional = requestingProfessional;
        return this;
    }

    public AppointmentBuilder withResponsibleProfessional(Professional responsibleProfessional) {
        this.responsibleProfessional = responsibleProfessional;
        return this;
    }

    public AppointmentBuilder withEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public AppointmentBuilder withPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public Appointment build() {
        Appointment appointment = new Appointment();
        appointment.setNotes(notes);
        appointment.setPriorit(priorit);
        appointment.setStatus(status);
        appointment.setReturn(isReturn);
        appointment.setSchedulingForecast(schedulingForecast);
        appointment.setScheduledAt(scheduledAt);
        appointment.setAgreement(agreement);
        appointment.setInstituition(instituition);
        appointment.setServiceType(serviceType);
        appointment.setRequestingProfessional(requestingProfessional);
        appointment.setResponsibleProfessional(responsibleProfessional);
        appointment.setEmployee(employee);
        appointment.setPatient(patient);

        return appointment;
    }
    
    public static AppointmentRequestDTO createValidRequestDTO(){
        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO();
        appointmentRequestDTO.setRequestingProfessionalId(1L);
        appointmentRequestDTO.setEmployeeId(1L);
        appointmentRequestDTO.setPatientId(1L);
        appointmentRequestDTO.setStatus(Status.COMPLETED.getLabel());
        appointmentRequestDTO.setServiceTypeId(1L);
        appointmentRequestDTO.setPriority(Priority.ELECTIVE.getLabel());
        return appointmentRequestDTO;
    }
}
