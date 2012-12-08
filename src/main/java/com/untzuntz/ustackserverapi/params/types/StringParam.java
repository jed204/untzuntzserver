package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.BaseParam;
import com.untzuntz.ustackserverapi.params.ParamValueException;
import com.untzuntz.ustackserverapi.params.ValidatedParam;

/**
 * Validates a string type with a min and max length (optional)
 */
public class StringParam extends BaseParam implements ParamTypeInt,ValidatedParam {
	
	private Integer minLen;
	private Integer maxLen;
	
	/**
	 * Setup a string parameter with a min and max length
	 * @param minLen
	 * @param maxLen
	 */
	public StringParam(Integer minLen, Integer maxLen)
	{
		this.minLen = minLen;
		this.maxLen = maxLen;
	}

	/**
	 * Setup a string parameter with just a minimum length
	 * @param minLen
	 */
	public StringParam(Integer minLen)
	{
		this.minLen = minLen;
	}
	
	public Integer getMinLen() {
		return minLen;
	}

	public void setMinLen(Integer minLen) {
		this.minLen = minLen;
	}

	public Integer getMaxLen() {
		return maxLen;
	}

	public void setMaxLen(Integer maxLen) {
		this.maxLen = maxLen;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minLen != null)
			throw new ParamValueException(this, "String must be at least " + minLen + " characters");

		if (minLen != null && data.length() < minLen)
			throw new ParamValueException(this, "String must be at least " + minLen + " characters");
	
		if (maxLen != null && data.length() > maxLen)
			throw new ParamValueException(this, "String cannot exceed " + maxLen + " characters");
		
	}


}
