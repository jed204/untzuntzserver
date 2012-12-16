package com.untzuntz.ustackserverapi.params;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.params.exceptions.ParamGroupException;

public class AndParam implements Validated {

	private String name;
	private List<Validated> params;

	public AndParam(String name) {
		params = new ArrayList<Validated>();
		this.name = name;
	}
	
	public AndParam add(OrParam param) {
		params.add(param);
		return this;
	}
	
	public AndParam add(AndParam param)
	{
		params.add(param);
		return this;
	}
	
	public AndParam add(APICallParam param)
	{
		params.add(param);
		return this;
	}
	
	@Override
	public void validate(CallParameters data) throws APIException {

		ParamGroupException preExcep = new ParamGroupException(name);
		
		for (Validated val : params)
		{
			try {
				val.validate(data);
			} catch (APIException e) {
				preExcep.addException(e);
			}
		}

		if (preExcep.getChildExceptions().size() > 0)
			throw preExcep;
		
	}

}
