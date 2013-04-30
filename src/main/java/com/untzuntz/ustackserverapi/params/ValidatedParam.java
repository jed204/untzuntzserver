package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.APIException;

/**
 * Interface for validating parameters
 * 
 * @author jdanner
 *
 */
public interface ValidatedParam {

	public void validate(String data) throws APIException;

}
