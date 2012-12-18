package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;

public interface AuthenticationInt<T> {

	public T authenticationAuthorization(MethodDefinition method, CallParameters params) throws APIException;
	
}
