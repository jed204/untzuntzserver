package com.untzuntz.ustackserver.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class TestCallParams
{
    SortedMap<String, String> sortedParamMap = new TreeMap<String, String>();

	
	public TestCallParams(String name, String val)
	{
		append(name, val);
	}
	
	public TestCallParams(ParameterDefinitionInt<?> name, String val)
	{
		append(name, val);
	}
	
	public TestCallParams append(String name, String val)
	{
		sortedParamMap.put(name, val);
		return this;
	}
	
	public TestCallParams append(ParameterDefinitionInt<?> name, String val)
	{
		sortedParamMap.put(name.getName(), val);
		return this;
	}
	
	public String getQueryString() throws UnsupportedEncodingException
	{
		StringBuffer q = new StringBuffer();
		
		Iterator<String> it = sortedParamMap.keySet().iterator();
		while (it.hasNext())
		{
			if (q.length() > 0)
				q.append("&");

			String k = it.next();
			String v = sortedParamMap.get(k);
			q.append(urlEncodePair(k, v));
		}
		
		return q.toString();
	}
	
	private String urlEncodePair(String k, String v) throws UnsupportedEncodingException {
		
		if (k == null)
			return "";
		if (v == null)
			v = "";

		return String.format("%s=%s", 
				URLEncoder.encode(k, "UTF-8"), 
				URLEncoder.encode(v, "UTF-8"));
	}

}
