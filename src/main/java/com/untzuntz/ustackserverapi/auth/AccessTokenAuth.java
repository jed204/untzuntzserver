package com.untzuntz.ustackserverapi.auth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.untzuntz.ustack.exceptions.AuthExceptionAuthError;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    	if (StringUtils.isNotEmpty(params.get(ParamNames.token)) && params.get(ParamNames.token).startsWith("JWT_")) {
    		// do JWT auth check
			DecodedJWT jwt = null;
			try {
				jwt = AccessToken.decodeJwt(params.get(ParamNames.token).substring(4));
			} catch (AuthExceptionAuthError authExceptionAuthError) {
				if (authExceptionAuthError.getMessage() != null && authExceptionAuthError.getMessage().indexOf("The Token has expired") > -1) {
					logger.info("Token Expired: " + authExceptionAuthError.getMessage());
					throw new APIAuthenticationException("Token has expired");
				}
				throw new APIAuthenticationException("Invalid Token");
			}

			if (jwt.getClaim("ipAddress") != null && jwt.getClaim("ipAddress").asString() != null) {
				boolean mismatch = !jwt.getClaim("ipAddress").asString().equals( params.getRemoteIpAddress() );
				if (mismatch) {
					if (MethodDefinition.TokenCheckMode.Enforce.equals(method.getTokenMode())) {
						throw new APIAuthenticationException("Invalid Token - IP Mismatch");
					}
					logger.warn(String.format("Token ID [%s] for user [%s] has a token IP Address mismatch | [%s] expected, but got [%s]", jwt.getClaim("id").asString(), jwt.getClaim("userName").asString(), jwt.getClaim("ipAddress").asString(), params.getRemoteIpAddress()));
				}
			}

			LogoutUtil.checkTokenLogout(jwt);

			params.setParameterValue(ParamNames.client_id.getName(), jwt.getClaim("clientId").asString());
			params.setTokenTTL(jwt.getExpiresAt().getTime() - System.currentTimeMillis());

			return UserAccount.getUser(jwt.getClaim("userName").asString());
		}

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
