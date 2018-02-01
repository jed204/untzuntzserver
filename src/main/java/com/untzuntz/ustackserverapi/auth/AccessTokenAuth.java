package com.untzuntz.ustackserverapi.auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustack.data.AccessToken;
import com.untzuntz.ustack.data.AccessToken.AccessTokenDetails;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * Authentication method for an access token (temporary access value)
 * 
 * @author jdanner
 *
 */
public class AccessTokenAuth implements AuthenticationInt<UserAccount> {

    static Logger           		logger               	= Logger.getLogger(AccessTokenAuth.class);
 
    public static final String CookieTokenName = "UNTZ";
    
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

		if ("STATIC".equals(params.get(ParamNames.token))) {
			AuthTypes.ClientKey.authenticate(method, req, params);
			
			APIClient acct = APIClient.getAPIClient(params.get(ParamNames.client_id));
			String userEquiv = acct.getString("userEquivalent");
			if (userEquiv == null)
				throw new APIAuthenticationException("No API Client userEquivalent set");
			
			return UserAccount.getUser(userEquiv);
		}
			
		AccessTokenDetails details = AccessToken.decode( params.get(ParamNames.token) );
		
		if (details == null)
		{
			logger.warn(String.format("Invalid Token => '%s' (%s)", params.get(ParamNames.token), method.getPath()));
			throw new APIAuthenticationException("Invalid Token");
		}

		LogoutUtil.checkTokenLogout(details);

		long tokenLife = details.getExpirationAge() - System.currentTimeMillis();
		if (tokenLife < 0)
		{
			logger.info(String.format("%s/%s | Access Token expired at '%s'", details.getUserName(), details.getClientId(), new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date(details.getExpirationAge()))));
			throw new APIAuthenticationException("Token has expired");
		}

//		logger.info(String.format("%s -> Expiration %d -> Now: %d", details.userName, details.expirationAge, System.currentTimeMillis()));
		
		params.setParameterValue(ParamNames.client_id.getName(), details.getClientId());
		params.setTokenTTL(tokenLife);
		
		return UserAccount.getUser(details.getUserName());
	}

}
