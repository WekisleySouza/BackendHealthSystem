package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
@Data
@NoArgsConstructor
public class Appointment extends IDAbstraction {

    @Column(name = "notes")
    private String notes;
    @Column(name="scheduled_at")
    private LocalDateTime scheduledAt;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="priorit")
    private String priorit;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @ManyToOne
    private ServiceType serviceType;
    @ManyToOne
    private Professional professional;
    @ManyToOne
    private Employee employee;
    @ManyToOne
    private Person person;

    public void createdNow(){
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

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

    public long getPersonId(){
        if(this.person != null){
            return person.getId();
        }
        return -1;
    }

    public long getServiceTypeId(){
        if(this.serviceType != null){
            return serviceType.getId();
        }
        return -1;
    }
}
