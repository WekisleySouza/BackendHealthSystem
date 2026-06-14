package com.project.healthsystem.repository;

import com.project.healthsystem.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByLogin(String login);

    Optional<Login> findByPersonId(long personId);

    Optional<Login> findByPersonEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByPersonId(long id);
}
