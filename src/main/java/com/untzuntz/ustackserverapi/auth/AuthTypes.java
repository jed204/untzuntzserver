package com.untzuntz.ustackserverapi.auth;

/**
 * Some standard authentication methods
 * 
 * @author jdanner
 *
 */
public class AuthTypes {

	public static final UserNamePasswordAuth UserNamePassword = new UserNamePasswordAuth();
	public static final APIClientKeyAuth ClientKey = new APIClientKeyAuth();
	public static final APIClientKeyTokenSecretAuth ClientKeyTokenSecret = new APIClientKeyTokenSecretAuth();
	public static final AccessTokenAuth AccessTokenAuth = new AccessTokenAuth();
	public static final GuestAccessTokenAuth GuestAccessTokenAuth = new GuestAccessTokenAuth();
	public static final APIClientKeyUserAuth ClientKeyUser = new APIClientKeyUserAuth();
	
}
