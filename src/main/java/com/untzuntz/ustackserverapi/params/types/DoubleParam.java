package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class DoubleParam extends BaseParam implements ParameterDefinitionInt<Double> {
	
	private Double minVal;
	private Double maxVal;

	/**
	 * Setup an Double parameter with a min and max value
	 * @param minVal
	 * @param maxVal
	 */
	public DoubleParam(String n, String d, Double minVal, Double maxVal)
	{
		super(n, d);
		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * Setup a Double parameter with just a maximum value
	 * @param minLen
	 */
	public DoubleParam(String n, String d, Double maxVal)
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

	public Double getMinVal() {
		return minVal;
	}

	public void setMinVal(Double minVal) {
		this.minVal = minVal;
	}

	public Double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Double maxVal) {
		this.maxVal = maxVal;
	}

	@Override
	public void validate(String data) throws APIException {
		
		if (data == null && minVal != null)
			throw new ParamValueException(this, "Double must be greater than " + minVal);

		double intVal = -1;
		try {
			intVal = Double.valueOf(data);
		} catch (NumberFormatException nfe) {
			throw new ParamValueException(this, "Field must have a valid value");
		}
		
		if (minVal != null && intVal < minVal)
			throw new ParamValueException(this, "Double must be greater than " + minVal);
	
		if (maxVal != null && intVal > maxVal)
			throw new ParamValueException(this, "Double must be less than " + maxVal);
		
	}

	@Override
	public Double getValue(String data) {
		if (data == null)
			return null;
		
		try {
			
			
			double ret = Double.valueOf(data);
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
