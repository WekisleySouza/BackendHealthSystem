package com.project.healthsystem.security;

import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.RefreshToken;
import com.project.healthsystem.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {
    private final RefreshTokenRepository repository;

    @Value("${security.refresh.expiration}")
    private long refreshExpirationMs;

    public RefreshToken generateToken(Login login) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setLogin(login);
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        return rt;
    }
}
