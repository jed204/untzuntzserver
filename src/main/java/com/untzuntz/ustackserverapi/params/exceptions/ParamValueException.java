package com.untzuntz.ustackserverapi.params.exceptions;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIExceptionDocumentation;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class ParamValueException extends APIException implements APIExceptionDocumentation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String paramTypeName;
	private String message;

	public ParamValueException(ParameterDefinitionInt pti, String message) {
		super();
		this.paramTypeName = pti.getName();
		this.message = pti.getName() + " - " + message;
	}

	public String getParamTypeName() {
		return paramTypeName;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public String getReason() {
		return "The request parameter is not valid";
	}

}
