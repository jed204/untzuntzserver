package com.untzuntz.ustackserverapi.exceptions;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.untzuntz.ustackserverapi.APIException;

public class APIStatusException extends APIException {

	private static final long serialVersionUID = 1L;

	public APIStatusException() { 
		super();
	}
	
	public APIStatusException(String msg) {
		super(msg);
	}
	
	protected HttpResponseStatus status;

	public HttpResponseStatus getHttpStatus() {
		if (status == null)
			return HttpResponseStatus.FORBIDDEN;
		return status;
	}
	
}
