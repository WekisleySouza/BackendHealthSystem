package com.project.healthsystem.e2e;

import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.service.AppointmentStatusHistoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AppointmentStatusHistoryService.class)
@Testcontainers(disabledWithoutDocker = true)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AppointmentStatusHistoryPersistenceTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        properties.add("spring.datasource.username", POSTGRES::getUsername);
        properties.add("spring.datasource.password", POSTGRES::getPassword);
        properties.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        properties.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
    @Autowired AppointmentRepository appointments;
    @Autowired AppointmentStatusHistoryRepository histories;
    @Autowired AppointmentStatusHistoryService history;
    @Autowired PlatformTransactionManager transactions;

    TransactionTemplate tx() { return new TransactionTemplate(transactions); }

    @BeforeEach
    void clear() {
        tx().executeWithoutResult(s -> {
            histories.deleteAllInBatch();
            appointments.deleteAllInBatch();
        });
    }

    long create(Status status, LocalDateTime scheduledAt) {
        return tx().execute(s -> {
            Appointment appointment = new Appointment();
            appointment.setStatus(status);
            appointment.setScheduledAt(scheduledAt);
            return appointments.saveAndFlush(appointment).getId();
        });
    }

    @Test
    void statusAndHistoryRollbackTogether() {
        long id = create(Status.PENDING_SCHEDULING, null);
        assertThrows(IllegalStateException.class, () -> tx().executeWithoutResult(s -> {
            Appointment appointment = appointments.findByIdForUpdate(id).orElseThrow();
            appointment.setStatus(Status.COMPLETED);
            history.record(appointment, Status.PENDING_SCHEDULING, null, "teste");
            histories.flush();
            throw new IllegalStateException("rollback");
        }));
        assertEquals(Status.PENDING_SCHEDULING, appointments.findById(id).orElseThrow().getStatus());
        assertEquals(0, histories.count());
    }

    @Test
    void candidateQueryAndAutomaticRecordingAreIdempotent() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        long overdue = create(Status.SCHEDULED, now.minusDays(1));
        create(Status.COMPLETED, now.minusDays(1));
        create(Status.PRE_SCHEDULED, now.minusDays(1));
        create(Status.SCHEDULED, now.plusDays(1));
        assertEquals(java.util.List.of(overdue), appointments.findAutomaticStatusChangeIds(now));
        history.updateAutomaticStatus(overdue, now);
        assertTrue(appointments.findAutomaticStatusChangeIds(now).isEmpty());
        history.updateAutomaticStatus(overdue, now);
        assertEquals(1, histories.count());
        assertEquals(Status.OVERDUE, appointments.findById(overdue).orElseThrow().getStatus());
        assertEquals("Sistema", history.findByAppointment(overdue, 0, 20).getContent().get(0).changedByName());
    }

    @Test
    void deletingHistoryThenAppointmentSatisfiesForeignKey() {
        long id = create(Status.PENDING_SCHEDULING, null);
        tx().executeWithoutResult(s -> history.record(appointments.findByIdForUpdate(id).orElseThrow(), null, null, null));
        tx().executeWithoutResult(s -> {
            Appointment appointment = appointments.findByIdForUpdate(id).orElseThrow();
            history.deleteForAppointment(id);
            appointments.delete(appointment);
        });
        assertFalse(appointments.existsById(id));
        assertEquals(0, histories.count());
    }

    @Test
    void concurrentChangesKeepCorrectPreviousStatus() throws Exception {
        long id = create(Status.PENDING_SCHEDULING, null);
        CountDownLatch firstLocked = new CountDownLatch(1);
        CountDownLatch secondStarted = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> tx().executeWithoutResult(s -> {
                Appointment appointment = appointments.findByIdForUpdate(id).orElseThrow();
                firstLocked.countDown();
                try {
                    if (!secondStarted.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Second transaction did not start");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(ex);
                }
                appointment.setStatus(Status.SCHEDULED);
                history.record(appointment, Status.PENDING_SCHEDULING, null, null);
            }));
            assertTrue(firstLocked.await(5, TimeUnit.SECONDS));
            Future<?> second = executor.submit(() -> tx().executeWithoutResult(s -> {
                secondStarted.countDown();
                Appointment appointment = appointments.findByIdForUpdate(id).orElseThrow();
                Status previous = appointment.getStatus();
                appointment.setStatus(Status.COMPLETED);
                history.record(appointment, previous, null, null);
            }));
            first.get(10, TimeUnit.SECONDS);
            second.get(10, TimeUnit.SECONDS);
            var events = histories.findByAppointmentId(id, PageRequest.of(0,20,Sort.by("id"))).getContent();
            assertEquals(2, events.size());
            assertEquals(Status.PENDING_SCHEDULING, events.get(0).getPreviousStatus());
            assertEquals(Status.SCHEDULED, events.get(1).getPreviousStatus());
            assertEquals(Status.COMPLETED, events.get(1).getNewStatus());
        } finally {
            executor.shutdownNow();
        }
    }
}
