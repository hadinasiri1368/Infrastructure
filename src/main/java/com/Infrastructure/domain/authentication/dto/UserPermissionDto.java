package com.infrastructure.domain.authentication.dto;

import com.infrastructure.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserPermissionDto {
    private Permission permission;
    private Long userId;
}
