package com.infrastructure.config.jpa;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class CustomMultiTenantConnectionProvider implements MultiTenantConnectionProvider {
    private final TenantDataSourceManager tenantDataSourceManager;
    private final DataSource dataSource;

    public CustomMultiTenantConnectionProvider(TenantDataSourceManager tenantDataSourceManager, DataSource dataSource) {
        this.tenantDataSourceManager = tenantDataSourceManager;
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection(Object object) throws SQLException {
        if (TenantContext.getCurrentTenant()!=null) {
            Connection connection = tenantDataSourceManager.getTenantDataSource(TenantContext.getCurrentTenant()).getConnection();
            return connection;
        }
        return dataSource.getConnection();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseConnection(Object object, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return unwrapType.isAssignableFrom(CustomMultiTenantConnectionProvider.class);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return unwrapType.cast(this);
        }
        log.info("Unwrapping " + unwrapType + " as CustomMultiTenantConnectionProvider");
        throw new IllegalArgumentException("Cannot unwrap to " + unwrapType.getName());
    }
}
