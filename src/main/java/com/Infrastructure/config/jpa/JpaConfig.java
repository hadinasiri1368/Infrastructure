package com.infrastructure.config.jpa;


import org.hibernate.cfg.Environment;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;



import javax.sql.DataSource;

@Configuration
public class JpaConfig {
    private final TenantDataSourceManager tenantDataSourceManager;
    private final DataSource dataSource;

    public JpaConfig(DataSource dataSource, TenantDataSourceManager tenantDataSourceManager) {
        this.dataSource = dataSource;
        this.tenantDataSourceManager = tenantDataSourceManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource, CustomMultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CustomTenantIdentifierResolver tenantIdentifierResolver) {
        LocalContainerEntityManagerFactoryBean factory = builder
                .dataSource(dataSource)
                .packages("org.fund")
                .build();
        factory.getJpaPropertyMap().put(Environment.DIALECT, "org.hibernate.dialect.OracleDialect");
        factory.getJpaPropertyMap().put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        factory.getJpaPropertyMap().put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        factory.getJpaPropertyMap().put("hibernate.multiTenancy", "DATABASE");

        return factory;
    }

    @Bean
    public CustomMultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new CustomMultiTenantConnectionProvider(tenantDataSourceManager,dataSource);
    }

    @Bean
    public CustomTenantIdentifierResolver tenantIdentifierResolver() {
        return new CustomTenantIdentifierResolver(tenantDataSourceManager);
    }
}
