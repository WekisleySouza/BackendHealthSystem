package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
@Data
@NoArgsConstructor
public class Appointment extends BasicEntityAbstraction {

    @Column(name = "notes")
    private String notes;
    @Enumerated(EnumType.STRING)
    @Column(name="priorit")
    private Priority priorit;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;
    @Column(name="is_return")
    private boolean isReturn;
    @Column(name = "scheduling_forecast")
    private LocalDateTime schedulingForecast;
    @Column(name="scheduled_at")
    private LocalDateTime scheduledAt;

    @ManyToOne
    private ServiceType serviceType;
    @ManyToOne
    private Professional requestingProfessional;
    @ManyToOne
    private Professional responsibleProfessional;
    @ManyToOne
    private Employee employee;
    @ManyToOne
    private Patient patient;

    public String getResponsibleProfessionalName(){
        if(this.responsibleProfessional != null){
            return responsibleProfessional.getPerson().getName();
        }
        return "";
    }

    public Long getResponsibleProfessionalId(){
        if(this.responsibleProfessional != null){
            return responsibleProfessional.getId();
        }
        return null;
    }

    public String getRequestingProfessionalName(){
        if(this.requestingProfessional != null){
            return requestingProfessional.getPerson().getName();
        }
        return "";
    }

    public Long getRequestingProfessionalId(){
        if(this.requestingProfessional != null){
            return requestingProfessional.getId();
        }
        return null;
    }

    public String getEmployeeName(){
        if(this.employee != null){
            return this.employee.getPerson().getName();
        }
        return "";
    }

    public String getPatientName(){
        if(this.patient != null){
            return patient.getPerson().getName();
        }
        return "";
    }

    public String getServiceTypeName(){
        if(this.serviceType != null){
            return serviceType.getName();
        }
        return "";
    }
    public String getServiceType(){
        if(this.serviceType != null){
            return serviceType.getType().getLabel();
        }
        return "";
    }

    public String getServiceTypeCategoryGroupName(){
        if(this.serviceType != null){
            return serviceType.getCategoryGroupName();
        }
        return "";
    }
}
