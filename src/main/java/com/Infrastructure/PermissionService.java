package com.infrastructure;

import com.infrastructure.model.Permission;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
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
