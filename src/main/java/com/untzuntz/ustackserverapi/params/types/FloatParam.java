package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class FloatParam extends BaseParam implements ParameterDefinitionInt<Float> {
	
	private Float minVal;
	private Float maxVal;

	/**
	 * Setup an Float parameter with a min and max value
	 * @param minVal
	 * @param maxVal
	 */
	public FloatParam(String n, String d, Float minVal, Float maxVal)
	{
		super(n, d);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * Setup a Float parameter with just a maximum value
	 * @param minLen
	 */
	public FloatParam(String n, String d, Float maxVal)
	{
		super(n, d);
		this.maxVal = maxVal;
	}
	
	public String getTypeDescription() {
		
		String mv = "" + minVal;
		if (minVal == null)
			mv = "-2^63";
		String mxv = "" + maxVal;
		if (maxVal == null)
			mxv = "2^63";

		return "A number between " + mv + " and " + mxv;
	}

	public Float getMinVal() {
		return minVal;
	}

	public void setMinVal(Float minVal) {
		this.minVal = minVal;
	}

	public Float getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Float maxVal) {
		this.maxVal = maxVal;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minVal != null)
			throw new ParamValueException(this, "Float must be greater than " + minVal);

		Float intVal = -1.0f;
		try {
			intVal = Float.valueOf(data);
		} catch (NumberFormatException nfe) {
			throw new ParamValueException(this, "Field must have a valid value");
		}
		
		if (minVal != null && intVal < minVal)
			throw new ParamValueException(this, "Float must be greater than " + minVal);
	
		if (maxVal != null && intVal > maxVal)
			throw new ParamValueException(this, "Float must be less than " + maxVal);
		
	}

	@Override
	public Float getValue(String data) {
		if (data == null)
			return null;
		
		try {
			
			
			Float ret = Float.valueOf(data);
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
