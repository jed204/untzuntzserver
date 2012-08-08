package com.untzuntz.ustackserverapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class CallParameters {

    static Logger           		logger               	= Logger.getLogger(CallParameters.class);

	private QueryStringDecoder qsd;
	private String userName;
	private DBObject user;
	private String path;
	
	public void setUser(DBObject u) { 
		user = u;
	}
	
	public DBObject getUser() {
		return user;
	}
	
	public CallParameters(String uri) {
		qsd = new QueryStringDecoder(uri);
		if (uri.indexOf("?") > -1)
			path = uri.substring(0, uri.indexOf("?"));
		else
			path = uri;
		
		if (userName == null)
			userName = getParameter("username");
		if (userName == null)
			userName = getParameter("userName");
	}
	
	public String getPath() {
		return qsd.getPath();
	}
	
    /**
     * All strings are handled as UTF-8
     */
    private static final String UTF8_CHARSET = "UTF-8";
    
    /**
     * The HMAC algorithm required by Amazon
     */
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	
    private static final Hashtable<String,Mac> keys = new Hashtable<String,Mac>();
    
	public String getRequestSignature(String key)
	{
        SortedMap<String, String> sortedParamMap = new TreeMap<String, String>();
        
		Map<String,List<String>> params = qsd.getParameters();
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext())
		{
			String param = it.next();
			if (!"sig".equalsIgnoreCase(param))
			{
				List<String> val = params.get(param);
				String valStr = "";
				if (val.size() > 0)
					valStr = val.get(0);
				
				//logger.info("\t" + param + " => " + valStr);
				
				sortedParamMap.put(param, valStr);
			}
		}
        
        // get the canonical form the query string
        String canonicalQS = path + "?" + canonicalize(sortedParamMap);
        
        String sig = hmac(key, canonicalQS);
        String psig = percentEncodeRfc3986(sig);

		logger.info("Full Request to Encode: " + canonicalQS + " => Signature: " + psig);

		return psig;
	}
	
    /**
     * Canonicalize the query string.
     * 
     * @param sortedParamMap    Parameter name-value pairs in lexicographical order.
     * @return                  Canonical form of query string.
     */
    private String canonicalize(SortedMap<String, String> sortedParamMap) {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        String cannoical = buffer.toString();
        return cannoical;
    }

    /**
     * Percent-encode values according the RFC 3986. The built-in Java
     * URLEncoder does not encode according to the RFC, so we make the
     * extra replacements.
     * 
     * @param s decoded string
     * @return  encoded string per RFC 3986
     */
    private String percentEncodeRfc3986(String s) {
    	if (s == null)
    		return null;
    	
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }

    /**
     * Compute the HMAC.
     *  
     * @param stringToSign  String to compute the HMAC over.
     * @return              base64-encoded hmac value.
     */
    private String hmac(String key, String stringToSign) {
    	
		Mac mac = keys.get(key);
		if (mac == null)
		{
			try {
		        byte[] secretyKeyBytes = key.getBytes(UTF8_CHARSET);
		        SecretKeySpec secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
		        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		        mac.init(secretKeySpec);
		        keys.put(key, mac);
			} catch (Exception e) {
				return null;
			}
		}
    	
        String signature = null;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature;
    }
    
    public BasicDBList getParameterList() 
    {
    	BasicDBList ret = new BasicDBList();
    	Map<String,List<String>> params = qsd.getParameters();
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext())
		{
			String param = it.next();
			if (!"sig".equalsIgnoreCase(param))
			{
				List<String> val = params.get(param);
				String valStr = "";
				if (val.size() > 0)
					valStr = val.get(0);
				
				ret.add(new BasicDBObject(param, valStr));
			}
		}
		return ret;
    }
	
	public String getParameter(String name) {
		
		List<String> val = qsd.getParameters().get(name);
		if (val == null || val.size() == 0)
			return null;
		
		return val.get(0);
	}
	
	public String getUserName() {
		return userName;
	}
}
