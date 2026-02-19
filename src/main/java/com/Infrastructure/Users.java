package com.infrastructure;

import lombok.Data;

@Data
public class Users {
    private int id;
    private Boolean isAdmin;
    private Boolean isActive;
    private String username;
    private String password;
}
