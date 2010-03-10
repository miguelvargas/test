package com.mvwsolutions.common.dao.hibernate;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.CacheConcurrencyStrategy;
import org.hibernate.cache.CacheException;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.mapping.Collection;
import org.hibernate.persister.collection.OneToManyPersister;

/**
 * Persister that do not persist collection to DB. Needed by PlayerDataAccessService
 * @author smineyev
 *
 */
public class NulOneToManyPersister extends OneToManyPersister {

    public NulOneToManyPersister(Collection collection,
            CacheConcurrencyStrategy cache, Configuration cfg,
            SessionFactoryImplementor factory) throws MappingException,
            CacheException {
        super(collection, cache, cfg, factory);
    }

    @Override
    protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session) throws HibernateException {
        return 0;
    }
    
    

}
