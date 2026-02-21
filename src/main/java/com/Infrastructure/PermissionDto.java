package com.infrastructure;

import lombok.Data;

@Data
public class PermissionDto {
    private Long id;
    private String name;
    private Boolean requiresAuthentication;
}
