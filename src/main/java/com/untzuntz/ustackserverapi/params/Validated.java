package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.CallParameters;

/**
 * Interface for validating parameter values
 * 
 * @author jdanner
 *
 */
public interface Validated {

	public void validate(CallParameters data) throws APIException;

}
