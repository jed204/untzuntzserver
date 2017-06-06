package com.untzuntz.ustackserverapi.exceptions;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.untzuntz.ustackserverapi.APIExceptionDocumentation;

/**
 * The call you have made is just not correct (you're probably missing a required value)
 * 
 * @author jdanner
 *
 */
public class BadRequestException extends APIStatusException implements APIExceptionDocumentation {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public BadRequestException(String message)
	{
		super(message);
		this.message = message;
	}

	public BadRequestException(String message, HttpResponseStatus status)
	{
		super(message);
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getReason() {
		return "Your request was invalid and cannot be processed.";
	}

}