package com.infrastructure.repository.log.impl;

import com.infrastructure.repository.log.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Log4jAuditLogger implements AuditLogger {
    private static final Logger log = LogManager.getLogger("audit");
    @Override
    public void log(String username,
                    String tenantId,
                    String operation,
                    String entityName,
                    Object entityId,
                    Object entity,
                    String uuId ) {

        log.info("USER={} TENANT={} OPERATION={} ENTITY_NAME={} ENTITY_ID={} ENTITY={} UUID={}",
                username, tenantId, operation, entityName, entityId, entity,uuId);
    }
}
