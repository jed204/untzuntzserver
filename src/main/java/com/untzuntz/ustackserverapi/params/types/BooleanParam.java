package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.BaseParam;
import com.untzuntz.ustackserverapi.params.ParamValueException;
import com.untzuntz.ustackserverapi.params.ValidatedParam;

/**
 * Validates a boolean type ('true' or 'false')
 */
public class BooleanParam extends BaseParam implements ParamTypeInt,ValidatedParam {
	
	/**
	 * Simple Boolean
	 */
	public BooleanParam()
	{
	}

	@Override
	public void validate(String data) throws APIException {
	
		if ("true".equalsIgnoreCase(data))
			return;
		if ("false".equalsIgnoreCase(data))
			return;
		
		throw new ParamValueException(this, "Boolean must be either 'true' or 'false'");
	}


}
