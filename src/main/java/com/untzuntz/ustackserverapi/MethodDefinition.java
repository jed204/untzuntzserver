package com.untzuntz.ustackserverapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.untzuntz.ustackserverapi.auth.AuthenticationInt;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.ParamInt;
import com.untzuntz.ustackserverapi.params.Validated;
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
	private AuthenticationInt authMethod;
	private Class apiClass;
	private HashMap<String,Object> data;
	private String methodName;
	private String description;
	private VersionInt sinceVersion;
	private List<Object> paramVal;
	private int order;
	private String hashKey;
	private int hashEnforcementLevel;
	private List<APICallParam> apiParams;
	private HashMap<Class,Object> objectInstances;
	private HashMap<String,Method> methodInstances;

	public MethodDefinition(String path, Class apiClass, String methodName) {
		this.path = path;
		this.apiClass = apiClass;
		this.methodName = methodName;
		this.apiParams = new ArrayList<APICallParam>();
		this.data = new HashMap<String,Object>();
		this.objectInstances = new HashMap<Class,Object>();
		this.methodInstances = new HashMap<String,Method>();
		this.paramVal = new ArrayList<Object>();
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

	public MethodDefinition authMethod(AuthenticationInt b) {
		authMethod = b;
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
	
	public AuthenticationInt getAuthMethod() {
		return authMethod;
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
	
	public void addRequiredParam(ParamInt val)
	{
		paramVal.add(val);
	}
	
	@SuppressWarnings({ "unchecked" })
	public void handleCall(Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		
		for (Object val : paramVal)
		{
			if (val instanceof Validated)	
				((Validated)val).validate(callParams);
			else if (val instanceof ParamInt)
				getParamter((ParamInt)val).validate(callParams);
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
		m.invoke(apiInt, arglist);
	}
	
	public APICallParam getParamter(ParamInt param) {
		
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
