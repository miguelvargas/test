package com.mvwsolutions.common.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.mvwsolutions.common.dao.BaseDao;
import com.mvwsolutions.common.dao.DataAccessException;
import com.mvwsolutions.common.dao.SortingAndPagingDescriptor;
import com.mvwsolutions.common.dao.SortingAndPagingDescriptor.SortingOption;



/**
 * This class is the Hibernate based implementaion of BaseDao (Data access
 * object) it must be extended by all Hibernate based DAO Implementations for
 * domain model objects to perform the CRUD(create ,update,delete) operations on
 * those domain model objects .
 * 
 * @author smineyev, with permission.  Modified by Marcus
 * 
 */
public abstract class HibernateBaseDaoImpl<T, ID extends Serializable> extends
        HibernateDaoSupport implements BaseDao<T, ID> {

    private final static Logger logger = LoggerFactory
            .getLogger(HibernateBaseDaoImpl.class);

    protected static final int ALL_RESULTS = 0;

    protected static final int START_INDEX = 0;

    protected static final int INITIAL_PARENT_ID = 0;

    private Class<T> persistentEntityClass;

    /**
     * constructor.
     */
    @SuppressWarnings("unchecked")
    public HibernateBaseDaoImpl() {
        this.persistentEntityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * @return the persistentEntityClass
     */
    protected Class<T> getPersistentEntityClass() {
        return persistentEntityClass;
    }

    public T merge(T transObj) throws DataAccessException {
        logger.debug("merging  " + transObj + " instance");
        try {
            if (transObj == null) {
                throw new DataAccessException("Invalid instance to merge: "
                        + transObj);
            }
            getHibernateTemplate().merge(transObj);
            logger.debug("merge successful");
        } catch (Exception ex) {
            throw new DataAccessException("merge failed.", ex);
        }
        return transObj;
    }


    public T saveOrUpdate(T transObj) throws DataAccessException {
        logger.debug("saving  " + transObj + " instance");
        try {
            if (transObj == null) {
                throw new DataAccessException("Invalid instance to save: "
                        + transObj);
            }
            getHibernateTemplate().saveOrUpdate(transObj);
            logger.debug("save successful");
        } catch (Exception ex) {
            throw new DataAccessException("save failed.", ex);
        }
        return transObj;
    }

    
    public List<T> saveOrUpdateAll(List<T> objList)
            throws DataAccessException {
        logger.debug("saving All instances");
        try {
            if ((objList == null)
                    || ((objList != null) && (objList.size() == 0))) {
                throw new DataAccessException(
                        "Invalid instance list to save: " + objList);
            }
            this.getHibernateTemplate().saveOrUpdateAll(objList);
            if (logger.isDebugEnabled()) {
                logger.debug("save All instances successful");
            }

        } catch (Exception ex) {
            throw new DataAccessException("save All instances failed.", ex);
        }
        return objList;
    }


    public boolean deleteById(final ID id) throws DataAccessException {
        boolean res = (Boolean) getHibernateTemplate().execute(new HibernateCallback<Boolean>() {

            public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(
                        "DELETE FROM " + getPersistentEntityClass().getName() 
                        + " WHERE id=:id");
                query.setParameter("id", id);
                int r = query.executeUpdate();
                return r > 0;
            }
            
        });
        
        return res;
    }
    
    public void delete(T transObj) throws DataAccessException {
        logger.debug("deleting instance");
        if (transObj == null) {
            throw new DataAccessException("Invalid instance to delete: "
                    + transObj);
        }
        try {
            this.getHibernateTemplate().delete(transObj);
            if (logger.isDebugEnabled()) {
                logger.debug("delete successful..");
            }
        } catch (Exception ex) {
            throw new DataAccessException("delete failed", ex);
        }
    }

    
    public void deleteAll(List<T> objList) throws DataAccessException {
        logger.debug("deleting all instances");
        try {
            if (objList == null) {
                throw new DataAccessException(
                        "Invalid instance List to delete: " + objList);
            }
            this.getHibernateTemplate().deleteAll(objList);
            if (logger.isDebugEnabled()) {
                logger.debug("delete all instances successful..");
            }
        } catch (Exception ex) {
            throw new DataAccessException("delete all instances failed", ex);
        }
    }

    
    public T findById(final ID id) throws DataAccessException {
        return findById(id, getPersistentEntityClass());
    }

    public <C> C findById(ID id, Class<C> expectedClass)
            throws DataAccessException {
        logger.debug("finding ById (ID: " + id + ")");
        C domainEntity = null;
        if (id == null) {
            throw new DataAccessException("Invalid id for load: " + id);
        }
        try {
            domainEntity = (C) this.getHibernateTemplate().get(expectedClass,
                    id);
        } catch (Exception ex) {
            throw new DataAccessException("findById Failed.(ID: " + id + ")",
                    ex);
        }
        return domainEntity;
    }

    @SuppressWarnings("unchecked")
    public Iterator<T> iterateOverAll() {
        return this.getHibernateTemplate().iterate(
                "from " + getPersistentEntityClass().getName());
    }

    
    public List<T> findAll() throws DataAccessException {
        logger.debug("findAll ");
        return findAll(START_INDEX, ALL_RESULTS);
    }


    public List<T> findAll(final int maxResults) throws DataAccessException {
        logger.debug(" findAll(maxResults)");
        return findAll(START_INDEX, maxResults);
    }

    
    @SuppressWarnings("unchecked")
    public List<T> findAll(final int startIndex, final int maxResults)
            throws DataAccessException {
        List<T> list = null;
        try {
            list = (this.getHibernateTemplate()
                    .executeFind(new HibernateCallback() {
                        List<T> tempresultList = new ArrayList<T>();

                        public Object doInHibernate(Session session) {
                            Criteria critria = session
                                    .createCriteria(getPersistentEntityClass());
                            if (startIndex > 0) {
                                critria.setFirstResult(startIndex);
                            }
                            if (maxResults > 0) {
                                critria.setMaxResults(maxResults);
                            }
                            tempresultList = critria.list();
                            return tempresultList;
                        }
                    }));
        } catch (Exception ex) {
            throw new DataAccessException("findAll(maxResults)  failed", ex);
        }
        return list;
    }


    public List<T> findByExample(T exampleObject) throws DataAccessException {
        // calling findbyexample without any sorting order.
        List<T> retList = findByExample(exampleObject, null, null, null);
        return retList;
    }
    
    public List<T> findByExample(T exampleObject, int maxResults)
            throws DataAccessException {
        // calling findbyexample without any sorting order.
        List<T> retList = findByExample(exampleObject, null, null, maxResults);
        return retList;
    }
    

    public List<T> findByExample(final T exampleObject,
            final SortingAndPagingDescriptor pagingDescriptor) {
        return findByExample(exampleObject, pagingDescriptor, null, null);
    }
    
    public List<T> findByExample(final T exampleObject,
            final SortingAndPagingDescriptor pagingDescriptor, int maxResults) {
        return findByExample(exampleObject, pagingDescriptor, null, maxResults);
    }
    

    public List<T> findByExample(final T exampleObject,
            final LockMode lockMode) throws DataAccessException {
        return findByExample(exampleObject, null, lockMode, null);
    }
    
    
    @SuppressWarnings("unchecked")
    public List<T> findByExample(final T exampleObject,
            final SortingAndPagingDescriptor pagingDescriptor,
            final LockMode lockMode, final Integer maxResults) throws DataAccessException {
        List<T> retList = null;
        if (exampleObject == null) {
            throw new DataAccessException(
                    " Invalid search criteria, exampleObject is : "
                            + exampleObject);
        }
        try {
            retList = (this.getHibernateTemplate()
                    .executeFind(new HibernateCallback() {
                        public Object doInHibernate(Session session) {
                            Example example = Example.create(exampleObject);
                            // example.enableLike(MatchMode.START);
                            // example.ignoreCase();
                            Criteria criteria = session
                                    .createCriteria(exampleObject.getClass()/* getPersistentEntityClass() */);
                            criteria.add(example);
                            if (pagingDescriptor != null) {
                                criteria = buildCriteria(criteria,
                                        pagingDescriptor);
                            }
                            if (lockMode != null) {
                                criteria.setLockMode(lockMode);
                            }
                            if (maxResults != null) {
                                criteria.setMaxResults(maxResults);
                            }
                            List<T> resultList = criteria.list();
                            return resultList;
                        }
                    }));
        } catch (Exception ex) {
            throw new DataAccessException("find by example failed", ex);
        }
        return retList;
    }

    public Iterator<T> iterateByExmple(final T exampleObject) {
        return iterateByExmple(exampleObject, (SortingOption[]) null);
    }
    
    public Iterator<T> iterateByExmple(final T exampleObject, LockMode lockMode) {
        return iterateByExmple(exampleObject, lockMode, (SortingOption[]) null);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so) {
        return iterateByExmple(exampleObject, new SortingOption[] { so }, null,
                100);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so, LockMode lockMode) {
        return iterateByExmple(exampleObject, new SortingOption[] { so },
                lockMode, 100);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            LockMode lockMode, final SortingOption... sos) {
        return iterateByExmple(exampleObject, sos, lockMode, 100);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption... sos) {
        return iterateByExmple(exampleObject, sos, null, 100);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so, final int internalCacheSize) {
        return iterateByExmple(exampleObject, new SortingOption[] { so }, null,
                internalCacheSize);
    }

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption[] sos, final LockMode lockMode,
            final int internalCacheSize) {
        return new Iterator<T>() {

            private int currStart = 0;

            private Iterator<T> currListIterator;

            private boolean nextListAvailable = true;

            private void checkAndLoadNextList() {
                if (currListIterator != null && currListIterator.hasNext()) {
                    return;
                }
                if (!nextListAvailable) {
                    return;
                }
                List<T> l = findByExample(exampleObject,
                        new SortingAndPagingDescriptor(currStart,
                                internalCacheSize, sos), lockMode, null);
                currStart += internalCacheSize;
                if (l.size() < internalCacheSize) {
                    nextListAvailable = false;
                }
                currListIterator = l.iterator();
            }

            public boolean hasNext() {
                checkAndLoadNextList();
                return currListIterator.hasNext();
            }

            public T next() {
                checkAndLoadNextList();
                return currListIterator.next();
            }

            public void remove() {
                throw new RuntimeException("method not supported");
            }

        };
    }

    /**
     * This method build the criteria object with paging & sorting options
     * 
     * @param criteria
     *            Specifies the Criteria instance.
     * @param pagingDescriptor
     *            Specifies the PagingDescriptor instance.
     * @return the criteria instance.
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     */
    protected Criteria buildCriteria(Criteria criteria,
            SortingAndPagingDescriptor pagingDescriptor)
            throws DataAccessException {
        logger.debug("buildCriteria(criteria,pagingDescriptor)");
        try {
            if ((criteria != null) && (pagingDescriptor != null)) {
                List<SortingOption> sortingOptions = pagingDescriptor
                        .getSortingOptions();
                if (sortingOptions != null) {
                    for (Object sortOptionObj : sortingOptions) {
                        if (sortOptionObj == null) {
                            continue;
                        }
                        SortingAndPagingDescriptor.SortingOption sortingOption = (SortingAndPagingDescriptor.SortingOption) sortOptionObj;
                        if ((sortingOption != null)
                                && (sortingOption.getDirection() != null)
                                && (sortingOption.getDirection()
                                        .equalsIgnoreCase("desc"))) {
                            criteria.addOrder(Order.desc(sortingOption
                                    .getProperty()));
                        } else {
                            criteria.addOrder(Order.asc(sortingOption
                                    .getProperty()));
                        }
                    }
                }
                if (pagingDescriptor.getStartIndex() > -1) {
                    criteria.setFirstResult(pagingDescriptor.getStartIndex());
                }
                if (pagingDescriptor.getPageSize() > -1) {
                    criteria.setMaxResults(pagingDescriptor.getPageSize());
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("build criteria  failed", ex);
        }
        return criteria;
    }

    public void initializeLazyObject(Object obj) {
        getHibernateTemplate().initialize(obj);
    }

//    FIXME smineyev: I didn't find the way to re-attach modified data-graph to hibernate session 
//  (neither update, locl(NONE), relicate, refresh work)    
    public void reattachModified(T entity) {
        throw new AbstractMethodError("not implemented since solution not found");
//  smineyev: commented out since this aproach cause exception saying "possible nonthreadsafe access to session" later if we call "evict"
//  also it flushes changes previously made to nested collections. TODO investigation required !
//        getHibernateTemplate().update(entity);
    }
    
//    public void reattachUnmodified(T entity) {
//        getHibernateTemplate().lock(entity, LockMode.NONE);
//    }

    public void clearCache() {
        getHibernateTemplate().clear();
        logger.debug("Hibernate session cleared");
    }

    public void evict(T ri) {
        getHibernateTemplate().evict(ri);
    }
    
    public void flush() {
        getHibernateTemplate().flush();
    }
    
    
}
