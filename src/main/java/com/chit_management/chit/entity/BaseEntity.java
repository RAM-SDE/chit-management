package com.chit_management.chit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * All entities extend this.
 * UUID stored as CHAR(36) — MariaDB 10.4 compatible.
 * Auto-generated on persist via @PrePersist.
 */
@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {

    @Column(
            name             = "uuid",
            columnDefinition = "CHAR(36)",
            unique           = true,
            nullable         = false,
            updatable        = false
    )
    private String uuid;

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
