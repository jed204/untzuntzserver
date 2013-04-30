package com.untzuntz.ustackserverapi.params;

import java.util.ArrayList;
import java.util.List;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.params.exceptions.ParamGroupException;

/**
 * Provide validation against one set of parameters OR another
 * 
 * @author jdanner
 *
 */
public class OrParam implements Validated {

	private String name;
	private List<Validated> params;

	public OrParam(String name) {
		params = new ArrayList<Validated>();
		this.name = name;
	}
	
	public OrParam add(AndParam param) {
		params.add(param);
		return this;
	}
	
	public OrParam add(OrParam param) {
		params.add(param);
		return this;
	}
	
	public OrParam add(APICallParam param)
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

		if (preExcep.getChildExceptions().size() == params.size())
			throw preExcep;
		
	}

}
