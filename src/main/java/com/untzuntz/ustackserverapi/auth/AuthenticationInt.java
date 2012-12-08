package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;

public interface AuthenticationInt {

	public void authenticate(CallParameters params) throws APIException;
	
}
