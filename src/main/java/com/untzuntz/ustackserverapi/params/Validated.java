package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;

public interface Validated {

	public void validate(CallParameters data) throws APIException;

}
