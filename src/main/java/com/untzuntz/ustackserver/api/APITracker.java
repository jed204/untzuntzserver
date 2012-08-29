package com.untzuntz.ustackserver.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.untzuntz.ustack.data.APILog;
import com.untzuntz.ustackserverapi.CallParameters;

public class APITracker {

	public static APILog create(CallParameters params, Channel chl, HttpRequest req, HttpResponse res, String responseBody)
	{
		APILog ret = new APILog();
		
		// add method
		ret.put("method", req.getMethod());
		ret.put("uri", params.getPath());

		// IP Data
		String realSourceIP = req.getHeader("X-Real-IP");
		if (realSourceIP == null)
			realSourceIP = chl.getRemoteAddress().toString();
		String countryCode = req.getHeader("X-Country-Code");

		ret.put("sourceIP", realSourceIP);
		ret.put("sourceCountry", countryCode);
		
		// request headers
		List<Map.Entry<String, String>> reqHeaders = req.getHeaders();
		BasicDBList headerList = new BasicDBList();
		for (Map.Entry<String, String> entry : reqHeaders)
			headerList.add(new BasicDBObject( entry.getKey(), entry.getValue() ));
		ret.put("requestHeaders", headerList);

		// request parameters
		ret.put("requestParams", params.getParameterList());
		
		// response code
		ret.put("responseCode", res.getStatus().getCode());
		
		// response headers
		Iterator<String> headers = res.getHeaderNames().iterator();
		headerList = new BasicDBList();
		while (headers.hasNext()) {
			String hdr = headers.next();
			headerList.add(new BasicDBObject( hdr, res.getHeader(hdr) ));
		}
		ret.put("responseHeaders", headerList);
		
		// response body
		ret.put("responseBody", responseBody);
		
		ret.save("API Tracker");

		return ret;
	}

}
