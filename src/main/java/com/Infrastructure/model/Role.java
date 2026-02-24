package com.infrastructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Table(name = "AHA_Role")
@Entity(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseEntity implements Serializable {
    @Column(columnDefinition = "NVARCHAR2(50)", name = "NAME", nullable = false)
    private String name;
}
