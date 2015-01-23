package com.untzuntz.ustackserverapi.params.exceptions;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class UnknownParameterTypeException extends APIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String paramTypeName;

	public UnknownParameterTypeException(ParameterDefinitionInt<?> pti) {
		super();
		paramTypeName = pti.getName();
	}

	public String getParamTypeName() {
		return paramTypeName;
	}
	
}
