package com.untzuntz.ustackserverapi.params.types;

import java.net.MalformedURLException;
import java.net.URL;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a url type
 */
public class URLParam extends BaseParam implements ParameterDefinitionInt<URL> {

	private String forceType;

	/**
	 * Core URL Param
	 * @param n
	 * @param d
	 */
	public URLParam(String n, String d)
	{
		super(n, d);
	}

	/**
	 * URL Param with optional 'forceType' value which ensures the URL starts with the value provided
	 * 
	 * @param n
	 * @param d
	 * @param forceType
	 */
	public URLParam(String n, String d, String forceType)
	{
		super(n, d);
		this.forceType = forceType;
	}

	public String getTypeDescription() {
		return "A string of text in the format of a uniform resource locator (ex: http://google.com/hello)";
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
	
	@Override
	public URL getValue(String data) {
		try { 
			return new URL(data);
		} catch (MalformedURLException mue) {
			return null;
		}
	}

}
