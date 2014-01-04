package com.untzuntz.ustackserverapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustack.aaa.UStackPermissionEnum;
import com.untzuntz.ustackserverapi.auth.AuthenticationInt;
import com.untzuntz.ustackserverapi.auth.AuthorizationInt;
import com.untzuntz.ustackserverapi.exceptions.BadRequestException;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.Validated;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.version.VersionInt;

@SuppressWarnings("rawtypes")
public class MethodDefinition {
	
    static Logger           		logger               	= Logger.getLogger(MethodDefinition.class);

    public static final int HASH_ENFORCEMENT_NONE = 0;
    public static final int HASH_ENFORCEMENT_WARN = 1;
    public static final int HASH_ENFORCEMENT_REJECT = 2;
    
	private String path;
	private boolean methodGET;
	private boolean methodPOST;
	private boolean methodPUT;
	private boolean methodDELETE;
	private boolean expectingFile;
	private AuthenticationInt authMethod;
	private Class apiClass;
	private HashMap<String,Object> data;
	private String methodName;
	private String description;
	private VersionInt sinceVersion;
	private List<Object> paramVal;
	private List<ParameterDefinitionInt<?>> variesParams;
	private int order;
	private String overrideResponse;
	private String hashKey;
	private String docGroup;
	private int hashEnforcementLevel;
	private List<APICallParam> apiParams;
	private List<AuthorizationInt> authorizationMethods;
	private HashMap<Class,Object> objectInstances;
	private HashMap<String,Method> methodInstances;
	private boolean allowNoClientVer;
	private boolean noLogging;
	
	public boolean isNoLogging() {
		return noLogging;
	}
	
	public void noLogging() {
		noLogging = true;
	}
	
	public boolean isExpectingFile() { 
		return expectingFile;
	}
	
	public void fileToBeUploaded() {
		expectingFile = true;
	}
	
	public String getDocumentationGroup() {
		return docGroup;
	}
	
	public MethodDefinition setDocumentationGroup(String dg) {
		docGroup = dg;
		return this;
	}
	
	public List<Object> getParameterValidation() {
		return paramVal;
	}
	
	public boolean isClientVerCheckDisabled() {
		return allowNoClientVer;
	}
	
	public MethodDefinition disableClientVerCheck() {
		allowNoClientVer = true;
		return this;
	}
	
	public MethodDefinition(String path, Class apiClass, String methodName) {
		this.path = path;
		this.apiClass = apiClass;
		this.methodName = methodName;
		this.apiParams = new ArrayList<APICallParam>();
		this.data = new HashMap<String,Object>();
		this.objectInstances = new HashMap<Class,Object>();
		this.methodInstances = new HashMap<String,Method>();
		this.paramVal = new ArrayList<Object>();
		this.variesParams = new ArrayList<ParameterDefinitionInt<?>>();
		this.authorizationMethods = new ArrayList<AuthorizationInt>();
		this.order = 1000;
	}

	public void setHashKey(int hashEnforcementLevel, String hashKey) {
		setHashKey(hashKey);
		setHashEnforcement(hashEnforcementLevel);
	}
	
	public String getHashKey() {
		return hashKey;
	}
	
	public void setHashKey(String k) {
		hashKey = k;
	}
	
	public int getHashEnforcement() {
		return hashEnforcementLevel;
	}
	
	public void setHashEnforcement(int l) {
		hashEnforcementLevel = l;
	}
	
	public void overrideDocumentationResponse(String or) {
		overrideResponse = or;
	}
	
	public String getOverrideDocumentationResponse() {
		return overrideResponse;
	}
	
	public void setSinceVersion(VersionInt v) {
		sinceVersion = v;
	}
	
	public VersionInt getSinceVersion() {
		return sinceVersion;
	}
	
	public void setOrder(int o) {
		order = o;
	}
	
	public int getOrder() {
		return order;
	}
	
