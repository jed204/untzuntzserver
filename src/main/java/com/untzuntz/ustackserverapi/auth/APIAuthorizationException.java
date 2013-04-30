package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustackserverapi.APIException;

/**
 * Authorization has failed
 * 
 * @author jdanner
 *
 */
public class APIAuthorizationException extends APIException {

	private static final long serialVersionUID = 1L;

	public APIAuthorizationException(String m) {
		super(m);
	}
	
}
