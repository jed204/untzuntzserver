package com.untzuntz.ustackserverapi.params.types;

/**
 * Helper class to capture standard info for a parameter (name and description)
 * 
 * @author jdanner
 *
 */
public abstract class BaseParam {

	protected String name;
	protected String desc;
	
	@SuppressWarnings("unused")
	private BaseParam() {
		// stub
	}
	
	public BaseParam(String name, String desc)
	{
		this.name = name;
		this.desc = desc;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription() {
		return desc;
	}	
	
	public void setDescription(String d) {
		desc = d;
	}


	public boolean hasValue(String data) {

		if (data == null || data.length() == 0)
			return false;
		
		return true;
	}

}
