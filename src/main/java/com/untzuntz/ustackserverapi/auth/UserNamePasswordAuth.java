package com.untzuntz.ustackserverapi.auth;

import org.apache.commons.codec.binary.Base64;

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;

public class UserNamePasswordAuth implements AuthenticationInt {

	@Override
	public void authenticate(CallParameters params) throws APIException {

		try {
		
		if ("true".equalsIgnoreCase(params.getParameter("s2")))
			params.setUser(Authentication.authenticateUserHash(params.getParameter("username"), new String(Base64.decodeBase64(params.getParameter("accesscode").getBytes()))));
		else
			params.setUser(Authentication.authenticateUser(params.getParameter("username"), params.getParameter("accesscode")));
		
		} catch (AuthenticationException e) {
			throw new APIAuthenticationException("Bad Username/Password");
		}
		
	}
	
	private static final UserNamePasswordAuth me = new UserNamePasswordAuth();
	public static AuthenticationInt get() {
		return me;
	}
	
}
