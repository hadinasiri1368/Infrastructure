package com.infrastructure.domain.authentication.service;

import com.infrastructure.domain.authentication.dto.UserPermissionDto;
import com.infrastructure.domain.authentication.repository.PermissionRepository;
import com.infrastructure.model.Permission;
import com.infrastructure.model.Users;
import com.infrastructure.service.BaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService extends BaseService<Permission, Long> {
    @Value("${authentication.paths-to-bypass}")
    private List<String> pathsToBypass;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public boolean isAuthenticationRequired(HttpServletRequest request) {
        if (request.getMethod().equals("OPTIONS")) {
            return false;
        }

        if (isBypassedUrl(request.getRequestURI())) {
            return false;
        }

        Permission permission = findPermissionByUrl(request.getRequestURI());
        if (permission == null) {
            return true;
        }
        return permission.getIsSensitive();
    }

    public Permission findPermissionByUrl(String requestUrl) {
        List<Permission> permissions = findAll();
        return permissions.stream()
                .filter(p -> p.getUrl() != null && pathMatcher.match(p.getUrl(), requestUrl))
                .min(Comparator.comparing(Permission::getUrl, pathMatcher.getPatternComparator(requestUrl)))
                .orElse(null);


    }

    @Cacheable(value = "user-permissions", keyGenerator = "tenantAwareKeyGenerator")
    public Set<Permission> getPermissions(Users user) {
        if (user.getIsAdmin()) {
            return new HashSet<>(findAll());
        }

        return getUserPermissions(user.getId()).stream().collect(Collectors.toSet());
    }

    public boolean isBypassedUrl(String requestUrl) {
        for (String path : pathsToBypass) {
            if (pathMatcher.match(path.trim(), requestUrl)) {
                return true;
            }
        }
        return false;
    }

    private List<Permission> getUserPermissions(Long userId) {
        List<Permission> permissions = repository.findAllUserPermission().stream()
                .filter(a -> a.userId().equals(userId))
                .map(UserPermissionDto::permission)
                .collect(Collectors.toList());
        return permissions;
    }
}
