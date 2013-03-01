package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a string array type with a min and max item count (optional)
 */
public class StringArrayParam extends BaseParam implements ParameterDefinitionInt<String[]> {
	
	private Integer minSize;
	private Integer maxSize;

	/**
	 * Setup core string array parameter
	 * 
	 * @param n
	 * @param d
	 */
	public StringArrayParam(String n, String d) {
		super(n, d);
	}
	
	/**
	 * Setup a string array parameter with a min and max item count
	 * @param minSize
	 * @param maxSize
	 */
	public StringArrayParam(String n, String d, Integer minSize, Integer maxSize)
	{
		super(n, d);
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	/**
	 * Setup a string parameter with just a minimum length
	 * @param minLen
	 */
	public StringArrayParam(String n, String d, Integer minSize)
	{
		super(n, d);
		this.minSize = minSize;
	}
	
	public String getTypeDescription() {
		
		String msg = "";
		if (minSize != null && maxSize == null)
			msg = " at least " + minSize + " items";
		else if (minSize == null && maxSize != null)
			msg = " at max " + minSize + " items";
		else if (minSize != null && maxSize != null)
			msg = " between " + minSize + " and " + maxSize + " items";
		
		return "A string array of text separated by commas" + msg + " (example: item1,item2";
	}
	
	

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minSize != null)
			throw new ParamValueException(this, "String array must have at least " + minSize + " items");

		String[] ret = data.split(",");
		if (minSize != null && ret.length < minSize)
			throw new ParamValueException(this, "String array must have at least " + minSize + " items");
	
		if (maxSize != null && ret.length > maxSize)
			throw new ParamValueException(this, "String array cannot have more than " + maxSize + " items");
		
	}

	@Override
	public String[] getValue(String data) {
		
		if (data == null)
			return null;
		
		String[] ret = data.split(",");
		
		if (maxSize != null && ret.length > maxSize)
			return null;
		
		return ret;
	}

}
