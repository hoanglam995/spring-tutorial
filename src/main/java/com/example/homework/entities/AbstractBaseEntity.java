package com.example.homework.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @JsonIgnore
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @JsonIgnore
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonIgnore
    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
