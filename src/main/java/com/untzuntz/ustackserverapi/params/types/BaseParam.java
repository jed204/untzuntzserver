package com.untzuntz.ustackserverapi.params.types;



public abstract class BaseParam {

//	public static ParameterDefinitionInt getParam(String name, String desc, Class type)
//	{
//		ParameterDefinitionInt ret = (ParameterDefinitionInt)type.newInstance();
//		
//		return ret;
//	}
//	
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
