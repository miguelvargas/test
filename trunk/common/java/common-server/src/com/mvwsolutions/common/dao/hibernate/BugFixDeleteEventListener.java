package com.mvwsolutions.common.dao.hibernate;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * 
 * bug description:
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2146
 * http://lists.jboss.org/pipermail/hibernate-issues/2007-January/003099.html
 * 
 * @author smineyev
 * 
 */
public class BugFixDeleteEventListener extends DefaultDeleteEventListener {

    private static final long serialVersionUID = 2885464529422013461L;

    @SuppressWarnings("unchecked")
	protected void deleteTransientEntity(EventSource session, Object entity,
            boolean cascadeDeleteEnabled, EntityPersister persister,
            Set transientEntities) {
        super.deleteTransientEntity(session, entity, cascadeDeleteEnabled,
                persister, transientEntities == null ? new HashSet()
                        : transientEntities);
    }
}
