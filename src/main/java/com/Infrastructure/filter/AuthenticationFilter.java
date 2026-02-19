package com.infrastructure.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.Permission;
import com.infrastructure.PermissionService;
import com.infrastructure.Users;
import com.infrastructure.config.jpa.RequestContext;
import com.infrastructure.config.jpa.TenantContext;
import com.infrastructure.config.security.CustomUserDetails;
import com.infrastructure.constants.Consts;
import com.infrastructure.constants.DateFormat;
import com.infrastructure.constants.TimeFormat;
import com.infrastructure.exceptions.AuthenticationExceptionType;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.ExceptionDto;
import com.infrastructure.exceptions.GeneralExceptionType;
import com.infrastructure.util.AppUtils;
import com.infrastructure.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private final PermissionService permissionService;

    public AuthenticationFilter(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws IOException {
        try {
            String token = AppUtils.getToken(request);
            if (AppUtils.isNull(token))
                throw new BaseException(AuthenticationExceptionType.TOKEN_IS_NULL);

            String tenantId = request.getHeader(Consts.HEADER_TENANT_PARAM_NAME);
            if (AppUtils.isNull(tenantId))
                throw new BaseException(GeneralExceptionType.SCHEMAID_ID_IS_NULL);

            Users user = (Users) JwtUtil.getTokenData(token);

            setupSecurityContext(request, user);
            setTenantId(tenantId);

            String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.GREGORIAN.getValue() + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            log.info("RequestURL: {} | Start Date : {} | uuid : {}", request.getRequestURL(), startTime, RequestContext.getUuid());

            filterChain.doFilter(request, response);

            String endTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(DateFormat.GREGORIAN.getValue() + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
            log.info("RequestURL: {} | Start Date : {} | End Date : {} | uuid : {}", request.getRequestURL(), startTime, endTime, RequestContext.getUuid());

        } catch (BaseException e) {
            handleException(response, e, e.getStatus());
        } catch (Exception e) {
            handleException(response, new BaseException(GeneralExceptionType.UNKNOWN_ERROR), HttpStatus.UNAUTHORIZED);
        } finally {
            RequestContext.clear();
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private void setupSecurityContext(HttpServletRequest request, Users user) {
        UserDetails userDetails = getUserDetails(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, AppUtils.getToken(request), userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void setTenantId(String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
    }

    private void handleException(HttpServletResponse response, BaseException e, HttpStatus status) throws IOException {
        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DateFormat.GREGORIAN.getValue() + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
        log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                status, e.getMessage(), currentTime, RequestContext.getUuid());

        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(convertObjectToJson(ExceptionDto.builder()
                .code(e.getMessage())
                .message(AppUtils.getMessageFromMessageSource(e.getMessage(), e.getParams()))
                .uuid(RequestContext.getUuid())
                .time(currentTime)
                .build()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return permissionService.isAuthenticationRequired(request);
    }

    private UserDetails getUserDetails(Users user) {
        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        if (user.getIsAdmin())
            auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        else {
            Set<Permission> permissions = permissionService.getPermissions(user);
            for (Permission perm : permissions) {
                auths.add(new SimpleGrantedAuthority(perm.getName()));
            }
        }
        return new CustomUserDetails(user, true, true, true, auths);
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
