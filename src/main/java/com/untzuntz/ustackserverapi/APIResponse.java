package com.untzuntz.ustackserverapi;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class APIResponse {

    static Logger           		logger               	= Logger.getLogger(APIResponse.class);

    public static String AccessControlAllowOrigin;
	public static final String ContentTypeTextXML = "text/xml";
	public static final String ContentTypeTextHTML = "text/html";
	public static final String ContentTypeTextPlain = "text/plain";
	public static final String ContentTypeJSON = "application/json";
	public static final String ContentTypeJSONP = "application/javascript";
	public static final String ContentTypeCalendar = "text/calendar";
	
	private static void addHeaders(Channel channel, HttpResponse res, String jsonpFunction)
	{
		if (AccessControlAllowOrigin != null)	
			res.setHeader("Access-Control-Allow-Origin", AccessControlAllowOrigin);
		if (jsonpFunction != null)
		{
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Content-type", ContentTypeJSONP);
		}
		
		if (channel.getAttachment() instanceof Long)
			res.setHeader("X-Processing-Time", System.currentTimeMillis() - (Long)channel.getAttachment());
	}
	
	public static void httpOk(Channel channel, String text, String contentType, CallParameters params, Cookie[] cookie)
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
		
		addHeaders(channel, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
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
		
		StringBuffer json = new StringBuffer();
		json.append(jsonpFunction);
		json.append("(").append(text).append(");");
		return json.toString();
	}
	
	public static void httpResponse(Channel channel, String text, String contentType, HttpResponseStatus status, CallParameters params)
	{
		String jsonpFunction = params.get(ParamNames.json_callback);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpError(Channel channel, String text, String contentType, CallParameters params)
	{
		httpError(channel, text, contentType, HttpResponseStatus.BAD_REQUEST, params);
	}
	
	public static void httpError(Channel channel, String text, String contentType, HttpResponseStatus status, CallParameters params)
	{
		String jsonpFunction = params.get(ParamNames.json_callback);
		logger.warn(String.format("Returning API Error [%d | %s] => %s", status.getCode(), channel.getRemoteAddress(), text));
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, res, jsonpFunction);
		if (jsonpFunction != null)
			text = handleJSONPResponse(res, jsonpFunction, text, params);
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpOk(Channel channel, DBObject dbObject, CallParameters params, Cookie[] cookies)
	{
		httpOk(channel, JSON.serialize(dbObject), ContentTypeJSON, params, cookies);
	}
	
	public static void httpOk(Channel channel, DBObject dbObject, CallParameters params)
	{
		httpOk(channel, dbObject, params, null);
	}
	
	public static void httpResponse(Channel channel, DBObject dbObject, HttpResponseStatus status, CallParameters params)
	{
		httpResponse(channel, JSON.serialize(dbObject), ContentTypeJSON, status, params);
	}
	
	public static void httpError(Channel channel, DBObject dbObject, CallParameters params)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON, params);
	}

	public static void httpError(Channel channel, DBObject dbObject, HttpResponseStatus status, CallParameters params)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON, status, params);
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
