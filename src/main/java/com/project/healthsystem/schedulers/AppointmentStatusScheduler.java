package com.project.healthsystem.schedulers;

import com.project.healthsystem.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class AppointmentStatusScheduler {
    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateStatuses(){
        LocalDateTime now = LocalDateTime.now(
            ZoneId.of("America/Sao_Paulo")
        );

        appointmentRepository.updateToOverdue(now);
        appointmentRepository.updateToScheduled(now);
        appointmentRepository.updateToPending();

    }
}
