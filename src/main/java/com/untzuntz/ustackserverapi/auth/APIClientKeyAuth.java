package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class APIClientKeyAuth implements AuthenticationInt<ClientData> {

	@Override
	public ClientData authenticationAuthorization(MethodDefinition method, CallParameters params) throws APIException {

		ClientData ret = new ClientData();
		try {
			
			ret.api = Authentication.authenticateAPI(params.get(ParamNames.client_id), params.get(ParamNames.api_key));
			
			if (params.get(ParamNames.username) != null)
			{
				ret.userName = params.get(ParamNames.username);
				ret.user = UserAccount.getUser(ret.userName);
			}

			
		} catch (AuthenticationException e) {
			throw new APIAuthenticationException("Bad Client ID/API Key");
		}
		
		if (method.isAuthorizationRequired())
		{
			try {
				Authorization.authorizeAPI(ret.api, method.getAuthGroup());
			} catch (AuthorizationException e) {
				throw new APIAuthorizationException("Not Authorized");
			}
		}
		
		return ret; 

		
	}
	
}
