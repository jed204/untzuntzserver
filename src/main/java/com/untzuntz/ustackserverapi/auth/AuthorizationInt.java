package com.untzuntz.ustackserverapi.auth;

import java.util.List;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public interface AuthorizationInt {

	public List<ParameterDefinitionInt<?>> getAuthenticationParameters();
	public void authorize(MethodDefinition method, CallParameters params) throws APIException;

}
