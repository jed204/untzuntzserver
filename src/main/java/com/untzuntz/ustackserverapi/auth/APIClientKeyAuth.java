package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.Ostermiller.util.Base64;
import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.exceptions.AuthenticationException;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class APIClientKeyAuth implements AuthenticationInt<Boolean> {

    static Logger           		logger               	= Logger.getLogger(APIClientKeyAuth.class);

    public String getAuthenticationDescription() {
    	return "Your client ID/API Key via HTTP Basic Authentication required.";
    }

    public boolean isBasicAuth() {
    	return true;
    }
    
    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	return ret;
    }

	@Override
	public Boolean authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException {

		String clientId = params.get(ParamNames.client_id);
		String apiKey = params.get(ParamNames.api_key);
		String authHeader = null;
		
		if (clientId == null && req != null)
		{
			authHeader = req.getHeader(HttpHeaders.Names.AUTHORIZATION);
			if (authHeader == null)
				throw new APIAuthenticationException("Client ID/API Key not provided");
				
			if (!authHeader.toLowerCase().startsWith("basic "))
				throw new APIAuthenticationException("Unsupported Authorization Method");

			authHeader = authHeader.substring(6);
			
			// check cache
			if (UOpts.getCacheEnabled() && "T".equals( (String)UDataCache.getInstance().get(authHeader + "_" + method.getAuthenticationGroup()) ))
				return true;
			
			authHeader = Base64.decode(authHeader);
			
			String[] spl = authHeader.split(":");
			if (spl.length != 2)
				throw new APIAuthenticationException("Bad Client ID/API Key (Invalid)");
				
			clientId = spl[0];
			apiKey = spl[1];
			
			params.setParameterValue(ParamNames.client_id.getName(), clientId);
			params.setParameterValue(ParamNames.api_key.getName(), apiKey);
		}
		
		if (clientId == null || apiKey == null || clientId.length() == 0 || apiKey.length() == 0)
			throw new APIAuthenticationException("Bad Client ID/API Key (Not Provided)");
		
		try {

			// authenticate the api client
			Authentication.authenticateAPI(clientId, apiKey);

			// verify the user has authorized the client
			// TODO: ??
			
		} catch (AuthenticationException e) {
			if (UOpts.getCacheEnabled() && authHeader != null)
				UDataCache.getInstance().set(authHeader + "_" + method.getAuthenticationGroup(), 600, "F");
			throw new APIAuthenticationException("Bad Client ID/API Key");
		}
		
		if (method.getAuthenticationGroup() != null)
		{
			try {
				Authorization.authorizeAPI(params.get(ParamNames.client_id), method.getAuthenticationGroup());
			} catch (AuthorizationException e) {
				logger.warn("Client [" + params.get(ParamNames.client_id) + "] tried to accesss '" + method.getPath() + "' but does not have permission '" + method.getAuthenticationGroup() + "' => " + e.getMessage());
				if (UOpts.getCacheEnabled() && authHeader != null)
					UDataCache.getInstance().set(authHeader + "_" + method.getAuthenticationGroup(), 600, "F");
				throw new APIAuthorizationException("API Client Not Authorized");
			}
		}
		
		if (UOpts.getCacheEnabled() && authHeader != null)
			UDataCache.getInstance().set(authHeader + "_" + method.getAuthenticationGroup(), 600, "T");
		
		return true;
	}
	
}
