package com.mvwsolutions.android.dao;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 
 * @author SMineyev
 * 
 * @param <T>
 *            Target bean class
 */
public abstract class IdBaseDao<T> extends BaseDao<T> {

	public abstract long extractPk(T bean);

	public abstract void assignPk(T bean, long id);

	/**
	 * Return the same ContentValues object with PK field removed
	 * 
	 * @param cv
	 * @return
	 */
	private ContentValues removeId(ContentValues cv) {
		cv.remove(getPkColumnName());
		return cv;
	}

	/**
	 * Insert a row in the table with the values of this instance
	 * 
	 * @param db
	 * @return
	 */
	public long insert(T bean) {
		long id = database.insert(getTableName(), null,
				removeId(createContentValues(bean)));
		assignPk(bean, id);
		return id;
	}

	/**
	 * Delete the row in the table corresponding to this instance
	 * 
	 * @param id
	 * @return Number of rows deleted
	 */
	public int delete(long id) {
		return database.delete(getTableName(), getPkColumnName() + "=" + id,
				null);
	}

	/**
	 * Delete the row in the table corresponding to this instance
	 * 
	 * @param bean
	 * @return Number of rows deleted
	 */
	public int delete(T bean) {
		return delete(extractPk(bean));
	}

	/**
	 * Update the row in the table corresponding to this instance with the
	 * values of the instance
	 * 
	 * @param db
	 * @return Number of rows updated
	 */
	public int update(T bean) {
		return database.update(getTableName(),
				removeId(createContentValues(bean)), getPkColumnName() + "="
						+ extractPk(bean), null);
	}

	public T findById(int pkValue) {
		T bean = createNewBean();
		return loadById(pkValue, bean);
	}

	public T loadById(int pkValue, T bean) {
		Cursor c = database.query(getTableName(), null, getPkColumnName() + "="
				+ pkValue, null, null, null, null);
		if (c.moveToFirst()) {
			fillBean(c, bean);
			return bean;
		} else {
			return null;
		}
	}

	public void refresh(T bean) throws RuntimeException {
		long pkValue = extractPk(bean);
		Cursor c = database.query(getTableName(), null, getPkColumnName() + "="
				+ pkValue, null, null, null, null);
		if (c.moveToFirst()) {
			fillBean(c, bean);
		} else {
			throw new RuntimeException("Record not found in the table "
					+ getTableName() + "for " + getPkColumnName() + " = "
					+ pkValue);
		}

	}

}
