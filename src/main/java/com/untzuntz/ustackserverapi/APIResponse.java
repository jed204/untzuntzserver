package com.untzuntz.ustackserverapi;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class APIResponse {

    static Logger           		logger               	= Logger.getLogger(APIResponse.class);

    private static final Map<String,Boolean> legalOrigins = new HashMap<String,Boolean>();
    public static void addCORSOrigin(String origin) {
    	legalOrigins.put(origin, Boolean.TRUE);
    }
    
    public static String AccessControlAllowOrigin;
	public static final String ContentTypeTextXML = "text/xml";
	public static final String ContentTypeTextHTML = "text/html";
	public static final String ContentTypeTextPlain = "text/plain";
	public static final String ContentTypeJSON = "application/json";
	public static final String ContentTypeJSONP = "application/javascript";
	public static final String ContentTypeCalendar = "text/calendar";
	
	private static void addHeaders(Channel channel, HttpRequest req, HttpResponse res, String jsonpFunction)
	{
		if (AccessControlAllowOrigin != null)	
			res.setHeader("Access-Control-Allow-Origin", AccessControlAllowOrigin);
		if (jsonpFunction != null)
		{
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Content-type", ContentTypeJSONP);
		}
		
		if (req != null)
		{
			String originHeader = req.getHeader("Origin");
			if (originHeader != null && legalOrigins.get(originHeader))
				res.setHeader("Access-Control-Allow-Origin", originHeader);
		}
		else
		{
			try {
				Integer v = null;
				v.intValue();
			} catch (Exception e) {
				logger.error("failed", e);
			}
		}
		
		if (channel.getAttachment() instanceof Long)
			res.setHeader("X-Processing-Time", System.currentTimeMillis() - (Long)channel.getAttachment());
	}
	
	public static void httpOk(Channel channel, String text, String contentType, CallParameters params, Cookie[] cookie, HttpRequest req, boolean enableCORS)
	{
		String jsonpFunction = params.get(ParamNames.json_callback);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
		res.setHeader("Content-type", contentType);
		
		if (cookie != null)
		{
			for (int i = 0; i < cookie.length; i++)
			{
				CookieEncoder cookieEncoder = new CookieEncoder(true);
				cookieEncoder.addCookie(cookie[i]);
				res.addHeader("Set-Cookie", cookieEncoder.encode());
			}
		}
		
		addHeaders(channel, req, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
		if (enableCORS)
		{
			String originHeader = req.getHeader("Origin");
			if (originHeader != null)
				res.setHeader("Access-Control-Allow-Origin", originHeader);
			if (req.getMethod().equals(HttpMethod.OPTIONS) && req.getHeader("Access-Control-Request-Headers") != null)
				res.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
		}
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Inject 'httpResponseCode' into the stream if it's 400 or greater
	 * 
	 * This will let the client process the data
	 * 
	 * @param res
	 * @param jsonpFunction
	 * @param text
	 * @return
	 */
	private static String handleJSONPResponse(HttpResponse res, String jsonpFunction, String text, CallParameters params)
	{
		if (!"false".equals(params.get(ParamNames.json_response_code_inject)) && res.getStatus().getCode() >= 400) // respond w/ a 200 and jam in the error response code so the client side can process
		{
			text = String.format("{ \"httpResponseCode\" : %d,%s", res.getStatus().getCode(), text.substring(1));
			res.setStatus(HttpResponseStatus.OK);
		}
		
		res.setHeader("Content-type", "text/javascript");
		
		StringBuffer json = new StringBuffer();
		json.append(jsonpFunction);
		json.append("(").append(text).append(");");
		return json.toString();
	}
	
	public static void httpResponse(Channel channel, String text, String contentType, HttpRequest req, HttpResponseStatus status, CallParameters params)
	{
		String jsonpFunction = params.get(ParamNames.json_callback);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, req, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpError(Channel channel, String text, String contentType, HttpRequest req, CallParameters params)
	{
		httpError(channel, text, contentType, req, HttpResponseStatus.BAD_REQUEST, params);
	}

	public static void httpError(Channel channel, String text, String contentType, HttpRequest req, HttpResponseStatus status, CallParameters params)
	{
		httpError(channel, text, contentType, HttpResponseStatus.BAD_REQUEST, params, req, false);
	}
	
	public static void httpError(Channel channel, String text, String contentType, HttpResponseStatus status, CallParameters params, HttpRequest req, boolean enableCORS)
	{
		String jsonpFunction = params.get(ParamNames.json_callback);
		logger.warn(String.format("Returning API Error [%d | %s] => %s", status.getCode(), channel.getRemoteAddress(), text));
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, req, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		
		if (enableCORS)
		{
			res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
			if (req.getMethod().equals(HttpMethod.OPTIONS))
				res.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers"));
		}

		
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpOk(Channel channel, DBObject dbObject, HttpRequest req, CallParameters params, Cookie[] cookies)
	{
		httpOk(channel, JSON.serialize(dbObject), ContentTypeJSON, params, cookies, req, false);
	}
	
	public static void httpOk(Channel channel, DBObject dbObject, HttpRequest req, CallParameters params)
	{
		httpOk(channel, dbObject, req, params, null);
	}
	
	public static void httpResponse(Channel channel, DBObject dbObject, HttpRequest req, HttpResponseStatus status, CallParameters params)
	{
		httpResponse(channel, JSON.serialize(dbObject), ContentTypeJSON, req, status, params);
	}
	
	public static void httpError(Channel channel, DBObject dbObject, HttpRequest req, CallParameters params)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON, req, params);
	}

	public static void httpError(Channel channel, DBObject dbObject, HttpRequest req, HttpResponseStatus status, CallParameters params)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON, req, status, params);
	}

	public static DBObject getResponseObject(String status)
	{
		return getResponseObject(status, (String)null);
	}
	
	public static DBObject getResponseObject(String status, String msg)
	{
		DBObject obj = new BasicDBObject();
		DBObject result = new BasicDBObject();
		obj.put("apiResult", result);
		result.put("status", status);
		if (msg != null)	
			result.put("message", msg);

		return obj;
	}
	
	public static DBObject getResponseObject(String status, DBObject ret)
	{
		DBObject obj = new BasicDBObject();
		DBObject result = new BasicDBObject();
		obj.put("apiResult", result);
		result.put("status", status);
		if (ret != null)	
			obj.putAll(ret);

		return obj;
	}
	
	public static DBObject success(DBObject ret) {
		return getResponseObject("SUCCESS", ret);
	}
	
	public static DBObject error(DBObject ret) {
		return getResponseObject("ERROR", ret);
	}
	
	public static DBObject success(String msg) {
		return getResponseObject("SUCCESS", msg);
	}
	
	public static DBObject success() {
		return getResponseObject("SUCCESS", (String)null);
	}
	
	public static DBObject error(String msg) {
		return getResponseObject("ERROR", msg);
	}
	

}
