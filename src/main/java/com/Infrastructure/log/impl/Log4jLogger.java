package com.infrastructure.log.impl;

import com.infrastructure.log.AuditLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Log4jLogger implements AuditLogger {
    private static final Logger repositoryLog = LogManager.getLogger("audit");
    private static final Logger exceptionLog = LogManager.getLogger("exception");

    @Override
    public void repositoryLog(String username,
                              String tenantId,
                              String operation,
                              String entityName,
                              Object entityId,
                              Object entity,
                              String uuId) {

        repositoryLog.info("USER={} TENANT={} OPERATION={} ENTITY_NAME={} ENTITY_ID={} ENTITY={} UUID={}",
                username, tenantId, operation, entityName, entityId, entity, uuId);
    }

    @Override
    public void exceptionLog(HttpStatus httpStatus, String message, String currentTime, String uuid) {
        exceptionLog.error("exception occurred: httpStatus={}, message={}, time={}, uuid={}", httpStatus, message, currentTime, uuid);
    }
}
