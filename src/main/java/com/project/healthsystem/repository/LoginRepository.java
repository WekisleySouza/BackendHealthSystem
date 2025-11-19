package com.project.healthsystem.repository;

import com.project.healthsystem.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByLogin(String login);

    boolean existsByLogin(String login);
}
