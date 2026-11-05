package com.project.healthsystem.repository.custom;

import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportField;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportRowDTO;
import com.project.healthsystem.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;
import jakarta.persistence.Tuple;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportStatisticsDTO;

public class AppointmentRepositoryImpl implements AppointmentRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PatientReportStatisticsDTO findPatientReportStatistics(
        Specification<Appointment> spec
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Appointment> root = query.from(Appointment.class);
        Join<Appointment, ServiceType> service = root.join("serviceType", JoinType.LEFT);
        Predicate predicate = spec == null ? cb.conjunction() : spec.toPredicate(root, query, cb);
        query.multiselect(root.get("status"), root.get("priorit"), service.get("type"), cb.countDistinct(root.get("id")))
            .where(predicate == null ? cb.conjunction() : predicate)
            .groupBy(root.get("status"), root.get("priorit"), service.get("type"))
            .orderBy(List.of());

        Map<String, Long> statuses = new TreeMap<>();
        Map<String, Long> priorities = new TreeMap<>();
        Map<String, Long> types = new TreeMap<>();
        long total = 0;
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            Status status = tuple.get(0, Status.class);
            Priority priority = tuple.get(1, Priority.class);
            ServiceTypes type = tuple.get(2, ServiceTypes.class);
            long count = tuple.get(3, Long.class);
            total += count;
            statuses.merge(status == null ? "Não informado" : status.getLabel(), count, Long::sum);
            priorities.merge(priority == null ? "Não informada" : priority.getLabel(), count, Long::sum);
            types.merge(type == null ? "Não informado" : type.getLabel(), count, Long::sum);
        }
        // Union of both roles: the same person is counted only once.
        Set<Long> professionals = reportPersonIds(spec, "responsibleProfessional");
        professionals.addAll(reportPersonIds(spec, "requestingProfessional"));
        return new PatientReportStatisticsDTO(
            total, reportPersonIds(spec, "patient").size(), professionals.size(), statuses, priorities, types
        );
    }

    private Set<Long> reportPersonIds(Specification<Appointment> spec, String relation) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Appointment> root = query.from(Appointment.class);
        var person = root.join(relation, JoinType.LEFT).join("person", JoinType.LEFT);
        Predicate predicate = spec == null ? cb.conjunction() : spec.toPredicate(root, query, cb);
        query.select(person.get("id")).distinct(true)
            .where(cb.and(predicate == null ? cb.conjunction() : predicate, cb.isNotNull(person.get("id"))))
            .orderBy(List.of());
        return new HashSet<>(entityManager.createQuery(query).getResultList());
    }
    @Override
    public List<AppointmentSummaryDTO> getSummary(Specification<Appointment> spec){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppointmentSummaryDTO> cq = cb.createQuery(AppointmentSummaryDTO.class);
        Root<Appointment> root = cq.from(Appointment.class);

        Predicate predicate = cb.conjunction();

        if (spec != null) {
            Predicate p = spec.toPredicate(root, cq, cb);
            if (p != null) {
                predicate = p;
            }
        }
        cq.select(cb.construct(AppointmentSummaryDTO.class, root.get("status"), cb.count(root)))
            .where(predicate)
            .groupBy(root.get("status"));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<PatientReportRowDTO> findPatientReportRows(
        Specification<Appointment> spec,
        PatientReportField sortField,
        boolean descending,
        int offset,
        int limit
    ){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PatientReportRowDTO> cq = cb.createQuery(PatientReportRowDTO.class);
        Root<Appointment> root = cq.from(Appointment.class);

        // LEFT JOINs so appointments with optional relations still appear, matching the count query
        Join<Appointment, Patient> patient = root.join("patient", JoinType.LEFT);
        Join<Patient, Person> patientPerson = patient.join("person", JoinType.LEFT);
        Join<Appointment, Professional> responsibleProfessional = root.join("responsibleProfessional", JoinType.LEFT);
        Join<Professional, Person> responsiblePerson = responsibleProfessional.join("person", JoinType.LEFT);
        Join<Appointment, Professional> requestingProfessional = root.join("requestingProfessional", JoinType.LEFT);
        Join<Professional, Person> requestingPerson = requestingProfessional.join("person", JoinType.LEFT);
        Join<Appointment, ServiceType> serviceType = root.join("serviceType", JoinType.LEFT);
        Join<Appointment, Agreement> agreement = root.join("agreement", JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        if (spec != null) {
            Predicate p = spec.toPredicate(root, cq, cb);
            if (p != null) {
                predicate = p;
            }
        }

        Expression<?> sortExpression = switch (sortField) {
            case PATIENT_NAME -> patientPerson.get("name");
            case MOTHER_NAME -> patient.get("motherName");
            case SCHEDULED_AT -> root.get("scheduledAt");
            case CREATED_AT -> root.get("createdAt");
            case STATUS -> root.get("status");
            case RESPONSIBLE_PROFESSIONAL_NAME -> responsiblePerson.get("name");
            case REQUESTING_PROFESSIONAL_NAME -> requestingPerson.get("name");
            case PRIORIT -> root.get("priorit");
            case SERVICE_NAME -> serviceType.get("name");
            case SERVICE_TYPE -> serviceType.get("type");
            case AGREEMENT -> agreement.get("name");
        };

        cq.select(cb.construct(
                PatientReportRowDTO.class,
                patientPerson.get("name"),
                patient.get("motherName"),
                root.get("scheduledAt"),
                root.get("createdAt"),
                root.get("status"),
                responsiblePerson.get("name"),
                requestingPerson.get("name"),
                root.get("priorit"),
                serviceType.get("name"),
                serviceType.get("type"),
                agreement.get("name")
            ))
            .where(predicate)
            // id as tie-breaker keeps the order stable between batches
            .orderBy(
                descending ? cb.desc(sortExpression) : cb.asc(sortExpression),
                cb.asc(root.get("id"))
            );

        return entityManager.createQuery(cq)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }
}
