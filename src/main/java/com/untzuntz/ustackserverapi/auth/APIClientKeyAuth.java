package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class APIClientKeyAuth implements AuthenticationInt<Boolean> {

	public static boolean autoPass;
	
	@Override
	public Boolean authenticationAuthorization(MethodDefinition method, CallParameters params) throws APIException {

		if (autoPass)
			return true;
		
		if (!params.has(ParamNames.client_id) || !params.has(ParamNames.api_key))
			throw new APIAuthenticationException("Bad Client ID/API Key (Not Provided)");
		
		try {

			// authenticate the api client
			Authentication.authenticateAPI(params.get(ParamNames.client_id), params.get(ParamNames.api_key));

			// verify the user has authorized the client
			// TODO: ??
			
		} catch (AuthenticationException e) {
			throw new APIAuthenticationException("Bad Client ID/API Key");
		}
		
		if (method.isAuthorizationRequired())
		{
			try {
				Authorization.authorizeAPI(params.get(ParamNames.client_id), method.getAuthGroup());
			} catch (AuthorizationException e) {
				throw new APIAuthorizationException("API Client Not Authorized");
			}
		}
		
		return true;
	}
	
}
