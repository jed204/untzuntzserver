package com.untzuntz.ustackserverapi;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class APIResponse {

    static Logger           		logger               	= Logger.getLogger(APIResponse.class);

    public static String AccessControlAllowOrigin;
	public static final String ContentTypeTextXML = "text/xml";
	public static final String ContentTypeTextHTML = "text/html";
	public static final String ContentTypeTextPlain = "text/plain";
	public static final String ContentTypeJSON = "application/json";
	
	private static void addHeaders(Channel channel, HttpResponse res)
	{
		if (AccessControlAllowOrigin != null)	
			res.setHeader("Access-Control-Allow-Origin", AccessControlAllowOrigin);
		
		if (channel.getAttachment() instanceof Long)
			res.setHeader("X-Processing-Time", System.currentTimeMillis() - (Long)channel.getAttachment());
	}

	public static void httpOk(Channel channel, String text, String contentType)
	{
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, res);
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}
	
	public static void httpResponse(Channel channel, String text, String contentType, HttpResponseStatus status)
	{
		logger.warn("Returning API Response [" + channel.getRemoteAddress() + "] => " + text);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, res);
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpError(Channel channel, String text, String contentType)
	{
		httpError(channel, text, contentType, HttpResponseStatus.BAD_REQUEST);
	}
	
	public static void httpError(Channel channel, String text, String contentType, HttpResponseStatus status)
	{
		logger.warn("Returning API Error [" + channel.getRemoteAddress() + "] => " + text);
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1, status);
		res.setHeader("Content-type", contentType);
		addHeaders(channel, res);
		
		res.setContent(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
		setContentLength(res, res.getContent().readableBytes());
		
		channel.write(res).addListener(ChannelFutureListener.CLOSE);
	}

	public static void httpOk(Channel channel, DBObject dbObject)
	{
		httpOk(channel, JSON.serialize(dbObject), ContentTypeJSON);
	}
	
	public static void httpResponse(Channel channel, DBObject dbObject, HttpResponseStatus status)
	{
		httpResponse(channel, JSON.serialize(dbObject), ContentTypeJSON, status);
	}
	
	public static void httpError(Channel channel, DBObject dbObject)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON);
	}

	public static void httpError(Channel channel, DBObject dbObject, HttpResponseStatus status)
	{
		httpError(channel, JSON.serialize(dbObject), ContentTypeJSON, status);
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
