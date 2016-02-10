package com.untzuntz.ustackserverapi.util;

import com.untzuntz.coredata.SearchFilters.FilterType;


/**
 * The class to receive the JSON representation of a search filter entry
 * 
 * @author jdanner
 *
 */
public class JsonSearchFilter {

	String fieldName;
	String type;
	String value;
	String[] values;
	
	public String getFieldName() {
		return fieldName;
	}
	
	public FilterType getType() {
		
		if (type != null)
			return FilterType.valueOf(type);

		return FilterType.Equals;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
}
