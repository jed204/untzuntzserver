package com.untzuntz.ustackserverapi.auth;

import com.untzuntz.ustack.data.APIClient;

/**
 * Holding class for an API client and other data that may be useful within calls
 * 
 * @author jdanner
 *
 */
public class ClientData {

	private APIClient api;

	public APIClient getApi() {
		return api;
	}

	public void setApi(APIClient api) {
		this.api = api;
	}
}
