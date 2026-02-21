package com.infrastructure.repository.log;

public interface AuditLogger {
    void log(String username,
             String tenantId,
             String operation,
             String entityName,
             Object entityId,
             Object entity,
             String uuId);
}
