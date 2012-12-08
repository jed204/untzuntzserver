package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;

public class UnknownParameterTypeException extends APIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String paramTypeName;

	public UnknownParameterTypeException(ParamTypeInt pti) {
		super();
		paramTypeName = pti.getName();
	}

	public String getParamTypeName() {
		return paramTypeName;
	}
	
}
