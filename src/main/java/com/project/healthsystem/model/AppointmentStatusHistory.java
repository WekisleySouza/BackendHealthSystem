package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_appointment_status_history", indexes = {
    @Index(name = "idx_appointment_status_history_appointment", columnList = "appointment_id,recorded_at,id")
})
@Getter
@Setter
@NoArgsConstructor
public class AppointmentStatusHistory extends IDAbstraction {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;
    @Enumerated(EnumType.STRING)
    private Status previousStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status newStatus;
    @Column(nullable = false)
    private LocalDateTime occurredAt;
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
    // Null identifies an automatic change made by the system.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private Person changedBy;
    @Column(length = 1000)
    private String reason;
}
