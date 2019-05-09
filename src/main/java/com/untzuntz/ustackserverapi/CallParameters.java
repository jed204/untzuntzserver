package com.untzuntz.ustackserverapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BasicBSONObject;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.untzuntz.ustackserverapi.auth.AuthenticationInt;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;
import com.untzuntz.ustackserverapi.util.UploadedFile;

public class CallParameters {

    static Logger           		logger               	= Logger.getLogger(CallParameters.class);

	private QueryStringDecoder qsd;
	private String path;
	private String postData;
	private String remoteIpAddress;
	private String remoteIpCountry;
	private List<UploadedFile> uploadedFiles;
	private Long tokenTTL;
	
	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	public void setTokenTTL(Long ttl) {
		tokenTTL = ttl;
	}
	
	public Long getTokenTTL() {
		return tokenTTL;
	}
	
	private Map<String,List<String>> qsdMap;
	public CallParameters(String uri) {

		qsd = new QueryStringDecoder(uri);
		if (uri.indexOf("?") > -1)
			path = uri.substring(0, uri.indexOf("?"));
		else
			path = uri;
		
		Map<String,List<String>> map = qsd.getParameters();
		if (map == null)
			qsdMap = new HashMap<String,List<String>>();
		else
			qsdMap = new HashMap<String,List<String>>(map);
		
		if (qsdMap.get("x_object") != null)
		{
			try {
				BasicBSONObject bs = (BasicBSONObject)JSON.parse(getParameter("x_object"));
				if (bs != null)
				{
					Iterator<String> it = bs.keySet().iterator();
					while (it.hasNext())
					{
						String key = it.next();
						setParameterValue(key, bs.get(key) + "");
					}
				}
			} catch (Exception jpe) {}
		}
	}
	
	public List<UploadedFile> getUploadedFiles() {
		return uploadedFiles;
	}
	
	public void setUploadedFiles(List<UploadedFile> files) {
		uploadedFiles = files;
	}
	
	public String getRemoteIpAddress() {
		return remoteIpAddress;
	}

	public String getRemoteCountry() {
		return remoteIpCountry;
	}
	
	public void setRemoteCountry(String remoteIpCountry) {
		this.remoteIpCountry = remoteIpCountry;
	}
	
	public void setRemoteIpAddress(String remoteIpAddress) {
		if (StringUtils.isNotEmpty(remoteIpAddress) && remoteIpAddress.indexOf(",") > -1) {
			// user is proxied. Take the left most or 'first' ip address in the list as their remote ip
			String[] ips = remoteIpAddress.split(",");
			remoteIpAddress = ips[0].trim();
		}
		this.remoteIpAddress = remoteIpAddress;
	}

	public void setParameterValue(String param, String val)
	{
		if (param == null)
			return;
		
		if (val == null)
		{
			qsdMap.remove(param);
			return;
		}
		
		List<String> valList = new ArrayList<String>();
		valList.add(val);
		qsdMap.put(param, valList);
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
        //String psig = percentEncodeRfc3986(sig);

		logger.info(String.format("Full Request to Encode: %s => Signature: %s", canonicalQS, sig));

		return sig;
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
			buffer.append(kvpair.getKey());
			buffer.append("=");
			buffer.append(kvpair.getValue());
//            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
//            buffer.append("=");
//            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
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
    public static String percentEncodeRfc3986(String s) {
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
    	
    	if (key == null)
    		return null;
    	
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

    public String getQueryString() {

		Map<String,List<String>> paramsAsArray = getParameters();

		StringBuffer paramStr = new StringBuffer();

		Iterator var3 = paramsAsArray.keySet().iterator();

		while(var3.hasNext()) {
			Map.Entry<String, String[]> param = (Map.Entry)var3.next();
			String[] vals = new String[1];
			vals[0] = param.getValue().toString();
			paramStr.append(String.format("%s=%s", param.getKey(), vals[0]));
		}

		return paramStr.toString();
	}

    public Map<String,List<String>> getParameters() {
    	return qsd.getParameters();
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

    public <T> boolean has(ParameterDefinitionInt<T> param)
    {
    	return param.hasValue( getParameter(param.getName()) );
    }
    
    public <T> T get(ParameterDefinitionInt<T> param)
    {
    	return param.getValue( getParameter(param.getName()) );
	}
	
	public String getParameter(String name) {
		
		List<String> val = qsdMap.get(name);
		if (val == null || val.size() == 0)
			return null;
		
		return val.get(0);
	}
	
	private Object authObject;
	
	public void setAuthInfo(Object o) {
		authObject = o;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T getAuthInfo(AuthenticationInt<T> authType)
    {
    	return (T)authObject;
	}

}
