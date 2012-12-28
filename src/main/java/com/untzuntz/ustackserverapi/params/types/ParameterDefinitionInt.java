package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;

public interface ParameterDefinitionInt<T> {

	public String getName();
	public String getDescription();
	public String getTypeDescription();
	public void validate(String data) throws APIException;
	public T getValue(String src);
	public boolean hasValue(String src);
	public void setDescription(String d);

}
