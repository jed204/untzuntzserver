package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.params.types.StringParam;

/**
 * Some basic and common parameter names
 * 
 * @author jdanner
 *
 */
public class ParamNames {

	public static final ParameterDefinitionInt<Integer> TestA = new IntParam("test_id_a", "This is a test description A");
	public static final ParameterDefinitionInt<Integer> TestB = new IntParam("test_id_b", "This is a test description B");
	public static final ParameterDefinitionInt<Integer> TestC = new IntParam("test_id_c", "This is a test description C");

	public static final ParameterDefinitionInt<String> app_name = new StringParam("app_name", "Your API application name");
	public static final ParameterDefinitionInt<String> client_ver = new StringParam("client_ver", "Your API client version number");
	public static final ParameterDefinitionInt<String> RequestSignature = new StringParam("sig", "Request Signature from the client");
	public static final ParameterDefinitionInt<String> UID = new StringParam("uid", "A unique ID");
	public static final ParameterDefinitionInt<String> username = new StringParam("username", "Username");
	public static final ParameterDefinitionInt<String> password = new StringParam("password", "Username");
	public static final ParameterDefinitionInt<String> DeviceID = new StringParam("deviceId", "Device ID");
	public static final ParameterDefinitionInt<String> access_code = new StringParam("access_code", "Access Code");
	public static final ParameterDefinitionInt<String> json_callback = new StringParam("json_callback", "JSON Callback Function name");
	

	
	public static final ParameterDefinitionInt<String> api_key = new StringParam("api_key", "Your API Key");
	public static final ParameterDefinitionInt<String> client_id = new StringParam("client_id", "Your Client ID for the API");
	public static final ParameterDefinitionInt<String> token = new StringParam("token", "User Access Token");
	public static final ParameterDefinitionInt<String> user_identifier = new StringParam("user_identifier", "User Identifier");
	public static final ParameterDefinitionInt<String> secret = new StringParam("secret", "User Secret");

}
