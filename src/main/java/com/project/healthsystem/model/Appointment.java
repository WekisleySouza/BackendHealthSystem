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
    @Column(name="scheduled_at")
    private LocalDateTime scheduledAt;

    @ManyToOne
    private ServiceType serviceType;
    @ManyToOne
    private Professional professional;
    @ManyToOne
    private Employee employee;
    @ManyToOne
    private Patient patient;

    public long getProfessionalId(){
        if(this.professional != null){
            return professional.getId();
        }
        return -1;
    }

    public long getEmployeeId(){
        if(this.employee != null){
            return this.employee.getId();
        }
        return -1;
    }

    public long getPatientId(){
        if(this.patient != null){
            return patient.getId();
        }
        return -1;
    }

    public long getServiceTypeId(){
        if(this.serviceType != null){
            return serviceType.getId();
        }
        return -1;
    }

    public String getProfessionalName(){
        if(this.professional != null){
            return professional.getPerson().getName();
        }
        return "";
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
}
