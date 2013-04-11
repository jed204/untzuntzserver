package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a regular expression provided. Returns a String.
 */
public class RegexParam extends BaseParam implements ParameterDefinitionInt<String> {
	
	private String regex;
	private boolean required;

	/**
	 * Setup core regex parameter
	 * 
	 * @param n
	 * @param d
	 */
	public RegexParam(String n, String d) {
		super(n, d);
	}
	
	/**
	 * Setup a regex parameter with the regex defined
	 * @param regex
	 * @param required
	 */
	public RegexParam(String n, String d, String regex, boolean required)
	{
		super(n, d);
		this.regex = regex;
		this.required = required;
	}

	public String getTypeDescription() {
		
		return String.format("A string of text matching the regex '%s'", regex);
	}
	
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && required)
			throw new ParamValueException(this, "This parameter must have a value");

		if (data != null && !data.matches(regex))
			throw new ParamValueException(this, String.format("This parameter does not match the required regex '%s'", regex));
	}

	@Override
	public String getValue(String data) {
		
		if (data != null && data.matches(regex))
			return data;
		
		return null;
	}

}
