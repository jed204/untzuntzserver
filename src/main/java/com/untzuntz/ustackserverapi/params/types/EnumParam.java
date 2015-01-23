package com.untzuntz.ustackserverapi.params.types;

import com.untzuntz.ustackserverapi.APIException;

/**
 * Validates an enumeration type
 */
public abstract class EnumParam extends BaseParam
{
	public EnumParam(String n, String d) {
		super(n, d);
	}
	
	public abstract String getTypeDescription();
	
	public <T extends Enum<T>> String enumValues(Class<T> enumType) {
    	StringBuffer buf = new StringBuffer();
        for (T c : enumType.getEnumConstants()) {
        	buf.append(c.name()).append(", ");
        }
        return buf.toString();
	}
	
	public <T extends Enum<T>> boolean validate(Class<T> enumType, String data) throws APIException {
        for (T c : enumType.getEnumConstants()) {
        	
        	if (c.name().equals(data))
        		return true;
        }
        return false;
	}
	
	public <T extends Enum<T>> Enum<?> getValue(Class<T> enumType, String data) {
        for (T c : enumType.getEnumConstants()) {
        	
        	if (c.name().equals(data))
        		return c;
        }
		return null;
	}
}
