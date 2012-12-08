package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;

public class ParamValueException extends APIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String paramTypeName;
	private String message;

	public ParamValueException(ParamTypeInt pti, String message) {
		super();
		this.paramTypeName = pti.getName();
		this.message = message;
	}

	public String getParamTypeName() {
		return paramTypeName;
	}
	
	public String getMessage() {
		return message;
	}

}
