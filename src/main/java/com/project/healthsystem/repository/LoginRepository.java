package com.project.healthsystem.repository;

import com.project.healthsystem.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

}
