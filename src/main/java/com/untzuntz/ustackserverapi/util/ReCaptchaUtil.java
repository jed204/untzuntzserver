package com.untzuntz.ustackserverapi.util;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

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
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(privateKey);

        String challenge = callParams.get(ParamNames.recaptcha_challenge_field);
        String uresponse = callParams.get(ParamNames.recaptcha_response_field);
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(callParams.getRemoteIpAddress(), challenge, uresponse);

        if (!reCaptchaResponse.isValid())
        	throw new BadRequestException("Invalid Captcha");
	}
	
	
}
