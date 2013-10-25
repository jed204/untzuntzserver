package com.untzuntz.ustackserverapi.util;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.exceptions.BadRequestException;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class ReCaptchaUtil {

	/**
	 * Validates the user provided Re-Captcha with the remote server
	 * 
	 * @param privateKey
	 * @param callParams
	 * @throws BadRequestException
	 */
	public static void validateReCaptcha(String privateKey, CallParameters callParams) throws BadRequestException
	{
		if ("PASS".equals(System.getProperty("ReCaptchaTest")))
			return;
		if ("FAIL".equals(System.getProperty("ReCaptchaTest")))
        	throw new BadRequestException("Invalid Captcha");
		
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(privateKey);

        String challenge = callParams.get(ParamNames.recaptcha_challenge_field);
        String uresponse = callParams.get(ParamNames.recaptcha_response_field);
        if (challenge == null || uresponse == null)
        	throw new BadRequestException("'recaptcha_challenge_field' and 'recaptcha_response_field' parameters are required");
        
        System.out.println(String.format("ReCaptcha from '%s' | Challenge [%s], Response [%s]", callParams.getRemoteIpAddress(), challenge, uresponse));
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(callParams.getRemoteIpAddress(), challenge, uresponse);

        if (!reCaptchaResponse.isValid())
        	throw new BadRequestException("Invalid Captcha", new HttpResponseStatus(418, "Invalid Recaptcha"));
	}
	
	
}
