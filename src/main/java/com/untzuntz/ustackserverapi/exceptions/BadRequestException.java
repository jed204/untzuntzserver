package com.untzuntz.ustackserverapi.exceptions;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIExceptionDocumentation;

/**
 * The call you have made is just not correct (you're probably missing a required value)
 * 
 * @author jdanner
 *
 */
public class BadRequestException extends APIException implements APIExceptionDocumentation {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public BadRequestException(String message)
	{
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getReason() {
		return "Your request was invalid and cannot be processed.";
	}

}