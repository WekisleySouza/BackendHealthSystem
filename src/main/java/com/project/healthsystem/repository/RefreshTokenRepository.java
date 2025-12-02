package com.project.healthsystem.repository;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findByToken(String token);

    public void deleteByLogin(Login login);
}
