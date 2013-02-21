package com.untzuntz.ustackserverapi.params.types;

import org.bson.BasicBSONObject;

import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class JSONParam extends BaseParam implements ParameterDefinitionInt<BasicBSONObject>
{
	public JSONParam(String n, String d) {
		super(n, d);
	}
	
	public String getTypeDescription() {
		return "Properly formatted JSON text (ex: { key : \"value\" })";
	}

	@Override
	public void validate(String data) throws APIException {
		
		System.out.println("Field named [" + this.getName() + "] => Data [" + data + "]");
		
		BasicBSONObject bs = null;
		try {
			bs = (BasicBSONObject)JSON.parse(data);
		} catch (JSONParseException jpe) {
			throw new ParamValueException(this, "Could not parse parameter");
		}
		
		if (bs != null)
			return;

		throw new ParamValueException(this, "Could not parse parameter");
	}
	
	@Override
	public BasicBSONObject getValue(String data) {

		BasicBSONObject bs = null;
		try {
			bs = (BasicBSONObject)JSON.parse(data);
		} catch (Exception jpe) {}
		
		return bs;
				
	}

}
