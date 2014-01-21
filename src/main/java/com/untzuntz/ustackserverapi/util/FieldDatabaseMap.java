package com.untzuntz.ustackserverapi.util;

import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * Matches a 'nice' field name used in API calls to a backend class and class field name
 * 
 * The value type is used for validating the parameter when the 'nice' name is used
 * 
 * 
 * Setup the field maps in the 'Params' class
 * 
 * @author jdanner
 *
 */
public class FieldDatabaseMap {
	
	/**
	 * Use this constructor for SQL classes as you must define the type class (for joins)
	 * @param fieldName
	 * @param typeClass
	 * @param classField
	 * @param valueType
	 */
	public FieldDatabaseMap(String fieldName, Class<?> typeClass, String classField, ParameterDefinitionInt<?> valueType)
	{
		this.fieldName = fieldName;
		this.typeClass = typeClass;
		this.classField = classField;
		this.valueType = valueType;
	}

	/**
	 * Use this constructor for MongoDB classes as you should not define the typeClass
	 * @param fieldName
	 * @param classField
	 * @param valueType
	 */
	public FieldDatabaseMap(String fieldName, String classField, ParameterDefinitionInt<?> valueType)
	{
		this.fieldName = fieldName;
		this.classField = classField;
		this.valueType = valueType;
	}

	public FieldDatabaseMap(ParameterDefinitionInt<?> valueType)
	{
		this.fieldName = valueType.getName();
		this.classField = valueType.getName();
		this.valueType = valueType;
	}

	public String fieldName;
	public Class<?> typeClass;
	public String classField;
	public ParameterDefinitionInt<?> valueType;

}