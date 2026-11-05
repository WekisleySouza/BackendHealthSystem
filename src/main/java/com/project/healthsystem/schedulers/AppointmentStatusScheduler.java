package com.project.healthsystem.schedulers;

import com.project.healthsystem.repository.AppointmentRepository;
import com.project.healthsystem.service.AppointmentStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class AppointmentStatusScheduler {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusHistoryService statusHistoryService;

    @Scheduled(fixedRate = 60000)
    public void updateStatuses() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        for (Long id : appointmentRepository.findAutomaticStatusChangeIds(now)) {
            statusHistoryService.updateAutomaticStatus(id, now);
        }
    }
}
