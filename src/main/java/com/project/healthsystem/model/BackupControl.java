package com.project.healthsystem.model;

import com.project.healthsystem.model.abstractions.IDAbstraction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_control")
@NoArgsConstructor
public class BackupControl extends IDAbstraction {
    @Column(name = "total_updated")
    @Getter
    @Setter
    private Long totalUpdated;
    @Column(name = "total_created")
    @Getter
    @Setter
    private Long totalCreated;
    @Column(name = "stated_at")
    @Getter
    private LocalDateTime startedAt;
    @Column(name = "finished_at")
    @Getter
    private LocalDateTime finishedAt;
    @Column(name = "last_update")
    @Getter
    private LocalDateTime lastUpdate;

    public void startBackup(){
        this.startedAt = LocalDateTime.now();
    }

    public void finishBackup(){
        this.finishedAt = LocalDateTime.now();
        this.lastUpdate = LocalDateTime.now();
    }
}
