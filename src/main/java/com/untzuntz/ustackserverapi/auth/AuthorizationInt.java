package com.untzuntz.ustackserverapi.auth;

import java.util.List;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * API Authorization to allow verification that the caller is authorized to access a resource
 * 
 * @author jdanner
 *
 */
public interface AuthorizationInt {

	public List<ParameterDefinitionInt<?>> getAuthenticationParameters();
	public void authorize(MethodDefinition method, CallParameters params) throws APIException;

}
