package com.untzuntz.ustackserverapi.util;

import com.untzuntz.ustack.aaa.UStackPermissionEnum;

public enum APIPerms implements UStackPermissionEnum {
	
	APIClientManager("API access to manage more API clients"),
	APIResourceManager("API access to manage resources"),
	APIRoleManager("API Access to manage roles of resources");
	
	private String desc;
	private APIPerms(String desc)
	{
		this.desc = desc;
	}

	@Override
	public String getPermission() {
		return super.toString();
	}

	@Override
	public String getDesc() {
		return desc;
	}

}
