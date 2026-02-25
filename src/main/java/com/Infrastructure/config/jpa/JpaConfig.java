package com.infrastructure.config.jpa;


import org.hibernate.cfg.Environment;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class JpaConfig {
    private final TenantDataSourceManager tenantDataSourceManager;
    private final DataSource dataSource;

    public JpaConfig(DataSource dataSource, TenantDataSourceManager tenantDataSourceManager) {
        this.dataSource = dataSource;
        this.tenantDataSourceManager = tenantDataSourceManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       CustomMultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CustomTenantIdentifierResolver tenantIdentifierResolver) {

        LocalContainerEntityManagerFactoryBean factory = builder
                .dataSource(dataSource)
                .packages("com.infrastructure.model")
                .build();

        Map<String, Object> jpaProps = factory.getJpaPropertyMap();
        jpaProps.put("hibernate.multi_tenancy", "DATABASE");
        jpaProps.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        jpaProps.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);
        jpaProps.put(Environment.DIALECT, "org.hibernate.dialect.OracleDialect");

        // Redisson tenant-aware
        jpaProps.put("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.internal.JCacheRegionFactory");
        jpaProps.put("hibernate.cache.use_second_level_cache", true);
        jpaProps.put("hibernate.cache.use_query_cache", false);
        jpaProps.put("hibernate.cache.keys_factory", "com.infrastructure.config.jpa.TenantAwareCacheKeysFactory");
        jpaProps.put("hibernate.cache.provider_configuration_file_resource_path", "hibernate-redis.properties");
        jpaProps.put("hibernate.javax.cache.uri", "classpath:redisson-jcache.yaml");

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
