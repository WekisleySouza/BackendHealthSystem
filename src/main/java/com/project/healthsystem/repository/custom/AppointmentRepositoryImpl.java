package com.project.healthsystem.repository.custom;

import com.project.healthsystem.controller.dto.reports_patients.AppointmentSummaryDTO;
import com.project.healthsystem.model.Appointment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AppointmentRepositoryImpl implements AppointmentRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

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
}
