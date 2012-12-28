package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class LongParam extends BaseParam implements ParameterDefinitionInt<Long> {
	
	private Long minVal;
	private Long maxVal;

	/**
	 * Setup an long parameter with a min and max value
	 * @param minVal
	 * @param maxVal
	 */
	public LongParam(String n, String d, Long minVal, Long maxVal)
	{
		super(n, d);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * Setup a long parameter with just a maximum value
	 * @param minLen
	 */
	public LongParam(String n, String d, Long maxVal)
	{
		super(n, d);
		this.maxVal = maxVal;
	}
	
	public String getTypeDescription() {
		return "A number between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE + " (ex: 544304)";
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

	@Override
	public Long getValue(String data) {
		try {
			
			
			long ret = Long.valueOf(data);
			if (minVal != null && ret < minVal)
				ret = minVal;
			if (maxVal != null && ret > maxVal)
				ret = maxVal;
			
			return ret; 

		} catch (NumberFormatException nfe) {
			return null;
		}
	}

}
