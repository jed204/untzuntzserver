package com.untzuntz.ustackserverapi.params;

import org.bson.BasicBSONObject;

import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.JSONParam;
import com.untzuntz.ustackserverapi.params.types.LongParam;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.params.types.StringArrayParam;
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
	public static final ParameterDefinitionInt<String> json_response_code_inject = new StringParam("json_response_code_inject", "If set to false, the response code will NOT be included in the JSON and the HTTP response overridden");
	
	public static final ParameterDefinitionInt<Integer> page = new IntParam("page", "Page number (starts at 0)", 0, null);
	public static final ParameterDefinitionInt<Integer> items_per_page = new IntParam("items_per_page", "Number of items per page", 1, 100);
	public static final ParameterDefinitionInt<Integer> items_per_page_no_max = new IntParam("items_per_page", "Number of items per page", 1, 9999);

	public static final ParameterDefinitionInt<String> resource_name = new StringParam("resource_name", "Resource Name", 1, 255);
	public static final ParameterDefinitionInt<String> resource_type = new StringParam("resource_type", "Resource Type", 1, 255);
	public static final ParameterDefinitionInt<String> link_action_class = new StringParam("link_action_class", "Link action class", 1, 255);
	public static final ParameterDefinitionInt<String> support_email = new StringParam("support_email", "Support email address", 1, 255);
	public static final ParameterDefinitionInt<String> internal_name = new StringParam("internal_name", "Resource Internal Name", 1, 255);
	public static final ParameterDefinitionInt<String[]> managed_by = new StringArrayParam("managed_by", "A list of partners that are managed by this resource");
	public static final ParameterDefinitionInt<String[]> can_manage = new StringArrayParam("can_manage", "A list of partners that this resource can manage");
	public static final ParameterDefinitionInt<String> role_name = new StringParam("role_name", "Role Name", 1, 255);
	public static final ParameterDefinitionInt<String> permission = new StringParam("permission", "Permission Name", 1, 255);
	
	public static final ParameterDefinitionInt<String> target_client_id = new StringParam("target_client_id", "Target Client ID for the API");
	
	public static final ParameterDefinitionInt<BasicBSONObject> x_object = new JSONParam("x_object", "A JSON Object");
	public static final ParameterDefinitionInt<String> api_key = new StringParam("api_key", "Your API Key");
	public static final ParameterDefinitionInt<String> client_id = new StringParam("client_id", "Your Client ID for the API");
	public static final ParameterDefinitionInt<Long> token_life = new LongParam("token_life", "Time till token expires (ms)", 9999999L);
	public static final ParameterDefinitionInt<String> token = new StringParam("token", "User Access Token");
	public static final ParameterDefinitionInt<String> user_identifier = new StringParam("user_identifier", "User Identifier");
	public static final ParameterDefinitionInt<String> secret = new StringParam("secret", "User Secret");

	public static final ParameterDefinitionInt<String> recaptcha_challenge_field = new StringParam("recaptcha_challenge_field", "Captcha Challenge", 1, 255);
	public static final ParameterDefinitionInt<String> recaptcha_response_field = new StringParam("recaptcha_response_field", "Captcha Response", 1, 255);

}
