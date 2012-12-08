package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.BaseParam;
import com.untzuntz.ustackserverapi.params.ParamValueException;
import com.untzuntz.ustackserverapi.params.ValidatedParam;

public class LongParam extends BaseParam implements ParamTypeInt,ValidatedParam {
	
	private Long minVal;
	private Long maxVal;

	/**
	 * Setup an long parameter with a min and max value
	 * @param minVal
	 * @param maxVal
	 */
	public LongParam(Long minVal, Long maxVal)
	{
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * Setup a long parameter with just a maximum value
	 * @param minLen
	 */
	public LongParam(Long maxVal)
	{
		this.maxVal = maxVal;
	}

	public Long getMinVal() {
		return minVal;
	}

	public void setMinVal(Long minVal) {
		this.minVal = minVal;
	}

	public Long getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Long maxVal) {
		this.maxVal = maxVal;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minVal != null)
			throw new ParamValueException(this, "Long must be greater than " + minVal);

		long intVal = -1;
		try {
			intVal = Long.valueOf(data);
		} catch (NumberFormatException nfe) {
			throw new ParamValueException(this, "Field must have a valid value");
		}
		
		if (minVal != null && intVal < minVal)
			throw new ParamValueException(this, "Long must be greater than " + minVal);
	
		if (maxVal != null && intVal > maxVal)
			throw new ParamValueException(this, "Long must be less than " + maxVal);
		
	}

}
