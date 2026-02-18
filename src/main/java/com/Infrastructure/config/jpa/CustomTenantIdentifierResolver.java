package com.infrastructure.config.jpa;

import com.infrastructure.util.AppUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CustomTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    private final TenantDataSourceManager tenantDataSourceManager;

    public CustomTenantIdentifierResolver(TenantDataSourceManager tenantDataSourceManager) {
        this.tenantDataSourceManager = tenantDataSourceManager;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        if (AppUtils.isNull(TenantContext.getCurrentTenant())) {
            return tenantDataSourceManager.getTenantDataSources()
                    .keySet()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No tenant configured"));
        }
        return TenantContext.getCurrentTenant();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
