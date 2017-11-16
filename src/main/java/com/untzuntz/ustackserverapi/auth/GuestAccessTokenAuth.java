package com.untzuntz.ustackserverapi.auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.data.AccessToken.AccessTokenDetails;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class GuestAccessTokenAuth implements AuthenticationInt<String> {

    static Logger           		logger               	= Logger.getLogger(GuestAccessTokenAuth.class);
 
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
	public String authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException {

		//AuthTypes.ClientKey.authenticate(method, req, params);
		AccessTokenDetails details = AccessToken.decode( params.get(ParamNames.token) );
		
		if (details == null)
		{
			logger.warn(String.format("Invalid Token => '%s' (%s)", params.get(ParamNames.token), method.getPath()));
			throw new APIAuthenticationException("Invalid Token");
		}
		
		long tokenLife = details.getExpirationAge() - System.currentTimeMillis();
		if (tokenLife < 0)
		{
			logger.info(String.format("%s/%s | Access Token expired at '%s'", details.getUserName(), details.getClientId(), new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date(details.getExpirationAge()))));
			throw new APIAuthenticationException("Token has expired");
		}

//		logger.info(String.format("%s -> Expiration %d -> Now: %d", details.userName, details.expirationAge, System.currentTimeMillis()));
		
		params.setParameterValue(ParamNames.client_id.getName(), details.getClientId());
		params.setTokenTTL(tokenLife);
		
		return details.getUserName();
	}

}
