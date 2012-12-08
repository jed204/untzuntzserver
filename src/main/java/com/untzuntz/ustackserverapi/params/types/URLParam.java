package com.untzuntz.ustackserverapi.params.types;

import java.net.MalformedURLException;
import java.net.URL;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.BaseParam;
import com.untzuntz.ustackserverapi.params.ParamValueException;
import com.untzuntz.ustackserverapi.params.ValidatedParam;

/**
 * Validates a url type
 */
public class URLParam extends BaseParam implements ParamTypeInt,ValidatedParam {

	private String forceType;
	
	/**
	 * 
	 */
	public URLParam(String forceType)
	{
		this.forceType = forceType;
	}

	@Override
	public void validate(String data) throws APIException {
		
		try {
			new URL(data);
		} catch (MalformedURLException mue) {
			throw new ParamValueException(this, "Provided value must be a valid URL in the format of: protocol://host:port/path");
		}
		
		if (forceType != null && data != null && !data.startsWith(forceType))
			throw new ParamValueException(this, "Invalid URL type, must be start with '" + forceType + "'");
		
	}


}
