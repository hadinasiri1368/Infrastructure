package com.infrastructure;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

public class PermissionService {
    public boolean isAuthenticationRequired(HttpServletRequest request) {
        return false;
    }

    public Set<Permission> getPermissions(Users users) {
        return null;
    }

    public Permission findPermissionByUrl(String url) {
        return null;
    }
}
