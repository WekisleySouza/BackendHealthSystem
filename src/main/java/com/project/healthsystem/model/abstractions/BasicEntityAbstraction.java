package com.project.healthsystem.model.abstractions;

import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BasicEntityAbstraction extends IDAbstraction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="last_modified_by")
    @Setter
    protected Person lastModifiedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    @Setter
    protected Person createdBy;
    @Column(name="created_at")
    protected LocalDateTime createdAt;
    @Column(name="updated_at")
    protected LocalDateTime updatedAt;

    public void createdNow(){
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void updatedNow(){
            this.updatedAt = LocalDateTime.now();
    }
}
