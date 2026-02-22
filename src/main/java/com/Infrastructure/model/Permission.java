package com.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Table(name = "AHA_PERMISSION")
@Entity(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission extends BaseEntity {
    @Column(columnDefinition = "NVARCHAR2(50)", name = "NAME", nullable = false)
    private String name;
    @Column(columnDefinition = "NVARCHAR2(300)", name = "URL", nullable = false)
    private String url;
    @Column(columnDefinition = "NUMBER(1)", name = "IS_SENSITIVE", nullable = false)
    private Boolean isSensitive;
}
