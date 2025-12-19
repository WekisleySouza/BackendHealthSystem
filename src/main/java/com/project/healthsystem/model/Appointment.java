package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.BasicEntityAbstraction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
@Setter
@NoArgsConstructor
public class Appointment extends BasicEntityAbstraction {

    @Column(name = "notes")
    @Getter
    private String notes;
    @Column(name="priorit")
    @Getter
    private String priorit;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;
    @Column(name="scheduled_at")
    @Getter
    private LocalDateTime scheduledAt;

    @ManyToOne
    @Getter
    private ServiceType serviceType;
    @ManyToOne
    @Getter
    private Professional professional;
    @ManyToOne
    @Getter
    private Employee employee;
    @ManyToOne
    @Getter
    private Person person;

    public Status getStatus(){
        if(scheduledAt == null) return Status.PENDING_SCHEDULING;

        if(status != Status.COMPLETED && status != Status.NO_SHOW && status != Status.CANCELED){
            if(scheduledAt.isBefore(LocalDateTime.now())) return Status.SCHEDULED;
            if(scheduledAt.isAfter(LocalDateTime.now())) return Status.OVERDUE;
        }

        return this.status;
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
