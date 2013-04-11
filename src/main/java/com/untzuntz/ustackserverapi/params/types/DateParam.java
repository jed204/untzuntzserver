package com.untzuntz.ustackserverapi.params.types;

import java.text.ParseException;
import java.util.Date;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;
import com.untzuntz.ustackserverapi.params.types.util.DateRange;

/**
 * A Date parameter
 * 
 * @author jdanner
 *
 */
public class DateParam extends BaseParam implements ParameterDefinitionInt<Date>
{
	public DateParam(String n, String d) {
		super(n, d);
	}

	public String getTypeDescription() {
		return "A date/time formatted as YYYYMMDDHHMMSS (ex: 20121201 or 20121223150230)";
	}
	
	@Override
	public void validate(String data) throws APIException {

		try {
			new DateRange(data);
		} catch (ParseException e) {
			throw new ParamValueException(this, "Your dates must be in the format of YYYYMMDDHHMMSS");
		}
		
	}
	
	@Override
	public Date getValue(String data) {
		try {
			return new DateRange(data).getStart();
		} catch (ParseException e) {
			return null;
		}
	}

}
