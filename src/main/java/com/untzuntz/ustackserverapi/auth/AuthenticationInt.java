package com.untzuntz.ustackserverapi.auth;

import java.util.List;

import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

/**
 * Authentication interface to support authenticating calls to API methods
 * 
 * @author jdanner
 *
 * @param <T>
 */
public interface AuthenticationInt<T> {

	public T authenticate(MethodDefinition method, HttpRequest req, CallParameters params) throws APIException;
	public List<ParameterDefinitionInt<?>> getAuthenticationParameters();
	public String getAuthenticationDescription();
	public boolean isBasicAuth();
	
}
