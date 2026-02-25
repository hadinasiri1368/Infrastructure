package com.infrastructure.domain.authentication.dto;

import com.infrastructure.model.Permission;

public record UserPermissionDto(
        Long userId,
        Permission permission
) {}
