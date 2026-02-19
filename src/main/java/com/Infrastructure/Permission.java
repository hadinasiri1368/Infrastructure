package com.infrastructure;

import lombok.Data;

@Data
public class Permission {
    private Long id;
    private String name;
    private Boolean requiresAuthentication;
}
