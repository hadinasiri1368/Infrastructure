package com.infrastructure.domain.permission.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PermissionDto {
    private Long id;
    private String name;
    private String url;
    private Boolean isSensitive;
}
