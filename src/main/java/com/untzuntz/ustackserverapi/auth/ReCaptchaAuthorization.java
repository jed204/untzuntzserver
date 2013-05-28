package com.untzuntz.ustackserverapi.auth;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustack.data.UniqueReference;
import com.untzuntz.ustack.data.UniqueReference.UniqueLinkStatus;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.util.ReCaptchaUtil;

public class ReCaptchaAuthorization implements AuthorizationInt {

	private String reCaptchaPrivateKey;
	private static final ReCaptchaAuthorization singleton = new ReCaptchaAuthorization();
	
	/**
	 * No extra parameters required
	 */
    public List<ParameterDefinitionInt<?>> getAuthenticationParameters() {
    	
    	List<ParameterDefinitionInt<?>> ret = new ArrayList<ParameterDefinitionInt<?>>();
    	ret.add(ParamNames.recaptcha_challenge_field);
    	ret.add(ParamNames.recaptcha_response_field);
    	return ret;
    }


	public static ReCaptchaAuthorization getInstance(String reCaptchaPrivateKey) {
		singleton.reCaptchaPrivateKey = reCaptchaPrivateKey;
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
		if (callParams.get(ParamNames.UID) != null && Boolean.TRUE.equals( (Boolean)method.getData("AllowUIDOverride")))
		{
			if (UniqueReference.getLinkStatus(callParams.get(ParamNames.UID)).equals(UniqueLinkStatus.active))
				return;
		}
		
		ReCaptchaUtil.validateReCaptcha(reCaptchaPrivateKey, callParams);
	}

}
