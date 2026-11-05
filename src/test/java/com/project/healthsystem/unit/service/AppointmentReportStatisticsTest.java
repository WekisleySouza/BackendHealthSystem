package com.project.healthsystem.unit.service;

import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.custom.AppointmentRepositoryImpl;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AppointmentReportStatisticsTest {
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void shouldAggregateAllCategoriesAndDeduplicateProfessionalsAcrossRoles() {
        EntityManager em = mock(EntityManager.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class, RETURNS_DEEP_STUBS);
        when(em.getCriteriaBuilder()).thenReturn(cb);
        TypedQuery grouped = mock(TypedQuery.class);
        TypedQuery responsible = mock(TypedQuery.class);
        TypedQuery requesting = mock(TypedQuery.class);
        TypedQuery patients = mock(TypedQuery.class);
        doReturn(grouped, responsible, requesting, patients).when(em).createQuery(any(CriteriaQuery.class));
        var groups = List.of(
            group(Status.COMPLETED, Priority.ELECTIVE, ServiceTypes.EXAM, 2L),
            group(Status.COMPLETED, Priority.URGENT, ServiceTypes.SPECIALTY, 1L),
            group(null, null, null, 1L));
        when(grouped.getResultList()).thenReturn(groups);
        when(responsible.getResultList()).thenReturn(List.of(10L, 11L));
        when(requesting.getResultList()).thenReturn(List.of(11L, 12L));
        when(patients.getResultList()).thenReturn(List.of(20L, 21L));
        Specification<Appointment> filter = mock(Specification.class);
        AppointmentRepositoryImpl repository = new AppointmentRepositoryImpl();
        ReflectionTestUtils.setField(repository, "entityManager", em);
        var result = repository.findPatientReportStatistics(filter);
        assertEquals(4, result.totalAppointments());
        assertEquals(2, result.uniquePatients());
        assertEquals(3, result.involvedProfessionals());
        assertEquals(3L, result.byStatus().get("Completo"));
        assertEquals(1L, result.byStatus().get("Não informado"));
        assertEquals(2L, result.byPriority().get("Eletivo"));
        assertEquals(1L, result.byPriority().get("Urgente"));
        assertEquals(2L, result.byServiceType().get("Exame"));
        verify(filter, times(4)).toPredicate(any(), any(), same(cb));
    }

    private Tuple group(Status status, Priority priority, ServiceTypes type, long count) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.get(0, Status.class)).thenReturn(status);
        when(tuple.get(1, Priority.class)).thenReturn(priority);
        when(tuple.get(2, ServiceTypes.class)).thenReturn(type);
        when(tuple.get(3, Long.class)).thenReturn(count);
        return tuple;
    }
}