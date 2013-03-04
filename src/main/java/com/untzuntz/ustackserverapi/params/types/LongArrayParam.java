package com.untzuntz.ustackserverapi.params.types;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

/**
 * Validates a Long array type with a min and max item count (optional)
 */
public class LongArrayParam extends BaseParam implements ParameterDefinitionInt<Long[]> {
	
	private Integer minSize;
	private Integer maxSize;

	/**
	 * Setup core Long array parameter
	 * 
	 * @param n
	 * @param d
	 */
	public LongArrayParam(String n, String d) {
		super(n, d);
	}
	
	/**
	 * Setup a Long array parameter with a min and max item count
	 * @param minSize
	 * @param maxSize
	 */
	public LongArrayParam(String n, String d, Integer minSize, Integer maxSize)
	{
		super(n, d);
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	/**
	 * Setup a Long array parameter with just a minimum length
	 * @param minLen
	 */
	public LongArrayParam(String n, String d, Integer minSize)
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
		
		return "A Long array of text separated by commas" + msg + " (example: 124,444)";
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minSize != null)
			throw new ParamValueException(this, "Long array must have at least " + minSize + " items");

		if (data == null)
			throw new ParamValueException(this, "The Long array has no value");
		
		Long[] ret = getLongs(data.split(","));
		if (minSize != null && ret.length < minSize)
			throw new ParamValueException(this, "Long array must have at least " + minSize + " items");
	
		if (maxSize != null && ret.length > maxSize)
			throw new ParamValueException(this, "Long array cannot have more than " + maxSize + " items");
		
	}

	@Override
	public Long[] getValue(String data) {
		
		if (data == null)
			return null;
		
		Long[] ret = getLongs(data.split(","));
		if (maxSize != null && ret.length > maxSize)
			return null;
		
		return ret;
	}

	private Long[] getLongs(String[] vals) {
		
		List<Long> ret = new ArrayList<Long>();
		for (String val : vals)
			try { ret.add( Long.valueOf(val.trim()) ); } catch (NumberFormatException nfe) {}
		
		Long[] retArray = new Long[ret.size()];
		return ret.toArray(retArray);
	}
	
}
