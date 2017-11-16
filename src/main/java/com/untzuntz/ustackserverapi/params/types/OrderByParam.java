package com.untzuntz.ustackserverapi.params.types;

import java.util.HashMap;
import java.util.List;

import com.untzuntz.coredata.OrderBy;
import com.untzuntz.coredata.OrderBy.Direction;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;
import com.untzuntz.ustackserverapi.util.DataUtil;
import com.untzuntz.ustackserverapi.util.FieldDatabaseMap;

/**
 * Converts an order by value to a OrderBy object with a asc/desc value
 * 
 * @author jdanner
 *
 */
public class OrderByParam extends BaseParam implements ParameterDefinitionInt<OrderBy>  {

	private HashMap<String,FieldDatabaseMap> validFields;
	
	public OrderByParam(String name, String desc) {
		super(name, desc);
		this.validFields = new HashMap<String,FieldDatabaseMap>();
	}
	
	public String getTypeDescription() {
		return "A field name from the valid fields list. Optionally prepend with + (ascending) or - (desc) - ex: -age would order by age with the highest first.";
	}
	
	
	public OrderByParam addFilterFields(List<FieldDatabaseMap> list) {
		for (FieldDatabaseMap fdm : DataUtil.safeList(list))
			addFilterField(fdm);
		return this;
	}
	
	public OrderByParam addFilterField(String fieldName, Class<?> typeClass, String classField)
	{
		return addFilterField(new FieldDatabaseMap(fieldName, typeClass, classField, null));
	}
	
	public OrderByParam addFilterField(FieldDatabaseMap fdm)
	{
		validFields.put(fdm.getValueType().getName(), fdm);
		return this;
	}

	public String getDescription() {
		StringBuffer ret = new StringBuffer();
		ret.append(super.getDescription());
		
		if (!super.getDescription().endsWith(" "))
			ret.append(" ");
		
		ret.append(SearchFilterParam.getValidFieldsCSV(validFields));
		
		return ret.toString();
	}
	
	@Override
	public void validate(String data) throws APIException {

		String fieldName = data;
		if (fieldName.startsWith("+") || fieldName.startsWith("-"))
			fieldName = fieldName.substring(1);
		
		FieldDatabaseMap map = validFields.get(fieldName);
		if (map == null)
			throw new ParamValueException(this, "Invalid field for sorting (" + fieldName + ")");
		
	}
	
	@Override
	public OrderBy getValue(String data) {
		
		if (data == null)
			return null;
		
		OrderBy orderBy = new OrderBy();
		
		if (data.startsWith("+"))
		{
			orderBy.setDirection(Direction.Asc);
			data = data.substring(1);
		}
		else if (data.startsWith("-"))
		{
			orderBy.setDirection(Direction.Desc);
			data = data.substring(1);
		}

		FieldDatabaseMap map = validFields.get(data);
		if (map == null)
			return null;
		
		orderBy.setFieldName(map.getFieldName());
		
		return orderBy;
		
	}

}
