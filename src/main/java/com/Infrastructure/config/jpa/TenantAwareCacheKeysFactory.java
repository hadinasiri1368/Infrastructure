package com.infrastructure.config.jpa;

import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class TenantAwareCacheKeysFactory extends DefaultCacheKeysFactory {
    @Override
    public Object createEntityKey(
            Object id,
            EntityPersister persister,
            SessionFactoryImplementor factory,
            String tenantIdentifier) {

        return "jpa_catche:" + tenantIdentifier + ":" + persister.getEntityName() + ":" + id;
    }
}
