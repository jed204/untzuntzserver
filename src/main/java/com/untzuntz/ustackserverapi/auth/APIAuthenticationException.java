package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustackserverapi.APIException;

/**
 * Authentication has failed
 * 
 * @author jdanner
 *
 */
public class APIAuthenticationException extends APIException {

	private static final long serialVersionUID = 1L;

	public APIAuthenticationException(String m) {
		super(m);
	}
	
}
