package com.untzuntz.ustackserverapi.exceptions;

import com.untzuntz.ustackserverapi.APIException;

public class UnknownObjectException extends APIException {

	private static final long serialVersionUID = 1L;

	public UnknownObjectException(String m) {
		super(m);
	}
	
}
