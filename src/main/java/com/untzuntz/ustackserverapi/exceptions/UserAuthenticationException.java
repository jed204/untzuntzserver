package com.untzuntz.ustackserverapi.exceptions;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIExceptionDocumentation;

/**
 * Indicates the user has failed authentication
 * 
 * @author jdanner
 *
 */
public class UserAuthenticationException extends APIException implements APIExceptionDocumentation {
	
	private static final long serialVersionUID = 1L;
	private String message;
	private String failReason;
	
	public UserAuthenticationException(String message, String failReason)
	{
		super(message);
		this.message = message;
		this.failReason = failReason;
	}

	public String getFailReason() {
		return failReason;
	}

	public String getMessage() {
		return message;
	}

	public String getReason() {
		return "The user could not be authenticated. See the 'reason' field for more information.";
	}

}