package com.infrastructure.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.constants.DateFormat;
import com.infrastructure.constants.TimeFormat;
import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.ExceptionDto;
import com.infrastructure.util.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        int status = HttpStatus.FORBIDDEN.value();
        Object[] params = null;

        if (e.getCause() instanceof BaseException fe) {
            status = fe.getStatus().value();
            params = ((BaseException) fe).getParams();
        }

        String currentTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DateFormat.GREGORIAN.getValue() + " " + TimeFormat.HOUR_MINUTE_SECOND.getValue()));
        log.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}",
                status, e.getMessage(), currentTime, RequestContext.getUuid());

        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(convertObjectToJson(ExceptionDto.builder()
                .code(e.getMessage())
                .message(AppUtils.getMessageFromMessageSource(e.getMessage(), params))
                .uuid(RequestContext.getUuid())
                .time(currentTime)
                .build()));
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
