package com.infrastructure.domain.permission.service;

import com.infrastructure.domain.authentication.dto.UserPermissionDto;
import com.infrastructure.domain.permission.repository.PermissionRepository;
import com.infrastructure.model.Permission;
import com.infrastructure.model.Users;
import com.infrastructure.service.BaseService;
import com.infrastructure.util.AppUtils;
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
        return repository.findAll().stream()
                .filter(a -> a.getIsSensitive() && AppUtils.removeNumericPathVariables(requestUrl).toLowerCase().startsWith(a.getUrl().toLowerCase()))
                .findFirst()
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
        List<UserPermissionDto> userPermissionDtos = repository.findAllPermissionFromUser();
        userPermissionDtos.addAll(repository.findAllPermissionFromGroup());
        userPermissionDtos.addAll(repository.findAllPermissionFromRole());


        List<Permission> permissions = userPermissionDtos.stream()
                .filter(a -> a.getUserId().equals(userId))
                .map(UserPermissionDto::getPermission)
                .collect(Collectors.toList());
        return permissions;
    }
}
