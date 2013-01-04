package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;

public interface AuthorizationInt {

	public void authorize(MethodDefinition method, CallParameters params) throws APIException;

}
