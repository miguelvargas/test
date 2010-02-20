/**
 * Copyright (C) 2008 Michael A. MacDonald
 */
package com.mvwsolutions.android.dbgen;

import com.mvwsolutions.android.db.FieldType;
import com.mvwsolutions.android.db.FieldVisibility;

/**
 * Definition of a single column in a table, as defined by annotations in the interface
 * @author Michael A. MacDonald
 *
 */
class FieldDefinition {
	/**
     * Name as it appears in java
	 */
	String name;
	/**
	 * Column name in database table
	 */
	String columnName;
	FieldType type;
	String javaTypeCode;
	String javaType;
	String defaultValue;
	boolean nullable;
	boolean putRequired;
	String putName;
	boolean getRequired;
	String getName;
	boolean bothRequired;
	FieldVisibility visibility;
}
