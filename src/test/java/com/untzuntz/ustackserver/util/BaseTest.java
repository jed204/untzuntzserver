package com.untzuntz.ustackserver.util;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.BeforeClass;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.InvalidAPIRequestException;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.TestHttpChannel;
import com.untzuntz.ustackserverapi.auth.AuthorizationInt;

public class BaseTest {

	static 		Logger           	logger                  = Logger.getLogger(BaseTest.class);

	protected static final HttpResponse BAD_REQ = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

	protected static TestHttpChannel channel;
	protected static HttpRequest req;
	protected String responseString;
	protected BasicDBObject responseObject;
	protected BasicDBObject apiResult;

	public static final SimpleDateFormat testDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void assertDateEquals(Date expected, Date date)
	{
		assertEquals(testDateTime.format(expected), testDateTime.format(date));
	}

	@BeforeClass public static void setUp() {
		
		System.setProperty("TestCase", "true");
		System.setProperty("MongoDB.DefaultHost", "localhost:27017");
		System.setProperty(UAppCfg.CURRENT_HOSTANME, "TestCase");
		
		req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "");
		channel = new TestHttpChannel();
	}
	
	protected void handleCall(String path, TestCallParams params) throws Exception
	{
		channel.write(null); // reset the test channel
		
		StringBuffer fullQuery = new StringBuffer();
		fullQuery.append(path);
		
		if (params != null)
			fullQuery.append("?").append(params.getQueryString());
		
		MethodDefinition def = APICalls.getCallByURI(path);
		if (def == null)
			throw new Exception("Invalid Path => " + path);
		
		CallParameters cp = new CallParameters(fullQuery.toString());
		cp.setRemoteIpAddress("127.0.0.1");
		
		if (def.isAuthenticationRequired())
			cp.setAuthInfo(def.getAuthenticationMethod().authenticate(def, null, cp));
		
		def.validateCall(cp);
		
		if (def.isAuthorizationRequired())
		{
			List<AuthorizationInt> auths = def.getAuthorizationMethods();
			for (AuthorizationInt auth : auths)	
				auth.authorize(def, cp);
		}

		String resp = null;
		try {
			def.handleCall(channel, req, cp);
		} catch (InvalidAPIRequestException e) {
			channel.write(BAD_REQ); // simulate a general API failure
		} catch (Exception iar) {
			logger.warn("Error from API", iar);
			channel.write(BAD_REQ); // simulate a general API failure
			if (iar.getCause() != null && iar.getCause() instanceof APIException)
				resp = APIResponse.error(((APIException)iar.getCause()).toDBObject()).toString();
//			else
//			{
//				channel.write(BAD_REQ); // simulate a general API failure
//			}
		}
		
		if (resp == null)
			resp = channel.getResponseString();
		if (resp.startsWith("{"))
		{
			responseString = null;
			
			responseObject = new BasicDBObject();
			responseObject.putAll((DBObject)JSON.parse(resp));
			
			// check for response status
			apiResult = new BasicDBObject();
			apiResult.putAll((DBObject)responseObject.get("apiResult"));
			logger.info("\n\t=>  Request: " + fullQuery.toString() + "\n\t=> Response: [" + channel.getResponseCode() + "] Status [" + apiResult.get("status") + "] | Message [" + apiResult.get("message") + "]\n\t=> Full: " + resp);
		}
		else
		{
			responseString = resp;
			apiResult = null;
			responseObject = null;
			logger.info("\n\t=>  Request: " + fullQuery.toString() + "\n\t=> Response: " + channel.getResponseCode());
		}
		
		
	}


}
