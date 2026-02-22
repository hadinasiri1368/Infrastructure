package com.infrastructure.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "AHA_USER_ROLE")
@Entity(name = "userRole")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "F_USER_ID")
    private Users user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "F_ROLE_ID")
    private Role role;
}
