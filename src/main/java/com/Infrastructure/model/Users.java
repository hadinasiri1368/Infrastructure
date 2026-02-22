package com.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Table(name = "AHA_USERS")
@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Users extends BaseEntity{
    @Column(name = "IS_ACTIVE", columnDefinition = "NUMBER(1)", nullable = false)
    private Boolean isActive;
    @Column(name = "IS_ADMIN", columnDefinition = "NUMBER(1)", nullable = false)
    private Boolean isAdmin;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "F_PERSON_ID")
    private Person person;
    @Column(name = "F_VERIFICATION_CODE_ID", columnDefinition = "NUMBER(18)")
    private Long verificationCodeId;
    @Column(name = "USERNAME", columnDefinition = "NVARCHAR2(50)", nullable = false, unique = true)
    private String username;
    @Column(name = "PASSWORD", columnDefinition = "NVARCHAR2(200)", nullable = false)
    private String password;
}
