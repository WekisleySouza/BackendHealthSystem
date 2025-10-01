package com.project.healthsystem.repository;

import com.project.healthsystem.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository  extends JpaRepository<Status, Long> {
}
