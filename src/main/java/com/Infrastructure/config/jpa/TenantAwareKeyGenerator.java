package com.infrastructure.config.jpa;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component("tenantAwareKeyGenerator")
public class TenantAwareKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String tenantId = TenantContext.getCurrentTenant();
        String baseKey = method.getName() + Arrays.deepHashCode(params);
        return "spring_catche:" + tenantId + ":" + baseKey;
    }
}
