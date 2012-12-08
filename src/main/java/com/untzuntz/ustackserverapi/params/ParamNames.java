package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.BasicParamTypes;
import com.untzuntz.ustackserverapi.ParamTypeInt;

public enum ParamNames implements ParamInt {

	TestA("test_id_a", "This is a test description A", BasicParamTypes.Integer),
	TestB("test_id_b", "This is a test description B", BasicParamTypes.Integer),
	TestC("test_id_c", "This is a test description C", BasicParamTypes.Integer);
	
	private String name;
	private String desc;
	private ParamTypeInt type;
	private ParamNames(String n, String d, ParamTypeInt t) {
		this.name = n;
		this.desc = d;
		this.type = t;
				
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return desc;
	}
	public ParamTypeInt getType() {
		return type;
	}
	
}
