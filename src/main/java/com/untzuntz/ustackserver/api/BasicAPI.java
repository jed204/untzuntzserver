package com.untzuntz.ustackserver.api;

import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.bson.BasicBSONObject;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.stream.ChunkedStream;
import org.jboss.netty.util.CharsetUtil;
import org.w3c.dom.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustack.data.APIMapping;
import com.untzuntz.ustack.data.UniqueReference;
import com.untzuntz.ustack.data.UserAccount;
import com.untzuntz.ustack.main.UForgotPasswordSvc;
import com.untzuntz.ustackserverapi.APIDocumentation;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class BasicAPI {

    static Logger           		logger               	= Logger.getLogger(BasicAPI.class);
    
    public void documentationCss(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
    	HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);

        // Write the initial line and the header.
    	channel.write(response);

        // Write the content.
        ChannelFuture writeFuture = channel.write(new ChunkedStream(ClassLoader.class.getResourceAsStream("/com/untzuntz/ustackserver/docs/apidocs.css")));
        writeFuture.addListener(ChannelFutureListener.CLOSE);
    }
    
    public void documentation(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
    	generateDocumentation(def, channel, req, callParams, null);
    }
    
    public static void generateDocumentation(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams, HashMap<String,String> documentationDefaults) throws Exception
    {
    	TransformerFactory tFactory = TransformerFactory.newInstance();
	    
    	String docHtmlResourcePath = (String)def.getData("documentationHtmlResourcePath");
    	if (docHtmlResourcePath == null)
    		docHtmlResourcePath = "/com/untzuntz/ustackserver/docs/documentation.html";
    	
	    InputStream input = null;
	    try {
	    	input = ClassLoader.class.getResourceAsStream(docHtmlResourcePath);
		    InputStreamReader xslReader = new InputStreamReader( input );
		    
		    String clientId = callParams.get(ParamNames.client_id);
		    String apiKey = callParams.get(ParamNames.api_key);
		    
		    Document doc = APIDocumentation.getDocumentationXML((String)def.getData("baseUrl"), "curl", clientId, apiKey, 12, documentationDefaults);
		    DOMSource xml = new DOMSource(doc);
		    StreamSource xsl = new StreamSource(xslReader) ;
		    Transformer transformer = tFactory.newTransformer(xsl);
		    transformer.clearParameters();
		    
	    	StringWriter strWriter = new StringWriter();
		    StreamResult result = new StreamResult( strWriter );
		    transformer.transform(xml, result);
	    	
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
			res.setHeader("Content-type", "text/html");
			res.setContent(ChannelBuffers.copiedBuffer(strWriter.toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
			channel.write(res).addListener(ChannelFutureListener.CLOSE);
	    } finally {
	    	if (input != null)
	    		try { input.close(); } catch (Exception e) {}
	    }
    }
    
    public void hashtest(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
    	String serverSig = callParams.getRequestSignature(def.getHashKey());
    	
    	DBObject resp = new BasicDBObject("serverSignature", serverSig);
    	String clientSig = "(NOT PROVIDED)";  
    	if (callParams.get(ParamNames.RequestSignature) != null)
    		clientSig = callParams.get(ParamNames.RequestSignature);
    	
    	resp.put("clientSignature", clientSig);
    	resp.put("signatureStatus", "Error");
		if (clientSig.equals(serverSig))
	    	resp.put("signatureStatus", "Match");
			
		APIResponse.httpOk(channel, resp);
    }

    public void confirmEmailAddress(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
    {
		UniqueReference ref = UniqueReference.getByUID( callParams.get(ParamNames.UID) );
		if (ref != null)
		{
			logger.info("Loaded unique reference from uid : " + callParams.get(ParamNames.UID));
			
			ref.put("used", "true");
			UniqueReference.save(ref, ref.getString("userName"));
			
			UserAccount user = UserAccount.getUser(ref.getString("userName"));
			if (user != null)
			{
				user.put("emailConfirmed", "true");
				user.save(user.getUserName());
			}
			
			if (ref.get("successUrl") != null)
			{
				HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
				res.setHeader("Location", (String)ref.get("successUrl"));
				setContentLength(res, res.getContent().readableBytes());
				channel.write(res).addListener(ChannelFutureListener.CLOSE);
			}
			else
	    		APIResponse.httpOk(channel, APIResponse.success(""));
			
			return;
		}
		APIResponse.httpError(channel, APIResponse.error("Error during email confirmation process"));
    }
    
	public void forgotPassword(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		UserAccount usrAcct = UserAccount.getUser(callParams.get(ParamNames.username));
		if (usrAcct != null && !usrAcct.isDisabled())
		{
			try {
				UForgotPasswordSvc.sendForgotPassword(usrAcct.getUserName(), getClientIPObject(channel, req), (String)def.getData("url"), usrAcct.getUserName(), usrAcct.getPrimaryEmail(), usrAcct.getFullName(), (String)def.getData("template"));
			} catch (Exception e) {
	    		logger.warn("User [" + usrAcct.getUserName() + "] hit an exception during forgot password", e);
	    		APIResponse.httpError(channel, APIResponse.error("Error during forgot password request"));
	    		return;
			}
    		APIResponse.httpOk(channel, APIResponse.success(""));
		}
		else
    		APIResponse.httpError(channel, APIResponse.error("Error during forgot password request"));

	}
	
	public void resetPassword(MethodDefinition def, Channel channel, HttpRequest req, CallParameters callParams) throws Exception
	{
		UserAccount usrAcct = UserAccount.getUser(callParams.get(ParamNames.username)); 
		if (usrAcct != null && !usrAcct.isDisabled())
		{ 
			try {
				UForgotPasswordSvc.sendResetPassword(usrAcct.getUserName(), getClientIPObject(channel, req), usrAcct.getUserName(), usrAcct.getPrimaryEmail(), usrAcct.getFullName(), (String)def.getData("template"), null);
			} catch (Exception e) {
	    		logger.warn("User [" + usrAcct.getUserName() + "] hit an exception during forgot password", e);
	    		APIResponse.httpError(channel, APIResponse.error("Error during reset password request"));
	    		return;
			}
    		APIResponse.httpOk(channel, APIResponse.success(""));
		}
		else
    		APIResponse.httpError(channel, APIResponse.error("Error during reset password request"));

	}
	
	public static DBObject getClientIPObject(Channel channel, HttpRequest req)
	{
    	String clientIP = req.getHeader("X-Real-IP");
		if (clientIP == null)
			clientIP = req.getHeader("X-Forwarded-For");
		if (clientIP == null)
			clientIP = channel.getRemoteAddress().toString();
		String clientCountry = req.getHeader("X-Country-Code");
		if (clientCountry == null)
			clientCountry = "UNK";
		
		logger.info("IP: " + clientIP + " -> " + clientCountry);

		DBObject ret = new BasicDBObject();
		ret.put("requestIP", clientIP);
		ret.put("requestCountry", clientCountry);
		return ret;		
	}


}
