package com.untzuntz.ustackserverapi;

import com.untzuntz.ustackserverapi.params.UnknownParameterTypeException;

public enum ParamType implements ParamTypeInt {

	String,
	Integer,
	Float,
	Long,
	JSON,
	XML;
	
	private ParamType()
	{
		
	}
	
	public String getName() {
		return name();
	}
	
	public void validate(String data) throws APIException
	{
		if (String.equals(this))
		{
			
		}
		else
			throw new UnknownParameterTypeException(this);
	}
	
}