	public List<APICallParam> getAPIParameters() {
		return apiParams;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setData(String name, Object o) {
		if (name == null)
			return;
		
		if (o == null)
			data.remove(name);
		else
			data.put(name, o);
	}
	
	public Object getData(String name) {
		if (name == null)
			return null;
		
		return data.get(name);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setDescription(String d) {
		description = d;
	}

	public MethodDefinition authenticationMethod(AuthenticationInt b) {
		authMethod = b;
		return this;
	}
	
	public List<AuthorizationInt> getAuthorizationMethods() {
		return authorizationMethods;
	}
	
	public MethodDefinition addAuthorizationMethod(AuthorizationInt a) {
		authorizationMethods.add(a);
		return this;
	}

	public MethodDefinition enableGET(boolean b) {
		methodGET = b;
		return this;
	}
	
	public MethodDefinition enablePOST(boolean b) {
		methodPOST = b;
		return this;
	}
	
	public MethodDefinition enablePUT(boolean b) {
		methodPUT = b;
		return this;
	}
	
	public MethodDefinition enableDELETE(boolean b) {
		methodDELETE = b;
		return this;
	}
	
	public String getDescription() { 
		return description;
	}

	public boolean isAuthenticationRequired() {
		return authMethod != null;
	}
	
	public AuthenticationInt<?> getAuthenticationMethod() {
		return authMethod;
	}
	
	public boolean isAuthorizationRequired() {
		return authorizationMethods.size() > 0;
	}
	
	private UStackPermissionEnum authenticationGroup;
	
	public void authenticationGroup(UStackPermissionEnum ag) {
		authenticationGroup = ag;
	}
	
	public UStackPermissionEnum getAuthenticationGroup() {
		return authenticationGroup;
	}
	
	public boolean isMethodGET() {
		return methodGET;
	}

	public boolean isMethodPOST() {
		return methodPOST;
	}

	public boolean isMethodPUT() {
		return methodPUT;
	}

	public boolean isMethodDELETE() {
		return methodDELETE;
	}
	
	public boolean isMethodEnabled(HttpMethod method) {
		
		if (method == HttpMethod.DELETE && !methodDELETE)
			return false;
		else if (method == HttpMethod.PUT && !methodPUT)
			return false;
		else if (method == HttpMethod.POST && !methodPOST)
			return false;
		else if (method == HttpMethod.GET && !methodGET)
			return false;
		
		return true;
	}

	public void addRequiredParam(Validated val)
	{
		paramVal.add(val);
	}
	
	public void addRequiredParam(ParameterDefinitionInt val)
	{
		paramVal.add(val);
	}
	
	public void addVariesParam(ParameterDefinitionInt val)
	{
		variesParams.add(val);
	}
	
	public void addVariesParams(List<ParameterDefinitionInt<?>> vals)
	{
		variesParams.addAll(vals);
	}
	
	public List<ParameterDefinitionInt<?>> getVariesParams()
	{
		return variesParams;
	}
	
	@SuppressWarnings("unchecked")
	public void validateCall(CallParameters callParams) throws APIException
	{
		for (Object val : paramVal)
		{
			if (val instanceof APICallParam)
			{
				APICallParam param = getParamter(((APICallParam)val).getParamDetails());
				if (callParams.get(param.getParamDetails()) == null)
					throw new BadRequestException(String.format("%s is a required parameter", param.getParamDetails().getName()));
				
				param.validate(callParams);
			}
			else if (val instanceof ParameterDefinitionInt)
			{
				APICallParam param = getParamter((ParameterDefinitionInt)val);
				if (callParams.get(param.getParamDetails()) == null)
					throw new BadRequestException(String.format("%s is a required parameter", param.getParamDetails().getName()));
				
				param.validate(callParams);
			}
			else if (val instanceof Validated)	
				((Validated)val).validate(callParams);
			else
				logger.warn("Unknown Parameter Validation Type: " + val);
				
		}
		
		// Setup Default Values
		for (APICallParam param : apiParams)
		{
			if (param.defaultValue != null &&
				callParams.getParameter(param.getParamDetails().getName()) == null)
			{
				callParams.setParameterValue(param.getParamDetails().getName(), param.defaultValue);
			}
		}
		
	}
	
	@SuppressWarnings({ "unchecked" })
	public void handleCall(Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		Object apiInt = objectInstances.get(apiClass);
		Method m = methodInstances.get(methodName);
		if (m == null)
		{
			if (apiInt == null)
			{
				apiInt = apiClass.newInstance();
				objectInstances.put(apiClass, apiInt);
			}
			
			Class[] partypes = new Class[]{ MethodDefinition.class, Channel.class, HttpRequest.class, CallParameters.class };
			m = apiClass.getMethod(methodName, partypes);
			methodInstances.put(methodName, m);
		}
		
		Object[] arglist = new Object[]{ this, channel, req, callParams };
		channel.setAttachment( new Long(System.currentTimeMillis()) );
		m.invoke(apiInt, arglist);
	}
	
	public APICallParam getParamter(ParameterDefinitionInt param) {
		
		for (APICallParam call : apiParams)
			if (call.getParamDetails().equals(param))
				return call;

		return null;
		
	}
	
	public void addParameter(APICallParam param) {
		apiParams.add(param);
	}

	public void addParameter(APICallParam param, boolean required) {
		apiParams.add(param);
		if (required)
			addRequiredParam(param);
	}
	
//	public void addParameter(String name, String description, boolean required) {
//		addParameter(name, description, required, null);
//	}
//	
//	public void addParameter(String name, String description, boolean required, String since) {
//		
//		APICallParam p = new APICallParam();
//		p.name = name;
//		p.description = description;
//		p.req = required;
//		if (since == null)
//			p.since = sinceVersion;
//		else
//			p.since = since;
//		apiParams.add(p);
//		
//	}
	
	private String extraInfo;
	public void setExtraInfo(String extra) {
		extraInfo = extra;
	}
	
	public String getExtraInfo() {
		return extraInfo;
	}

	private String sampleHttpMethod;
	private String sampleCall;
	public void setSampleCall(String httpMethod, String call) {
		sampleHttpMethod = httpMethod;
		sampleCall = call;
	}
	
	private String sampleResponse;
	public void setSampleResponse(String r) {
		sampleResponse = r;
	}
	
	public String getSampleHttpMethod() {
		return sampleHttpMethod;
	}

	public String getSampleCall() {
		return sampleCall;
	}

	public String getSampleResponse() {
		return sampleResponse;
	}
}
