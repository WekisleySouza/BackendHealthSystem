package com.project.healthsystem.service;

import com.project.healthsystem.exceptions.AccessDeniedException;
import com.project.healthsystem.model.Login;
import com.project.healthsystem.model.RefreshToken;
import com.project.healthsystem.repository.RefreshTokenRepository;
import com.project.healthsystem.security.RefreshTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;
    private final LoginService loginService;

    @Transactional
    public String createRefreshToken(Login login) {
        refreshTokenRepository.deleteByLogin(login);
        RefreshToken token = refreshTokenProvider.generateToken(login);
        refreshTokenRepository.save(token);

        return token.getToken();
    }

    public Login validate(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
            .orElseThrow(() -> new AccessDeniedException("Refresh token inválido!"));

        if (token.isRevoked()) {
            throw new AccessDeniedException("Refresh token revogado!");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new AccessDeniedException("Refresh token expirado!");
        }

        return token.getLogin();
    }

    public void revoke(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }
}
