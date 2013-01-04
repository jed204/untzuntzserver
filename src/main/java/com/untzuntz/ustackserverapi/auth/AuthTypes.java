package com.untzuntz.ustackserverapi.auth;


public class AuthTypes {

	public static final UserNamePasswordAuth UserNamePassword = new UserNamePasswordAuth();
	public static final APIClientKeyAuth ClientKey = new APIClientKeyAuth();
	public static final APIClientKeyTokenSecretAuth ClientKeyTokenSecret = new APIClientKeyTokenSecretAuth();
	public static final AccessTokenAuth AccessTokenAuth = new AccessTokenAuth();
	
}
