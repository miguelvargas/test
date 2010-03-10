package com.mvwsolutions.common.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.LockMode;

import com.mvwsolutions.common.dao.SortingAndPagingDescriptor.SortingOption;



/**
 * This class is the Base DAO(Data Access Object)Interface. It must be
 * implemented by all data access objects (DAOs)
 * 
 * @author : smineyev with permission, altered by Marcus
 * 
 */
public interface BaseDao<T, ID extends Serializable> {

    /**
     * This method load/retrieve a domain Object for a given id from the
     * database Table
     * 
     * @param id
     *            Specifies the id of the domain Object to be retrieved.
     * @return the domain object instance
     * @throws DataAccessException
     *             in case of dataaccess execution errors.
     */
    public T findById(ID id) throws DataAccessException;

    public <C> C findById(ID id, Class<C> expectedClass)
            throws DataAccessException;

    /**
     * This method load/retrieve all the records for a domain Object from the
     * database Table
     * 
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public List<T> findAll() throws DataAccessException;

    /**
     * This method load/retrieve maximum number of records(specified by
     * maxResults parameter) for a domain Object from the database Table.
     * 
     * @param maxResults
     *            Specifies the maximum number of records to be retrieve.
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     */
    public List<T> findAll(int maxResults) throws DataAccessException;

    /**
     * This method load/retrieve maximum number of records( the index postion of
     * first record in the resultSet is specified by startIndex parameter) for a
     * domain Object from the database Table.
     * 
     * @param startIndex
     *            Specifies the start index of the record to be retrieve.
     * @param maxResults
     *            Specifies the maximum number of records to be retrieve.
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public List<T> findAll(int startIndex, int maxResults)
            throws DataAccessException;

    /**
     * This method load/retrieve all the records for a domain Object based on
     * the populated search attributes(search -parameter/values) from the
     * database Table.
     * 
     * @param exampleInstance
     *            Specifies the domainObject with search criteria
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     */
    public List<T> findByExample(T exampleInstance)
            throws DataAccessException;
    
    public List<T> findByExample(T exampleObject, int maxResults);    

    public T merge(T transObj) throws DataAccessException;

    /**
     * This method save/update a domain object's information into the Database
     * Table.
     * 
     * @param entity
     *            Specifies the domain object to be save/update.
     * @return the domainobject instance
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public T saveOrUpdate(T entity) throws DataAccessException;

    /**
     * This method remove a domain object's information from the Database Table.
     * 
     * @param entity
     *            Specifies the domain object to be deleted.
     * @return true if object has been deleted otherwise false
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public void delete(T entity) throws DataAccessException;
    
    /**
     * This method remove a domain object's information from the Database Table using object id.
     * 
     * @param id
     *            Specifies ID of domain object to be deleted.
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public boolean deleteById(ID id) throws DataAccessException;    

    /**
     * This method save/update a list of domain object's information into the
     * Database Table.
     * 
     * @param domainobjList
     *            Specifies the List of domain objects to be saved/updated.
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public List<T> saveOrUpdateAll(List<T> domainobjList)
            throws DataAccessException;

    /**
     * This method remove a list of domain object's information from the
     * Database Table.
     * 
     * @param domainobjList
     *            Specifies the list of domain objects to be deleted
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     * 
     */
    public void deleteAll(List<T> domainobjList) throws DataAccessException;

    /**
     * This method load/retrieve all the records for a domain Object based on
     * the populated search attributes(search- parameter/values) and
     * SortingAndPagingDescriptor (which contains the sorting-order, startIndex
     * and pagesize for the search result) from the database Table.
     * 
     * @param exampleObject
     *            Specifies the exampleObject with search criteria.
     * @param pagingData
     *            Specifies the sorting & paging criteria.
     * @return the list of domain-object instances
     * @throws DataAccessFwException
     *             in case of dataaccess execution errors.
     */
    public List<T> findByExample(T exampleObject,
            SortingAndPagingDescriptor pagingData) throws DataAccessException;

    public List<T> findByExample(final T exampleObject,
            final SortingAndPagingDescriptor pagingDescriptor, int maxResults);    
    
    public List<T> findByExample(final T exampleObject,
            final LockMode lockMode) throws DataAccessException;
    
    public Iterator<T> iterateByExmple(final T exampleObject);

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so);

    public Iterator<T> iterateByExmple(final T exampleObject, LockMode lockMode);

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption... sos);

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so, final int internalCacheSize);

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption so, LockMode lockMode);

    public Iterator<T> iterateByExmple(final T exampleObject,
            LockMode lockMode, final SortingOption... sos);

    public Iterator<T> iterateByExmple(final T exampleObject,
            final SortingOption[] sos, final LockMode lockMode,
            final int internalCacheSize);

    public void initializeLazyObject(Object obj);

//    FIXME smineyev: I didn't find the way to re-attach modified data-graph to hibernate session 
//  (neither update, locl(NONE), relicate, refresh work)    
//    public void reattachModified(T entity);
    
    public void clearCache();

    public void evict(T ri);
    
    public void flush();
}
