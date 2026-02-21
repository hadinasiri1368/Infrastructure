package com.infrastructure.repository;

import com.infrastructure.config.jpa.TenantContext;
import com.infrastructure.config.security.RequestContext;
import com.infrastructure.model.BaseEntity;
import com.infrastructure.repository.log.EntityChangedEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

@Aspect
@Component
@RequiredArgsConstructor
public class RepositoryAuditAspect {

    private final ApplicationEventPublisher publisher;

    @Around("execution(* org.springframework.data.repository.CrudRepository+.save*(..)) || " +
            "execution(* org.springframework.data.repository.CrudRepository+.delete*(..))")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = joinPoint.proceed();

        String username = String.valueOf(RequestContext.getUserId());
        String tenantId = TenantContext.getCurrentTenant();
        String uuId = RequestContext.getUuid().toString();

        String operation = joinPoint.getSignature().getName();
        Object entity = joinPoint.getArgs()[0];

        publisher.publishEvent(
                new EntityChangedEvent(
                        username,
                        tenantId,
                        operation,
                        entity.getClass().getSimpleName(),
                        extractId(entity),
                        entity,
                        uuId
                )
        );

        return result;
    }

    private Object extractId(Object entity) {
        if (entity instanceof BaseEntity be) {
            return be.getId();
        } else if (entity instanceof Collection<?> coll) {
            return coll.stream()
                    .filter(e -> e instanceof BaseEntity)
                    .map(e -> ((BaseEntity) e).getId())
                    .toList();
        }
        return null;
    }
}
