package com.infrastructure;

import lombok.Data;

@Data
public class Users {
    private Long id;
    private Boolean isAdmin;
    private Boolean isActive;
    private String username;
    private String password;
}
