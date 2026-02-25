package com.infrastructure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_seq")
    @SequenceGenerator(name = "hibernate_seq", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
    protected Long id;

    @CreatedDate
    @Column(name = "inserted_date_time", nullable = false, updatable = false)
    protected LocalDateTime insertedDateTime;

    @CreatedBy
    @Column(name = "inserted_user_id", nullable = false, updatable = false)
    protected Long insertedUserId;

    @LastModifiedDate
    @Column(name = "updated_date_time")
    protected LocalDateTime updatedDateTime;

    @LastModifiedBy
    @Column(name = "updated_user_id")
    protected Long updatedUserId;
}

