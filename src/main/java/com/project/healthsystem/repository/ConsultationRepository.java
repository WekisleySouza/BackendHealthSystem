package com.project.healthsystem.repository;

import com.project.healthsystem.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
