package com.untzuntz.ustackserverapi.params.types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.untzuntz.coredata.SearchFilters;
import com.untzuntz.ustackserver.Main;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;
import com.untzuntz.ustackserverapi.util.DataUtil;
import com.untzuntz.ustackserverapi.util.FieldDatabaseMap;
import com.untzuntz.ustackserverapi.util.JsonSearchFilter;

/**
 * Converts a parameter to a SearchFilters object. Uses the JsonSearchFilter class as the data model for filter entries.
 * 
 * @author jdanner
 *
 */
public class SearchFilterParam extends BaseParam implements ParameterDefinitionInt<SearchFilters>  {

	static 		Logger           	logger                  = Logger.getLogger(SearchFilterParam.class);

	private static final Type listType = new TypeToken<List<JsonSearchFilter>>() {}.getType();
	private HashMap<String,FieldDatabaseMap> validFields;
	
	public HashMap<String,FieldDatabaseMap> getValidFields() {
		return validFields;
	}
	
	public SearchFilterParam(String name, String desc) {
		super(name, desc);
		this.validFields = new HashMap<String,FieldDatabaseMap>();
	}
	
	public SearchFilterParam addFilterFields(List<FieldDatabaseMap> list) {
		for (FieldDatabaseMap fdm : DataUtil.safeList(list))
			addFilterField(fdm);
		return this;
	}
	
	public SearchFilterParam addFilterField(String fieldName, Class<?> typeClass, String classField, ParameterDefinitionInt<?> paramType)
	{
		return addFilterField(new FieldDatabaseMap(fieldName, typeClass, classField, paramType));
	}
	
	public SearchFilterParam addFilterField(FieldDatabaseMap fdm)
	{
		validFields.put(fdm.valueType.getName(), fdm);
		return this;
	}

	
	public String getTypeDescription() {
		return "JSON array with one or more filter fields (ex: [{ fieldName : \"name\", type : \"Equals\", value : \"me\" }] )";
	}
	
	public String getDescription() {
		StringBuffer ret = new StringBuffer();
		ret.append(super.getDescription());
		
		if (!super.getDescription().endsWith(" "))
			ret.append(" ");

		ret.append(getValidFieldsCSV(validFields));
		
		return ret.toString();
	}

	public static String getValidFieldsCSV(HashMap<String,FieldDatabaseMap> validFields) {
		
		StringBuffer ret = new StringBuffer();
		
		ret.append("(Valid fields: ");
		
		Iterator<String> it = validFields.keySet().iterator();

		if (!it.hasNext())
			ret.append("NO FIELD DEFINED");
		
		while (it.hasNext()) {
			ret.append("'").append(it.next()).append("'");
			
			if (it.hasNext())
				ret.append(", ");
		}
		
		ret.append(")");

		return ret.toString();
	}
	
	@Override
	public void validate(String data) throws APIException {
		
		List<JsonSearchFilter> jsonFilters = null;
		try {
			jsonFilters = Main.gson.fromJson(data, listType); 
		} catch (JsonSyntaxException jse) {
			throw new ParamValueException(this, "Could not parse parameter");
		}
		
		if (jsonFilters == null || jsonFilters.isEmpty())
			throw new ParamValueException(this, "No search filters provided");
		
		// check for bad/invalid filters
		for (JsonSearchFilter filter : jsonFilters)
		{
			FieldDatabaseMap map = validFields.get( filter.getFieldName() );
			if (map == null)
				throw new ParamValueException(this, "Invalid field for filter (" + filter.getFieldName() + ")");
			
			// check for valid typing
			try {
				map.valueType.validate(filter.getValue());
			} catch (APIException apiErr) {
				throw new ParamValueException(this, "Invalid value type for filter (" + filter.getFieldName() + ")");
			}
		}
		
	}

	@Override
	public SearchFilters getValue(String data) {
		
		if (data == null)
			return null;

		List<JsonSearchFilter> jsonFilters = Main.gson.fromJson(data, listType); 
		if (jsonFilters == null || jsonFilters.isEmpty())
			return null;
			
		SearchFilters filters = new SearchFilters();
		for (JsonSearchFilter filter : jsonFilters)
		{
			FieldDatabaseMap map = validFields.get( filter.getFieldName() );
			if (map != null) // ignore unknown params
			{
				if (map.typeClass != null) // sql fields have this, mongodb fields do not
				{
					filters.add(map.typeClass, map.classField, filter.getType(), map.valueType.getValue( filter.getValue() ));
				}
				else
				{
					if (filter.getValues() != null)
						filters.add(map.classField, filter.getType(), filter.getValues());
					else
						filters.add(map.classField, filter.getType(), map.valueType.getValue( filter.getValue() ));
				}
			}
			else
				logger.info("Caller provided an invalid filter parameter [" + filter.getFieldName() + "] " + getValidFieldsCSV(validFields));
		}
		
		return filters;
	}
	
}