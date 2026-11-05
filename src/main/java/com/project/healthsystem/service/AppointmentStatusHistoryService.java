package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.basic_responses.AppointmentStatusHistoryResponseDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class AppointmentStatusHistoryService {
    private final AppointmentStatusHistoryRepository historyRepository;
    private final AppointmentRepository appointmentRepository;

    private static final EnumSet<Status> AUTOMATIC_STATUSES = EnumSet.of(
        Status.SCHEDULED, Status.OVERDUE, Status.PENDING_SCHEDULING);

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(Appointment appointment, Status previousStatus, Person changedBy, String reason) {
        if (previousStatus == appointment.getStatus()) return;
        if (reason != null && reason.length() > 1000) {
            throw new InvalidDataException("A justificativa não pode ultrapassar 1000 caracteres!");
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        AppointmentStatusHistory history = new AppointmentStatusHistory();
        history.setAppointment(appointment);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(appointment.getStatus());
        history.setOccurredAt(now);
        history.setRecordedAt(now);
        history.setChangedBy(changedBy);
        history.setReason(reason == null || reason.isBlank() ? null : reason.strip());
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentStatusHistoryResponseDTO> findByAppointment(long id, int pageNumber, int pageLength) {
        if (pageNumber < 0 || pageLength < 1 || pageLength > 100) {
            throw new InvalidDataException("Página inválida: use pageNumber >= 0 e pageLength entre 1 e 100!");
        }
        if (!appointmentRepository.existsById(id)) {
            throw new NotFoundException("Atendimento não encontrado!");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageLength,
            Sort.by(Sort.Direction.DESC, "recordedAt", "id"));
        return historyRepository.findByAppointmentId(id, pageable).map(history ->
            new AppointmentStatusHistoryResponseDTO(
                history.getId(),
                history.getPreviousStatus() == null ? null : history.getPreviousStatus().getLabel(),
                history.getNewStatus().getLabel(),
                history.getOccurredAt(),
                history.getRecordedAt(),
                history.getChangedBy() == null ? null : history.getChangedBy().getId(),
                history.getChangedBy() == null ? "Sistema" : history.getChangedBy().getName(),
                history.getReason()
            ));
    }

    // Called once per candidate by the scheduler: each row has its own transaction.
    @Transactional
    public void updateAutomaticStatus(long id, LocalDateTime now) {
        Appointment appointment = appointmentRepository.findByIdForUpdate(id).orElse(null);
        if (appointment == null || !AUTOMATIC_STATUSES.contains(appointment.getStatus())) return;
        Status previous = appointment.getStatus();
        LocalDateTime scheduledAt = appointment.getScheduledAt();
        Status next = scheduledAt == null ? Status.PENDING_SCHEDULING
            : scheduledAt.isBefore(now) ? Status.OVERDUE
            : scheduledAt.isAfter(now) ? Status.SCHEDULED : previous;
        if (previous == next) return;
        appointment.setStatus(next);
        appointmentRepository.save(appointment);
        record(appointment, previous, null, null);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteForAppointment(long id) {
        historyRepository.deleteByAppointmentId(id);
    }
}
