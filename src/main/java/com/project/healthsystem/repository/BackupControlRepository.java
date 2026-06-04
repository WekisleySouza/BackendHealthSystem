package com.project.healthsystem.repository;

import com.project.healthsystem.model.BackupControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackupControlRepository extends JpaRepository<BackupControl, Long> {

    Optional<BackupControl> findTopByOrderByLastUpdateDesc();

    boolean existsBy();
}
