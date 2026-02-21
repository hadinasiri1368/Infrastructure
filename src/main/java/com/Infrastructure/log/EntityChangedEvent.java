package com.infrastructure.log;

public record EntityChangedEvent(
        String username,
        String tenantId,
        String operation,
        String entityName,
        Object entityId,
        Object entity,
        String uuId) {}
