package com.untzuntz.ustackserverapi.params.types;

import org.bson.types.ObjectId;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a string type with a min and max length (optional)
 */
public class ObjectIdParam extends BaseParam implements ParameterDefinitionInt<ObjectId> {
	
	private static final Integer minLen = 24;
	private static final Integer maxLen = 24;

	/**
	 * Setup core string parameter
	 * 
	 * @param n
	 * @param d
	 */
	public ObjectIdParam(String n, String d) {
		super(n, d);
	}
	
	public String getTypeDescription() {
		
		String msg = "";
		if (minLen != null && maxLen == null)
			msg = " at least " + minLen + " chars long";
		else if (minLen == null && maxLen != null)
			msg = " at max " + minLen + " chars long";
		else if (minLen != null && maxLen != null)
			msg = " between " + minLen + " and " + maxLen + " chars long";
		
		return "A string of text" + msg;
	}
	
	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minLen != null)
			throw new ParamValueException(this, "String must be at least " + minLen + " characters");

		if (minLen != null && data.length() < minLen)
			throw new ParamValueException(this, "String must be at least " + minLen + " characters");
	
		if (maxLen != null && data.length() > maxLen)
			throw new ParamValueException(this, "String cannot exceed " + maxLen + " characters");
	
		try {
			new ObjectId(data);
		} catch (Exception e) {
			throw new ParamValueException(this, "Invalid Object ID Format");
		}
	}

	@Override
	public ObjectId getValue(String data) {
		return new ObjectId(data);
	}

}
