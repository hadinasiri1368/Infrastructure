package com.infrastructure.repository;

import com.infrastructure.log.AuditLogger;
import com.infrastructure.log.EntityChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityChangedEventListener {

    private final AuditLogger auditLogger;

    @EventListener
    public void handle(EntityChangedEvent event) {
        auditLogger.repositoryLog(
                event.username(),
                event.tenantId(),
                event.operation(),
                event.entityName(),
                event.entityId(),
                event.entity(),
                event.uuId()
        );
    }
}
