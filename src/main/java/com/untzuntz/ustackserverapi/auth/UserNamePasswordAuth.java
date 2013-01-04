package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class UserNamePasswordAuth implements AuthenticationInt<UserAccount> {

    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	
    	ret.add(ParamNames.username);
    	ret.add(ParamNames.password);
    	
    	return ret;
    }

	@Override
	public UserAccount authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException {
		
		UserAccount user = null;
		try {
			user = Authentication.authenticateUser(params.get(ParamNames.username), params.get(ParamNames.password));
		} catch (AuthenticationException e) {
			throw new APIAuthenticationException(e.getMessage());
		}
		
		if (method.getAuthenticationGroup() != null)
		{
			try {
				Authorization.authorizeUser(user, "*", null, method.getAuthenticationGroup());
			} catch (AuthorizationException e) {
				throw new APIAuthorizationException("Not Authorized");
			}
		}
		
		return user;
	}
	
}
