package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.ParamTypeInt;
import com.untzuntz.ustackserverapi.params.BaseParam;
import com.untzuntz.ustackserverapi.params.ParamValueException;
import com.untzuntz.ustackserverapi.params.ValidatedParam;

public class IntParam extends BaseParam implements ParamTypeInt,ValidatedParam {
	
	private Integer minVal;
	private Integer maxVal;

	/**
	 * Setup an integer parameter with a min and max value
	 * @param minVal
	 * @param maxVal
	 */
	public IntParam(Integer minVal, Integer maxVal)
	{
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * Setup a integer parameter with just a maximum value
	 * @param minLen
	 */
	public IntParam(Integer maxVal)
	{
		this.maxVal = maxVal;
	}

	public Integer getMinVal() {
		return minVal;
	}

	public void setMinVal(Integer minVal) {
		this.minVal = minVal;
	}

	public Integer getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Integer maxVal) {
		this.maxVal = maxVal;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minVal != null)
			throw new ParamValueException(this, "Integer must be greater than " + minVal);

		int intVal = -1;
		try {
			intVal = Integer.valueOf(data);
		} catch (NumberFormatException nfe) {
			throw new ParamValueException(this, "Field must have a valid value");
		}
		
		if (minVal != null && intVal < minVal)
			throw new ParamValueException(this, "Integer must be greater than " + minVal);
	
		if (maxVal != null && intVal > maxVal)
			throw new ParamValueException(this, "Integer must be less than " + maxVal);
		
	}

}
