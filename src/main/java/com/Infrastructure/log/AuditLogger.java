package com.infrastructure.log;

import org.springframework.http.HttpStatus;

public interface AuditLogger {
    void repositoryLog(String username,
                       String tenantId,
                       String operation,
                       String entityName,
                       Object entityId,
                       Object entity,
                       String uuId);

    void exceptionLog(HttpStatus httpStatus,
                      String message,
                      String currentTime,
                      String uuid);
}
