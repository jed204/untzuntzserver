package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;
import com.untzuntz.ustackserverapi.params.types.util.DateRange;
import com.untzuntz.ustackserverapi.params.types.util.DateRangeObjectId;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.util.Date;

public class DateRangeObjectIdParam extends BaseParam implements ParameterDefinitionInt<DateRangeObjectId>
{
    public DateRangeObjectIdParam(String n, String d) {
        super(n, d);
    }

    public String getTypeDescription() {
        return "A date/time or date/time range formatted as YYYYMMDDHHMMSS (ex: 20121201 or 20121201-20121223150230)";
    }

    @Override
    public void validate(String data) throws APIException {

        try {
            new ObjectId(new DateRange(data).getStart(), 0, (short)0, 0);
        } catch (ParseException e) {
            throw new ParamValueException(this, "Your dates must be in the format of YYYYMMDDHHMMSS");
        }

    }

    @Override
    public DateRangeObjectId getValue(String data) {
        try {
            DateRangeObjectId ret = new DateRangeObjectId();
            ret.range = new DateRange(data);
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

}
