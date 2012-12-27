package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.data.APIMapping;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class APIClientKeyTokenSecretAuth implements AuthenticationInt<UserAccount> {

	@Override
	public UserAccount authenticationAuthorization(MethodDefinition method, CallParameters params) throws APIException {

		// Do the client id and api key auth
		AuthTypes.ClientKey.authenticationAuthorization(method, params);
	
		// get user info
		UserAccount user = UserAccount.getByAPIToken(params.get(ParamNames.client_id), params.get(ParamNames.token));
		
		// validate user API secret against provided
		APIMapping api = user.getAPIMapping(params.get(ParamNames.client_id));
		if (!api.checkAPIKey( params.get(ParamNames.secret) ))
			throw new APIAuthenticationException("Bad Token/Secret");

		if (method.isAuthorizationRequired())
		{
			try {
				Authorization.authorizeUser(user, "*", null, method.getAuthGroup());
			} catch (AuthorizationException e) {
				throw new APIAuthorizationException("User Not Authorized");
			}
		}

		return user;
	}
	

}
