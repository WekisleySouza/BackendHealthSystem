package com.project.healthsystem.e2e;

import com.project.healthsystem.controller.AppointmentsController;
import com.project.healthsystem.controller.dto.basic_requests.*;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.schedulers.AppointmentStatusScheduler;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.service.*;
import com.project.healthsystem.validator.AppointmentValidator;
import com.project.healthsystem.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AppointmentStatusHistoryTest {
    private final AppointmentRepository appointments = mock(AppointmentRepository.class);
    private final AppointmentStatusHistoryRepository histories = mock(AppointmentStatusHistoryRepository.class);
    private final AppointmentValidator validator = mock(AppointmentValidator.class);
    private final JwtTokenProvider jwt = mock(JwtTokenProvider.class);
    private final AppointmentStatusHistoryService history = new AppointmentStatusHistoryService(histories, appointments);
    private final AppointmentService service = new AppointmentService(appointments, null, validator, null, jwt, null, history);
    private final Person editor = new Person();
    private Appointment appointment;

    @BeforeEach
    void setup() {
        editor.setId(7L);
        editor.setName("Operador");
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Status.PENDING_SCHEDULING);
        when(jwt.getPerson("token")).thenReturn(editor);
        when(appointments.findByIdForUpdate(1L)).thenReturn(Optional.of(appointment));
        when(appointments.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private AppointmentStatusHistory savedHistory() {
        var captor = ArgumentCaptor.forClass(AppointmentStatusHistory.class);
        verify(histories).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void initialStatusRecordedWithUserAndCurrentDates() {
        var dto = new AppointmentRequestDTO();
        dto.setCreatedAt(LocalDateTime.of(2000, 1, 1, 0, 0));
        dto.setStatusChangeReason("  Pedido recebido  ");
        when(validator.validateSave(dto)).thenReturn(appointment);
        assertSame(appointment, service.save(dto, "token"));
        var event = savedHistory();
        assertNull(event.getPreviousStatus());
        assertEquals(Status.PENDING_SCHEDULING, event.getNewStatus());
        assertSame(editor, event.getChangedBy());
        assertEquals("Pedido recebido", event.getReason());
        assertEquals(event.getOccurredAt(), event.getRecordedAt());
        assertTrue(event.getRecordedAt().isAfter(dto.getCreatedAt()));
    }

    @Test
    void updateCapturesStatusBeforeValidatorMutatesEntity() {
        var dto = new AppointmentRequestDTO();
        dto.setStatusChangeReason("Realizado");
        when(validator.validateUpdate(dto, appointment)).thenAnswer(inv -> {
            appointment.setStatus(Status.COMPLETED);
            return appointment;
        });
        service.update(dto, 1L, "token");
        var event = savedHistory();
        assertEquals(Status.PENDING_SCHEDULING, event.getPreviousStatus());
        assertEquals(Status.COMPLETED, event.getNewStatus());
        assertEquals(editor, appointment.getLastModifiedBy());
    }

    @Test
    void editWithoutStatusChangeDoesNotRecordEvent() {
        var dto = new AppointmentRequestDTO();
        when(validator.validateUpdate(dto, appointment)).thenReturn(appointment);
        service.update(dto, 1L, "token");
        verifyNoInteractions(histories);
    }

    @Test
    void batchUsesStableLockOrderAndDoesNotDuplicateIdsOrUnchangedStatuses() {
        Appointment second = new Appointment();
        second.setId(2L);
        second.setStatus(Status.COMPLETED);
        when(appointments.findByIdForUpdate(2L)).thenReturn(Optional.of(second));
        var dto = new NewCommonStatusDTO();
        dto.setStatus(Status.COMPLETED.getLabel());
        dto.setAppointmentsId(new Long[]{2L, 1L, 1L});
        dto.setStatusChangeReason("Conferência");
        service.setNewStatusTo(dto, "token");
        var order = inOrder(appointments);
        order.verify(appointments).findByIdForUpdate(1L);
        order.verify(appointments).save(appointment);
        order.verify(appointments).findByIdForUpdate(2L);
        assertEquals("Conferência", savedHistory().getReason());
    }

    @Test
    void preSchedulingRecordsActorAndReason() {
        var dto = new PreSchedulingRequestDTO();
        dto.setAppointmentsId(new Long[]{1L});
        dto.setStatusChangeReason("Preparação");
        service.setPreSchedulingTo(dto, "token");
        assertEquals(Status.PRE_SCHEDULED, savedHistory().getNewStatus());
        assertEquals(Status.PRE_SCHEDULED, appointment.getStatus());
    }

    @Test
    void commonStatusRouteStillRejectsPreScheduled() {
        var dto = new NewCommonStatusDTO();
        dto.setStatus(Status.PRE_SCHEDULED.getLabel());
        dto.setAppointmentsId(new Long[]{1L});
        assertThrows(InvalidDataException.class, () -> service.setNewStatusTo(dto, "token"));
        verifyNoInteractions(histories);
    }

    @Test
    void automaticChangeIsRecordedOnceAsSystem() {
        LocalDateTime now = LocalDateTime.now();
        appointment.setScheduledAt(now.minusDays(1));
        history.updateAutomaticStatus(1L, now);
        history.updateAutomaticStatus(1L, now.plusMinutes(1));
        var event = savedHistory();
        assertEquals(Status.OVERDUE, event.getNewStatus());
        assertNull(event.getChangedBy());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"COMPLETED", "CANCELED", "PRE_SCHEDULED", "NO_SHOW", "EXCUSED_ABSENSE", "UNJUSTIFIED_ABSENSE"})
    void schedulerRechecksAndPreservesManualProtectedStatus(Status status) {
        appointment.setStatus(status);
        appointment.setScheduledAt(LocalDateTime.now().minusDays(1));
        history.updateAutomaticStatus(1L, LocalDateTime.now());
        assertEquals(status, appointment.getStatus());
        verifyNoInteractions(histories);
    }

    @Test
    void automaticFutureAndMissingDatesUseExistingRules() {
        LocalDateTime now = LocalDateTime.now();
        appointment.setScheduledAt(now.plusDays(1));
        history.updateAutomaticStatus(1L, now);
        assertEquals(Status.SCHEDULED, appointment.getStatus());
        appointment.setScheduledAt(null);
        history.updateAutomaticStatus(1L, now);
        assertEquals(Status.PENDING_SCHEDULING, appointment.getStatus());
        verify(histories, times(2)).save(any());
    }

    @Test
    void exactScheduledTimeAndDeletedCandidateDoNotGenerateEvents() {
        LocalDateTime now = LocalDateTime.now();
        appointment.setScheduledAt(now);
        history.updateAutomaticStatus(1L, now);
        when(appointments.findByIdForUpdate(2L)).thenReturn(Optional.empty());
        history.updateAutomaticStatus(2L, now);
        verifyNoInteractions(histories);
    }

    @Test
    void schedulerDelegatesCandidatesToTransactionalService() {
        var delegate = mock(AppointmentStatusHistoryService.class);
        when(appointments.findAutomaticStatusChangeIds(any())).thenReturn(List.of(1L, 2L));
        new AppointmentStatusScheduler(appointments, delegate).updateStatuses();
        verify(delegate).updateAutomaticStatus(eq(1L), any());
        verify(delegate).updateAutomaticStatus(eq(2L), any());
    }

    @Test
    void deletionRemovesHistoryBeforeAppointment() {
        service.delete(1L);
        var order = inOrder(appointments, histories);
        order.verify(appointments).findByIdForUpdate(1L);
        order.verify(histories).deleteByAppointmentId(1L);
        order.verify(appointments).delete(appointment);
    }

    @Test
    void readUsesDeterministicPaginationAndSystemLabel() {
        when(appointments.existsById(1L)).thenReturn(true);
        var event = new AppointmentStatusHistory();
        event.setId(4L);
        event.setNewStatus(Status.SCHEDULED);
        when(histories.findByAppointmentId(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(event), PageRequest.of(0,20), 1));
        var page = history.findByAppointment(1L, 0, 20);
        assertEquals("Sistema", page.getContent().get(0).changedByName());
        assertNull(page.getContent().get(0).changedById());
        verify(histories).findByAppointmentId(1L,
            PageRequest.of(0,20,Sort.by(Sort.Direction.DESC,"recordedAt","id")));
    }

    @Test
    void historyEndpointReturnsPagedDtoAndNoSensitiveUserFields() throws Exception {
        when(appointments.existsById(1L)).thenReturn(true);
        var event = new AppointmentStatusHistory();
        event.setId(4L);
        event.setNewStatus(Status.SCHEDULED);
        event.setChangedBy(editor);
        when(histories.findByAppointmentId(eq(1L), any()))
            .thenReturn(new PageImpl<>(List.of(event), PageRequest.of(0,20), 1));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AppointmentsController(service, history)).build();
        mvc.perform(get("/appointments/1/status-history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].changedById").value(7))
            .andExpect(jsonPath("$.content[0].changedByName").value("Operador"))
            .andExpect(jsonPath("$.content[0].changedBy").doesNotExist())
            .andExpect(jsonPath("$.content[0].newStatus").value(Status.SCHEDULED.getLabel()));
    }

    @Test
    void missingAppointmentAndInvalidPaginationAreRejected() {
        assertThrows(NotFoundException.class, () -> history.findByAppointment(99L,0,20));
        assertThrows(InvalidDataException.class, () -> history.findByAppointment(1L,-1,20));
        assertThrows(InvalidDataException.class, () -> history.findByAppointment(1L,0,0));
        assertThrows(InvalidDataException.class, () -> history.findByAppointment(1L,0,101));
    }

    @Test
    void oversizedReasonRejectedAndBlankReasonStoredAsNull() {
        assertThrows(InvalidDataException.class, () -> history.record(appointment,null,editor,"x".repeat(1001)));
        history.record(appointment,null,editor,"   ");
        assertNull(savedHistory().getReason());
    }
}
