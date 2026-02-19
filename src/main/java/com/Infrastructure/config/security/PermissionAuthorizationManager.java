package com.infrastructure.config.security;

import com.infrastructure.Permission;
import com.infrastructure.PermissionService;
import com.infrastructure.config.jpa.RequestContext;
import com.infrastructure.exceptions.AuthenticationExceptionType;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.util.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;


@Component
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PermissionService permissionService;

    public PermissionAuthorizationManager(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();
        HttpServletRequest request = context.getRequest();

        if (!permissionService.isAuthenticationRequired(request)) {
            return new AuthorizationDecision(true);
        }

        if (auth == null || !auth.isAuthenticated()) {
            throw new BaseException(AuthenticationExceptionType.AUTHENTICATION_FAILED);
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return new AuthorizationDecision(true);
        }

        String uri = request.getRequestURI();
        Permission permission = permissionService.findPermissionByUrl(uri);

        if (permission == null) {
            throw new BaseException(AuthenticationExceptionType.URL_NOT_FOUND, new Object[]{uri});
        }

        String token = RequestContext.getToken();
        if (permission.getRequiresAuthentication() && AppUtils.isNull(token)) {
            throw new BaseException(AuthenticationExceptionType.TOKEN_IS_NULL);
        }

        String requiredAuthority = permission.getName();
        boolean hasPermission = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(requiredAuthority));

        if (!hasPermission)
            throw new BaseException(AuthenticationExceptionType.DO_NOT_HAVE_ACCESS_TO_ADDRESS, new Object[]{uri});

        return new AuthorizationDecision(true);
    }
}