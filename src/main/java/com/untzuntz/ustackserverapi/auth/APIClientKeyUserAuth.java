package com.untzuntz.ustackserverapi.auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.data.AccessToken.AccessTokenDetails;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * Authentication method for client key with a provided username
 * 
 * @author jdanner
 *
 */
public class APIClientKeyUserAuth implements AuthenticationInt<UserAccount> {

    static Logger           		logger               	= Logger.getLogger(APIClientKeyUserAuth.class);
 
    public static final String CookieTokenName = "UNTZ";
    
    public String getAuthenticationDescription() {
    	return "Authentication via an API Client and API Key. An authentication access token may also be used.";
    }

    public boolean isBasicAuth() {
    	return false;
    }

    /**
     * 
     */
    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	ret.add(ParamNames.client_id);
    	ret.add(ParamNames.api_key);
    	return ret;
    }
    
	@Override
	public UserAccount authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException {

		String userName = null;
		if ( params.get(ParamNames.token) != null)
		{
			//AuthTypes.ClientKey.authenticate(method, req, params);
			AccessTokenDetails details = AccessToken.decode( params.get(ParamNames.token) );
			
			if (details == null)
			{
				logger.warn(String.format("Invalid Token => '%s'", params.get(ParamNames.token)));
				throw new APIAuthenticationException("Invalid Token");
			}

			LogoutUtil.checkTokenLogout(details);

			long tokenLife = details.getExpirationAge() - System.currentTimeMillis();
			if (tokenLife < 0)
			{
				logger.info(String.format("%s -- %s/%s | Access Token expired at '%s'", method.getPath(), details.getUserName(), details.getClientId(), new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date(details.getExpirationAge()))));
				throw new APIAuthenticationException("Token has expired");
			}
	
	//		logger.info(String.format("%s -> Expiration %d -> Now: %d", details.userName, details.expirationAge, System.currentTimeMillis()));
			
			params.setParameterValue(ParamNames.client_id.getName(), details.getClientId());
			params.setTokenTTL(tokenLife);
			
			userName = details.getUserName();
		}
		else if (params.get(ParamNames.client_id) != null) {
			
			ClientKeyAuthBase.authenticate(method, req, params);
			userName = params.get(ParamNames.username);
		}
		else
			throw new APIAuthenticationException("Authentication Parameters Not Provided");
			
		return UserAccount.getUser(userName);
	}

}
