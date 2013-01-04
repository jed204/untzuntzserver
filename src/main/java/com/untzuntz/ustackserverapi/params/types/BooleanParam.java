package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a boolean type ('true' or 'false')
 */
public class BooleanParam extends BaseParam implements ParameterDefinitionInt<Boolean>
{
	public BooleanParam(String n, String d) {
		super(n, d);
	}
	
	public String getTypeDescription() {
		return "Boolean value as 'true' or 'false'";
	}

	@Override
	public void validate(String data) throws APIException {
		
		if ("true".equalsIgnoreCase(data))
			return;
		if ("false".equalsIgnoreCase(data))
			return;

		throw new ParamValueException(this, getTypeDescription());
		
	}
	
	@Override
	public Boolean getValue(String data) {
		
		if ("true".equalsIgnoreCase(data))
			return true;

		return false;
	}
}
