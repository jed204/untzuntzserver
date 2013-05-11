package com.untzuntz.ustackserverapi.exceptions;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIExceptionDocumentation;

/**
 * We thought we were going to get one thing and we didn't
 * 
 * @author jdanner
 *
 */
public class UnexpectedResultException extends APIException implements APIExceptionDocumentation {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public UnexpectedResultException(String message)
	{
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getReason() {
		return "An unexpected response was encountered.";
	}

}