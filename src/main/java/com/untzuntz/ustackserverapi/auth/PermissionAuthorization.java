package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustack.aaa.UStackPermissionEnum;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.exceptions.AuthorizationException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class PermissionAuthorization implements AuthorizationInt {

	private static final PermissionAuthorization singleton = new PermissionAuthorization();
	
	/**
	 * No extra parameters required
	 */
    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	return ret;
    }


	public static PermissionAuthorization getInstance() {
		return singleton;
	}
	
	/**
	 * Ensures the caller has access to the requested account
	 * 
	 * @param callParams
	 * @throws Exception
	 */
	public void authorize(MethodDefinition method, CallParameters callParams) throws APIException
	{
		UserAccount user = callParams.getAuthInfo(AuthTypes.AccessTokenAuth);
		String resource = (String)method.getData("Resource");
		UStackPermissionEnum permission = (UStackPermissionEnum)method.getData("Permission");
		
		try {
			Authorization.authorizeUser(user, resource, null, permission);
		} catch (AuthorizationException e) {
			throw new APIAuthorizationException("User does not have access to resource");
		}
	}

}
