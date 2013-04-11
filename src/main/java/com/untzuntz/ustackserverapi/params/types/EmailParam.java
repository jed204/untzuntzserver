package com.untzuntz.ustackserverapi.params.types;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates an email provided. Returns a InternetAddress.
 */
public class EmailParam extends BaseParam implements ParameterDefinitionInt<InternetAddress> {
	
	private final String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	private boolean required;

	/**
	 * Setup an email parameter
	 * 
	 * @param n
	 * @param d
	 */
	public EmailParam(String n, String d) {
		super(n, d);
	}
	
	/**
	 * Setup a regex parameter with the regex defined
	 * @param regex
	 * @param required
	 */
	public EmailParam(String n, String d, boolean required)
	{
		super(n, d);
		this.required = required;
	}

	public String getTypeDescription() {
		
		return "A valid email address";
	}
	
	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && required)
			throw new ParamValueException(this, "This parameter must have a value");

		if (data != null && !data.matches(regex))
			throw new ParamValueException(this, "This parameter must be an email address (ex: a@b.com)");
	}

	@Override
	public InternetAddress getValue(String data) {
		
		if (data != null && data.matches(regex))
			try {
				return new InternetAddress(data);
			} catch (AddressException e) {}
		
		return null;
	}

}
