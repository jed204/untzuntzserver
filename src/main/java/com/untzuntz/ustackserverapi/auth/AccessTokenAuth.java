package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.data.AccessToken.AccessTokenDetails;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class AccessTokenAuth implements AuthenticationInt<UserAccount> {

    static Logger           		logger               	= Logger.getLogger(AccessTokenAuth.class);
 
    public String getAuthenticationDescription() {
    	return "Access token in request parameters required.";
    }

    public boolean isBasicAuth() {
    	return false;
    }

    /**
     * 
     */
    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	
    	ret.add(ParamNames.token);
    	
    	return ret;
    }
    
	@Override
	public UserAccount authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException {

		//AuthTypes.ClientKey.authenticate(method, req, params);
		AccessTokenDetails details = AccessToken.decode( params.get(ParamNames.token) );
		if (details == null)
			throw new APIAuthenticationException("Invalid Token");
		if (details.expirationAge < System.currentTimeMillis())
			throw new APIAuthenticationException("Token has expired");
		
		params.setParameterValue(ParamNames.client_id.getName(), details.clientId);
		
		// get user info
		UserAccount user = UserAccount.getUser(details.userName);
		if (method.getAuthenticationGroup() != null)
		{
			try {
				Authorization.authorizeUser(user, "*", null, method.getAuthenticationGroup());
			} catch (AuthorizationException e) {
				logger.warn("Client [" + user.getUserName() + "] tried to accesss '" + method.getPath() + "' but does not have permission '" + method.getAuthenticationGroup() + "' => " + e.getMessage());
				throw new APIAuthorizationException("User Not Authorized");
			}
		}

		return user;
	}

}
