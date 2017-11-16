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

	private String fieldName;
	private Class<?> typeClass;
	private String classField;
	private ParameterDefinitionInt<?> valueType;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getTypeClass() {
		return typeClass;
	}

	public void setTypeClass(Class<?> typeClass) {
		this.typeClass = typeClass;
	}

	public String getClassField() {
		return classField;
	}

	public void setClassField(String classField) {
		this.classField = classField;
	}

	public ParameterDefinitionInt<?> getValueType() {
		return valueType;
	}

	public void setValueType(ParameterDefinitionInt<?> valueType) {
		this.valueType = valueType;
	}
}