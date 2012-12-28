package com.untzuntz.ustackserverapi.params.types;

import java.text.ParseException;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;
import com.untzuntz.ustackserverapi.params.types.util.DateRange;

public class DateRangeParam extends BaseParam implements ParameterDefinitionInt<DateRange>
{
	public DateRangeParam(String n, String d) {
		super(n, d);
	}

	public String getTypeDescription() {
		return "A date/time or date/time range formatted as YYYYMMDDHHMMSS (ex: 20121201 or 20121201-20121223150230)";
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
	public DateRange getValue(String data) {
		try {
			return new DateRange(data);
		} catch (ParseException e) {
			return null;
		}
	}

}
