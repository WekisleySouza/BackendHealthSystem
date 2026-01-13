package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class RefreshToken extends IDAbstraction {
    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @ManyToOne(optional = false)
    @JoinColumn(name = "login_id")
    private Login login;
}
