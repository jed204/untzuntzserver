package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;

public interface ValidatedParam {

	public void validate(String data) throws APIException;

}
