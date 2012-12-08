package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.version.VersionInt;

public class APICallParam implements Validated {

	public ParamInt paramDetails;
	public String defaultValue;
	public VersionInt since;
	private String desc;
	
	public APICallParam(ParamInt param)
	{
		this.paramDetails = param;
	}
	
	public APICallParam(ParamInt param, String defaultValue, String desc)
	{
		this.paramDetails = param;
		this.defaultValue = defaultValue;
		this.desc = desc;
	}
	
	public APICallParam(ParamInt param, String defaultValue)
	{
		this.paramDetails = param;
		this.defaultValue = defaultValue;
	}
	
	public APICallParam(ParamInt param, VersionInt since)
	{
		this.paramDetails = param;
		this.since = since;
	}
	
	public ParamInt getParamDetails() {
		return paramDetails;
	}
	
	public VersionInt getVersion() {
		return since;
	}
	
	public APICallParam setDescription(String desc) {
		this.desc = desc;
		return this;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public String getDescription() {
		if (desc != null)
			return desc;
		
		return paramDetails.getDescription();
	}
	
	@Override
	public void validate(CallParameters data) throws APIException {
		paramDetails.getType().validate(data.getParameter(paramDetails.getName()));
	}

}
