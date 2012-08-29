package com.untzuntz.ustackserverapi;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

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
	private boolean authenticationRequiredFlag;
	private Class apiClass;
	private HashMap<String,Object> data;
	private String methodName;
	private String description;
	private String sinceVersion;
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
		this.apiParams = new Vector<APICallParam>();
		this.data = new HashMap<String,Object>();
		this.objectInstances = new HashMap<Class,Object>();
		this.methodInstances = new HashMap<String,Method>();
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
	
	public void setSinceVersion(String v) {
		sinceVersion = v;
	}
	
	public String getSinceVersion() {
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

	public MethodDefinition authRequired(boolean b) {
		authenticationRequiredFlag = b;
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
		return authenticationRequiredFlag;
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
	
	@SuppressWarnings({ "unchecked" })
	public void handleCall(Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
//		logger.info("Request URI : " + req.getUri());
		for (APICallParam param : apiParams) {
			
//			logger.info("Checking Parameter [" + param.name + "] => Required: " + param.req);
			if (param.req && callParams.getParameter(param.name) == null)
			{
				logger.warn("\tRequired Parameter - MISSING => " + param.name);
				throw new InvalidAPIRequestException();
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
	
	public void addParameter(String name, String description, boolean required) {
		addParameter(name, description, required, null);
	}
	
	public void addParameter(String name, String description, boolean required, String since) {
		
		APICallParam p = new APICallParam();
		p.name = name;
		p.description = description;
		p.req = required;
		if (since == null)
			p.since = sinceVersion;
		else
			p.since = since;
		apiParams.add(p);
		
	}
	
	private String extraInfo;
	public void setExtraInfo(String extra) {
		extraInfo = extra;
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
	
	public class APICallParam {
		
		public String name;
		public String description;
		public boolean req;
		public String since;
		
	}
}
