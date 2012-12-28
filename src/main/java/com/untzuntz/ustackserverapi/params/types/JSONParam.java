package com.untzuntz.ustackserverapi.params.types;

import org.bson.BasicBSONObject;

import com.mongodb.util.JSON;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class JSONParam extends BaseParam implements ParameterDefinitionInt<BasicBSONObject>
{
	public JSONParam(String n, String d) {
		super(n, d);
	}
	
	public String getTypeDescription() {
		return "A properly formatted JSON text (ex: { key : \"value\" })";
	}

	@Override
	public void validate(String data) throws APIException {
		
		BasicBSONObject bs = (BasicBSONObject)JSON.parse(data);
		
		if (bs != null)
			return;

		throw new ParamValueException(this, "Could not parse parameter");
	}
	
	@Override
	public BasicBSONObject getValue(String data) {
		
		return (BasicBSONObject)JSON.parse(data);
		
	}

}
