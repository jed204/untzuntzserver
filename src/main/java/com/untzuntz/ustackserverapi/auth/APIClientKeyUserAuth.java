package com.untzuntz.ustackserverapi.auth;

import java.text.SimpleDateFormat;
import java.util.*;

import com.mongodb.DBObject;
import com.untzuntz.ustack.data.APIClient;
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
			/*
			 * Make sure that the ApiClient record has a resourceLink with a customerId that the given username also has.
			 * Don't trust the username here.
			 */

			ClientKeyAuthBase.authenticate(method, req, params);

			// Get set of API Client's customer IDs.
			APIClient acct = APIClient.getAPIClient(params.get(ParamNames.client_id));
			if (acct == null) {
				throw new APIAuthenticationException("Could not find API client");
			}
			if (acct.getResourceLinkList().isEmpty()) {
				throw new APIAuthenticationException("API client's resource list is empty.");
			}
			Set<String> apiClientCustomerIds = new HashSet<String>();
			for (Object apiClientResourceLinkObj : acct.getResourceLinkList()) {
				apiClientCustomerIds.add((String) ((DBObject)apiClientResourceLinkObj).get("customerId"));
			}

			// Fetch user object.
			userName = params.get(ParamNames.username);
			UserAccount user = UserAccount.getUser(userName);
			if (user == null) {
				throw new APIAuthenticationException("API does not have access to given username");
			}

			// Make sure a common customer ID exists between API Client and User.
			boolean commonCustomerIdsExist = false;
			for (Object apiClientResourceLinkObj : user.getResourceLinkList()) {
				if (apiClientCustomerIds.contains((String)((DBObject)apiClientResourceLinkObj).get("customerId"))) {
					commonCustomerIdsExist = true;
				}
			}
			if (!commonCustomerIdsExist) {
				throw new APIAuthenticationException("API does not have access to given username");
			}
		}
		else
			throw new APIAuthenticationException("Authentication Parameters Not Provided");
			
		return UserAccount.getUser(userName);
	}

}
