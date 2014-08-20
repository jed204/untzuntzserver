package com.untzuntz.ustackserverapi.auth;

public enum CachedAccess {

	True("T", 300),
	False("F", 60),
	None("N", 30);
	
	private String type;
	private int cacheTTL;
	private CachedAccess(String type, int cacheTTL) {
		this.type = type;
		this.cacheTTL = cacheTTL;
	}
	
	public String getType() {
		return type;
	}
	
	public int getCacheTTL() {
		return cacheTTL;
	}

}