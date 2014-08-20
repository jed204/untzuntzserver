package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * Validates an API client ID and API key via HTTP Basic Auth
 * 
 * @author jdanner
 *
 */
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
		ClientKeyAuthBase.authenticate(method, req, params);
		return true;
	}
	
}
