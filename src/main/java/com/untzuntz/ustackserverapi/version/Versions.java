package com.untzuntz.ustackserverapi.version;

public enum Versions implements VersionInt {

	Version100("1.0.0", "1/1/1900");
	
	private String versionId;
	private String releaseDate;
	private Versions(String versionId, String releaseDate) {
		this.versionId = versionId;
		this.releaseDate = releaseDate;
	}
	
	public String getVersionId() {
		return versionId;
	}
	
	public String getReleaseDate() {
		return releaseDate;
	}
	
}
