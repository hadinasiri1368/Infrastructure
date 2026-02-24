package com.infrastructure.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.config.jpa.TenantContext;
import com.infrastructure.constants.DateFormat;
import com.infrastructure.constants.TimeFormat;
import com.infrastructure.domain.authentication.service.AuthenticationService;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.ExceptionDto;
import com.infrastructure.util.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
public class Logout implements LogoutHandler {
    private final AuthenticationService service;

    public Logout(AuthenticationService service) {
        this.service = service;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String token = AppUtils.getToken(request);
            service.logout(token);
        } catch (BaseException e) {
            handleException(response, e.getMessage(), e.getParams());
        } catch (Exception e) {
            handleException(response, "general_exception.Unknown_error", null);
        } finally {
            TenantContext.clear();
            RequestContext.clear();
        }
    }

    private void handleException(HttpServletResponse response, String message, Object[] params) {
        String currentTime = getCurrentTime();
        String uuid = RequestContext.getUuid();

        log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                HttpStatus.UNAUTHORIZED, message, currentTime, uuid);

        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(convertObjectToJson(ExceptionDto.builder()
                    .code(message)
                    .message(AppUtils.getMessageFromMessageSource(message, params))
                    .uuid(uuid)
                    .time(currentTime)
                    .build()));
        } catch (Exception ex) {
            log.error("failed to write logout response: {}", ex.getMessage(), ex);
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(
                DateFormat.GREGORIAN.getValue() + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        return object == null ? null : new ObjectMapper().writeValueAsString(object);
    }
}
