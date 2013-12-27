package com.untzuntz.ustackserverapi.util;

import java.io.File;

public class UploadedFile {

	private File file;
	private String fileName;
	private String contentType;
	
	public UploadedFile(File file, String fileName, String contentType)
	{
		this.file = file;
		this.fileName = fileName;
		this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}
	
}
