package com.untzuntz.ustackserverapi.params;

import com.untzuntz.ustackserverapi.params.types.IntParam;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.params.types.StringParam;

public class ParamNames {

	public static final ParameterDefinitionInt<Integer> TestA = new IntParam("test_id_a", "This is a test description A");
	public static final ParameterDefinitionInt<Integer> TestB = new IntParam("test_id_b", "This is a test description B");
	public static final ParameterDefinitionInt<Integer> TestC = new IntParam("test_id_c", "This is a test description C");

	public static final ParameterDefinitionInt<String> RequestSignature = new StringParam("sig", "Request Signature from the client");
	public static final ParameterDefinitionInt<String> UID = new StringParam("uid", "A unique ID");
	public static final ParameterDefinitionInt<String> UserName = new StringParam("userName", "Username");
	public static final ParameterDefinitionInt<String> DeviceID = new StringParam("deviceId", "Device ID");
	public static final ParameterDefinitionInt<String> AccessCode = new StringParam("accessCode", "Access Code");
	
}
