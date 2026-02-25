package com.infrastructure.config.jpa;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component("tenantAwareKeyGenerator")
public class TenantAwareKeyGenerator implements KeyGenerator {

    private static final String PREFIX = "spring_cache";

    @Override
    public Object generate(Object target, Method method, Object... params) {

        String tenantId = TenantContext.getCurrentTenant();
        String className = target.getClass().getSimpleName();
        String methodName = method.getName();

        String paramsPart = Arrays.stream(params)
                .map(param -> param == null ? "null" : param.toString())
                .collect(Collectors.joining("_"));

        return String.format(
                "%s:%s:%s:%s:%s",
                PREFIX,
                tenantId,
                className,
                methodName,
                paramsPart
        );
    }
}