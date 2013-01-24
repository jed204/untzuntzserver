package com.untzuntz.ustackserverapi;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.untzuntz.ustack.aaa.Authorization;
import com.untzuntz.ustackserverapi.auth.AuthorizationInt;
import com.untzuntz.ustackserverapi.params.APICallParam;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.params.Validated;
import com.untzuntz.ustackserverapi.params.types.ParameterDefinitionInt;

public class APIDocumentation {
	
    static Logger           		logger               	= Logger.getLogger(APIDocumentation.class);

	public static String SystemName;
	public static String logoUrl;
	public static String version;
	
	public static String getStringFromDoc(org.w3c.dom.Document doc) {
	    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
	    LSSerializer lsSerializer = domImplementation.createLSSerializer();
	    return lsSerializer.writeToString(doc);   
	}
	
	public static org.w3c.dom.Document getDocumentationXML(String baseUrl, String codeType, String client_id, String api_key, int methodsPerColumn, HashMap<String,String> documentationDefaults) throws ParserConfigurationException, UnsupportedEncodingException, InstantiationException, IllegalAccessException, JSONException
	{
		if (methodsPerColumn == -1)
			methodsPerColumn = 10;
		
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        
        org.w3c.dom.Element docs = doc.createElement("doc");
        doc.appendChild(docs);

        org.w3c.dom.Element systemName = doc.createElement("systemName");
        systemName.setTextContent(SystemName + " API Documentation");
        docs.appendChild(systemName);
        
        org.w3c.dom.Element logoUrlNode = doc.createElement("logoUrl");
        logoUrlNode.setTextContent(logoUrl);
        docs.appendChild(logoUrlNode);
        
        org.w3c.dom.Element base = doc.createElement("baseUrl");
        base.setTextContent(baseUrl);
        docs.appendChild(base);
        
        org.w3c.dom.Element ver = doc.createElement("version");
        ver.setTextContent(version);
        docs.appendChild(ver);
        
        org.w3c.dom.Element clientid = doc.createElement("client_id");
        clientid.setTextContent(client_id);
        docs.appendChild(clientid);
        
        org.w3c.dom.Element apikey = doc.createElement("api_key");
        apikey.setTextContent(api_key);
        docs.appendChild(apikey);
        
        org.w3c.dom.Element code = doc.createElement("codeType");
        code.setTextContent(codeType);
        docs.appendChild(code);

        org.w3c.dom.Element errorsXml = doc.createElement("errors");
        docs.appendChild(errorsXml);
        
        List<APIException> errors = APICalls.getErrors();
        for (APIException error : errors)
        {
            org.w3c.dom.Element errorXml = doc.createElement("error");
            errorsXml.appendChild(errorXml);
            
            addErrorInformation(doc, errorXml, error);
        }
        
        List<MethodDefinition> methods = APICalls.getMethods();
        
        Collections.sort(methods, new MethodOrder());

        org.w3c.dom.Element toc = doc.createElement("toc");
        docs.appendChild(toc);

        org.w3c.dom.Element tocCol = doc.createElement("N1");
        toc.appendChild(tocCol);

        org.w3c.dom.Element tocGroup = doc.createElement("group");
        tocCol.appendChild(tocGroup);
        
        HashMap<String,org.w3c.dom.Element> tocGroups = new HashMap<String,org.w3c.dom.Element>();
        
        int cnt = 3;
        int tocColIdx = 1;
        
        org.w3c.dom.Element methodsXml = doc.createElement("methods");
        docs.appendChild(methodsXml);
        
        for (MethodDefinition method : methods) {
        	
        	if (method.getOrder() >= 0)
        	{
        		if (method.getAuthenticationGroup() != null && !Authorization.authorizeAPIBool(client_id, method.getAuthenticationGroup()))
        			continue;
        		
	            org.w3c.dom.Element methodXml = doc.createElement("method");
	            methodsXml.appendChild(methodXml);
	         
	            org.w3c.dom.Element parentToc = tocGroup;
	            if (method.getDocumentationGroup() != null)
	            {
	            	parentToc = tocGroups.get(method.getDocumentationGroup());
	            	if (parentToc == null)
	            	{
	            		parentToc = doc.createElement("group");
	            		addNode(doc, parentToc, "name", method.getDocumentationGroup());
	                    tocCol.appendChild(parentToc);

		            	tocGroups.put(method.getDocumentationGroup(), parentToc);
	            	}
	            }
	            
	            org.w3c.dom.Element tocEntry = doc.createElement("entry");
        		addNode(doc, tocEntry, "name", method.getMethodName());
        		addNode(doc, tocEntry, "internalname", method.getMethodName().toLowerCase());
        		parentToc.appendChild(tocEntry);
	        	
	            addMethodInformation(doc, methodXml, baseUrl, client_id, api_key, method, documentationDefaults);
        	}
        	
        	cnt++;
        	if (cnt > methodsPerColumn)
        	{
        		cnt = 0;
        		tocColIdx++;
        		
        		tocCol = doc.createElement("N" + tocColIdx);
                toc.appendChild(tocCol);
                
                tocGroup = doc.createElement("group");
                tocCol.appendChild(tocGroup);
                
                tocGroups.clear();
        	}
        }        

        
        return doc;
	}
	
	private static void addErrorInformation(org.w3c.dom.Document doc, org.w3c.dom.Element parent, APIException error) throws JSONException
	{
		addNode(doc, parent, "name", error.getClass().getSimpleName());
		addNode(doc, parent, "description", ((APIExceptionDocumentation)error).getReason());
		
        org.w3c.dom.Element extras = doc.createElement("extras");
        parent.appendChild(extras);

		final Field[] fields = error.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			final Field f = fields[i];
			String fieldName = f.getName();
			if (!"serialVersionUID".equals(fieldName))
			{
	            org.w3c.dom.Element field = doc.createElement("field");
	            extras.appendChild(field);
	    		addNode(doc, field, "name", fieldName);
			}
		}
		
		try {
			logger.info("Outputting type: " + error.getClass().getSimpleName());
			JSONTokener tokener = new JSONTokener(error.toDBObject() + ""); //tokenize the ugly JSON string
			JSONObject finalResult = new JSONObject(tokener); // convert it to JSON object
			addNode(doc, parent, "responseExample", finalResult.toString(3));
		} catch (Exception e) {
			logger.error("failed to output example", e);
		}
	}
	
	private static void addMethodInformation(org.w3c.dom.Document doc, org.w3c.dom.Element parent, String baseUrl, String client_id, String api_key,  MethodDefinition def, HashMap<String,String> exampleParameterValues) throws UnsupportedEncodingException {
		
		addNode(doc, parent, "name", def.getMethodName());
		addNode(doc, parent, "internalname", def.getMethodName().toLowerCase());
		
		addNode(doc, parent, "description", def.getDescription());
		addNode(doc, parent, "returns", "returns");
		String xMethod = "-X GET";
		StringBuffer methods = new StringBuffer();
		if (def.isMethodGET())
			methods.append("GET ");
		if (def.isMethodPUT())
		{
			methods.append("PUT ");
			xMethod = "-X PUT";
		}
		if (def.isMethodPOST())
		{
			methods.append("POST ");
			xMethod = "-X POST";
		}
		if (def.isMethodDELETE())
		{
			methods.append("DELETE ");
			xMethod = "-X DELETE";
		}
		
		List<String> reqParams = new ArrayList<String>();
		
		addNode(doc, parent, "httpMethod", methods.toString());
		addNode(doc, parent, "httpURL", baseUrl + def.getPath());

        org.w3c.dom.Element arguments = doc.createElement("arguments");
        parent.appendChild(arguments);

        if (def.getAuthenticationMethod() != null)
        {
	        List<ParameterDefinitionInt<?>> secArgs = def.getAuthenticationMethod().getAuthenticationParameters();
	        for (ParameterDefinitionInt<?> arg : secArgs) 
	        {
	            org.w3c.dom.Element argument = doc.createElement("argument");
	            arguments.appendChild(argument);
	            
	    		addNode(doc, argument, "name", arg.getName());
	    		addNode(doc, argument, "description", arg.getDescription());
	    		addNode(doc, argument, "optionalRequired", "required");
				addNode(doc, argument, "type", "Type: " + arg.getTypeDescription());
				
				reqParams.add(arg.getName() + "=" + getExampleParamValue(def.getMethodName(), arg.getName(), exampleParameterValues));
	        }
	        
	        for (AuthorizationInt ai : def.getAuthorizationMethods())
	        {
		        List<ParameterDefinitionInt<?>> params = ai.getAuthenticationParameters();
		        for (ParameterDefinitionInt<?> arg : params) 
		        {
		            org.w3c.dom.Element argument = doc.createElement("argument");
		            arguments.appendChild(argument);
		            
		    		addNode(doc, argument, "name", arg.getName());
		    		addNode(doc, argument, "description", arg.getDescription());
		    		addNode(doc, argument, "optionalRequired", "required");
					addNode(doc, argument, "type", "Type: " + arg.getTypeDescription());
					
					reqParams.add(arg.getName() + "=" + getExampleParamValue(def.getMethodName(), arg.getName(), exampleParameterValues));
		        }
	        }
        }
        
        List<APICallParam> args = def.getAPIParameters();
        
        if (!def.isClientVerCheckDisabled())
        {
            org.w3c.dom.Element argument = doc.createElement("argument");
            arguments.appendChild(argument);
            
    		addNode(doc, argument, "name", ParamNames.client_ver.getName());
    		addNode(doc, argument, "description", ParamNames.client_ver.getDescription());
			addNode(doc, argument, "type", "Type: " + ParamNames.client_ver.getTypeDescription());
			addNode(doc, argument, "optionalRequired", "required");
			reqParams.add(ParamNames.client_ver.getName() + "=" + getExampleParamValue(def.getMethodName(), ParamNames.client_ver.getName(), exampleParameterValues));
        }

        for (APICallParam arg : args) 
        {
        	boolean required = isParamRequired(def, arg.getParamDetails().getName());
        	
            org.w3c.dom.Element argument = doc.createElement("argument");
            arguments.appendChild(argument);
            
    		addNode(doc, argument, "name", arg.getParamDetails().getName());
    		addNode(doc, argument, "description", arg.getDescription());

    		if (required)
    		{
    			addNode(doc, argument, "optionalRequired", "required");
				reqParams.add(arg.getParamDetails().getName() + "=" + getExampleParamValue(def.getMethodName(), arg.getParamDetails().getName(), exampleParameterValues));
    		}

			addNode(doc, argument, "type", "Type: " + arg.getParamDetails().getTypeDescription());

    		if (arg.getDefaultValue() != null)
    			addNode(doc, argument, "default", "Default value is " + arg.getDefaultValue());

    		if (arg.getVersion() != null)
    			addNode(doc, argument, "since", "Since: " + arg.getVersion().getVersionId());
        }
        
        /**
         * Varies parameters are in place to aid in documentation - this may be changed
         */
        List<ParameterDefinitionInt<?>> varies = def.getVariesParams();
        for (ParameterDefinitionInt<?> arg : varies) 
        {
            org.w3c.dom.Element argument = doc.createElement("argument");
            arguments.appendChild(argument);
            
    		addNode(doc, argument, "name", arg.getName());
    		addNode(doc, argument, "description", arg.getDescription());
    		addNode(doc, argument, "optionalRequired", "varies");
			addNode(doc, argument, "type", "Type: " + arg.getTypeDescription());
			
			reqParams.add(arg.getName() + "=" + getExampleParamValue(def.getMethodName(), arg.getName(), exampleParameterValues));
        }

        boolean hasAnd = false;
        boolean dashDMode = true;
        StringBuffer params = new StringBuffer();
        StringBuffer csStr = new StringBuffer();
        for (int i = 0; i < reqParams.size(); i++)
        {
        	String cs = reqParams.get(i);

        	if (def.isMethodGET())
        	{
        		if (dashDMode)
                	csStr.append( " \\\n   -d ").append(cs);
        		else
        		{
        			if (i == 0)
        				csStr.append("?");
        			
        			csStr.append(cs);
        			
        			if ((i + 1) < reqParams.size())
        			{
        				hasAnd = true;
        				csStr.append("&");
        			}
        		}
        	}
        	else if (def.isMethodPUT() || def.isMethodDELETE())
        	{
    			if (i == 0)
    				csStr.append("?");
    			
    			csStr.append(cs);
    			
    			if ((i + 1) < reqParams.size())
    			{
    				hasAnd = true;
    				csStr.append("&");
    			}
        	}
        	else if (def.isMethodPOST())
            	csStr.append( " \\\n   -d ").append(cs);
        	
        	params.append(cs);
    		params.append("&");
        }

    	String start = "curl " + xMethod + " ";
    	if (hasAnd)
    	{
    		start += "\"";
    		csStr.append("\"");
    	}
    	
    	if (dashDMode && def.isMethodGET() && csStr.length() > 0)
        	csStr.append( " \\\n   -G");

		if (def.getAuthenticationMethod() != null)
		{
			addNode(doc, parent, "authenticationInfo", def.getAuthenticationMethod().getAuthenticationDescription());
	    	if (def.getAuthenticationMethod().isBasicAuth())
	        	csStr.append( " \\\n   -u " + client_id + ": " + api_key);
		}

		addNode(doc, parent, "codeSample", start + baseUrl + def.getPath() + csStr.toString());
		
		if (def.getOverrideDocumentationResponse() != null)
			addNode(doc, parent, "responseExample", def.getOverrideDocumentationResponse());
		else
		{
			try {
				String resp = handleCall(def.getPath(), params.toString() + "client_id=" + client_id + "&api_key=" + api_key);
				
				JSONTokener tokener = new JSONTokener(resp); //tokenize the ugly JSON string
				JSONObject finalResult = new JSONObject(tokener); // convert it to JSON object
				
				addNode(doc, parent, "responseExample", finalResult.toString(3));
			} catch (Exception e) {
				logger.warn("Error generating documentation", e);
				addNode(doc, parent, "responseExample", "(ERROR)");
			}
		}
		
	}
	
	protected static final HttpResponse BAD_REQ = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
	protected static TestHttpChannel channel;
	protected static HttpRequest req;
	
	private static String handleCall(String path, String params) throws Exception
	{
		if (channel == null)
		{
			req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "");
			channel = new TestHttpChannel();
		}
		
		channel.write(null); // reset the test channel
		
		StringBuffer fullQuery = new StringBuffer();
		fullQuery.append(path);
		fullQuery.append("?").append(params);
		
		MethodDefinition def = APICalls.getCallByURI(path);
		if (def == null)
			throw new Exception("Invalid Path => " + path);
		
		CallParameters cp = new CallParameters(fullQuery.toString());
		if (def.isAuthenticationRequired())
			cp.setAuthInfo(def.getAuthenticationMethod().authenticate(def, null, cp));

		try {
			def.handleCall(channel, req, cp);
		} catch (InvalidAPIRequestException e) {
			channel.write(BAD_REQ); // simulate a general API failure
		} catch (Exception iar) {
			channel.write(BAD_REQ); // simulate a general API failure
		}
		
		return channel.getResponseString();
	}

	
	private static String getExampleParamValue(String methodName, String arg, HashMap<String,String> exampleParameterValues) throws UnsupportedEncodingException
	{
		if (exampleParameterValues == null)
			return "";
		
		String exVal = exampleParameterValues.get(methodName + "-" + arg);
		if (exVal != null)
			return urlEncode(exVal);

		exVal = exampleParameterValues.get(arg);
		if (exVal != null)
			return urlEncode(exVal);
		
		return "";
	}
	
	private static String urlEncode(String val) throws UnsupportedEncodingException 
	{
		return URLEncoder.encode(val, "UTF-8");
	}
	
	private static boolean isParamRequired(MethodDefinition def, String paramName) {
		
//		if (!"v1/intelegrid/core/authenticate".equalsIgnoreCase(def.getPath()))
//			return false;
		
		for (Object val : def.getParameterValidation())
		{
			if (val instanceof APICallParam)
			{
				APICallParam param = (APICallParam)val;
				if (param.getParamDetails().getName().equals(paramName))
					return true;
			}
			else if (val instanceof Validated)
			{
				
			}
			else if (val instanceof ParameterDefinitionInt)
			{
				if (((ParameterDefinitionInt)val).getName().equals(paramName))
					return true;
			}
		}
		return false;
	}
	
	private static void addNode(org.w3c.dom.Document doc, org.w3c.dom.Element parent, String nodeName, String nodeVal) {
		
		if (nodeVal == null)
			return;
		
        org.w3c.dom.Element methodXml = doc.createElement(nodeName);
        parent.appendChild(methodXml);
        methodXml.setTextContent(nodeVal);
        
		
	}

	public static void createPdf(String filename, String title, String version) throws IOException, DocumentException, SQLException {
		
        // step 1
		Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        
        addTitle(document, title, version);
        
        addBasicInfo(document);
        
        
        // step 4
        List<MethodDefinition> methods = APICalls.getMethods();
        
        Collections.sort(methods, new MethodOrder());
        
        for (MethodDefinition method : methods) {
        	System.out.println("Method: " + method.getMethodName());
        }        
        
        for (MethodDefinition method : methods) {
        	
            Paragraph p = createMethodInformation(method);
            p.setAlignment(Element.ALIGN_LEFT);
            p.setIndentationLeft(18);
            p.setFirstLineIndent(-18);
            document.add(p);
            
            /*
             * Basic Info
             */
            PdfPTable table = new PdfPTable(2);
            table.setWidths(new int[] { 20, 80 });
            table.setSpacingBefore(10.0f);
            table.setSpacingAfter(5.0f);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(4f);
            
            addURI(method, table);
            addHTTPMethods(method, table);
            addAuthRequirements(method, table);
            addHashEnforcement(method, table);
            addSinceVersion(method, table);
            
            document.add(table);

            /*
             * Parameters
             */
            table = new PdfPTable(4);
            table.setWidths(new int[] { 20, 55, 15, 10 });
            table.setSpacingBefore(10.0f);
            table.setSpacingAfter(20.0f);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(4f);
            
            table.addCell(new Phrase("Parameter Name", BOLD));
            table.addCell(new Phrase("Description", BOLD));
            table.addCell(new Phrase("Required", BOLD));
            table.addCell(new Phrase("Since", BOLD));
            
            addParameters(method, table);
            
            document.add(table);

            
            
        }
        // step 5
        document.close();
    }
	
	public static void addTitle(Document document, String title, String version) throws DocumentException {
		
        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Phrase("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + title + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n", LARGE));
        document.add(p);

        p = new Paragraph();
        p.setFont(NORMAL);
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(new Phrase("Version " + version));
        document.add(p);
        
        document.newPage();
	}
	
	public static void addBasicInfo(Document document) throws DocumentException {
		
		Paragraph p = new Paragraph();
		p.add(new Phrase("Basic Request Information", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("The API users HTTP GET, POST, DELETE, and PUT requests for message exchange. Implementations may require a SSL-encrypted connection. Please note that all parameters should be properly encoded for use in HTTP.\n\n\n"));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Request Format", BOLD));
		document.add(p);
		
        PdfPTable table = new PdfPTable(2);
        table.setWidths(new int[] { 20, 80 });
        table.setSpacingBefore(10.0f);
        table.setSpacingAfter(25.0f);
        table.setWidthPercentage(100.0f);
        table.getDefaultCell().setPadding(4f);

        table.addCell("GET");
        table.addCell("/api/path/method?param1=value1&param2=value2...");
        
        table.addCell("POST");
        table.addCell("/api/path/method\nRequest parameters posted per RFC 2616");
        
        document.add(table);

		p = new Paragraph();
		p.add(new Phrase("Request Signature", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Certain API requests may require a request signature to be included as a parameter. The request signature should be calculated by sorting all request parameters alphabetically and performing a hmacSHA256 hash on the resulting URL encoded query string.\n\nExample Request: \n/api/test/hashtest?paramH=value1&paramA=value2 \n\nshould be calculated as the hash of:\n/api/test/hashtest?paramA=value2&paramH=value1\n\nNote: you can test your algorithm with the /api/test/hashtest call.\n\n\n"));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Authentication", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("If authentication is required the 'username' and 'accesscode' (user's password) parameters must be provided in each request.\n\nNote: If providing the hashed version of the user's password you must include the parameter s2=true\n\n\n"));
		document.add(p);


		p = new Paragraph();
		p.add(new Phrase("JSON", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("The API will respond with data in JSON format. See an example reponse below:"));
		document.add(p);

		p = new Paragraph();
		p.add(new Phrase("{\napiResult : { status : \"SUCCESS\" }\nmessage : \"Sample Message\"\nexampleObject : { a1 : 31, b1 : \"test\" }\n}", COURIER));
		document.add(p);
		
        document.newPage();

	}
	
	public static Paragraph createMethodInformation(MethodDefinition method) {
		
        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        
        p.add(getMethodHeader(method));
        
        p.add(getMethodDescription(method));

        return p;
    }
	
	private static Paragraph getMethodHeader(MethodDefinition method) {

        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.add(new Phrase("Method: "));
        p.add(new Phrase(method.getMethodName(), BOLD));
        return p;
	}

	private static Paragraph getMethodDescription(MethodDefinition method) {

        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.add(new Phrase(method.getDescription()));
        return p;
	}

	private static void addURI(MethodDefinition method, PdfPTable table) {

        table.addCell("URI");
        table.addCell("/api/" + method.getPath());
        
	}
	
	private static void addSinceVersion(MethodDefinition method, PdfPTable table) {

        table.addCell("Since Version");
        table.addCell(method.getSinceVersion().getVersionId());
        
	}
	
	private static void addAuthRequirements(MethodDefinition method, PdfPTable table) {

		table.addCell("Authentication");
		if (method.isAuthenticationRequired())
			table.addCell("Required");
		else
			table.addCell("No");

	}
	
	private static void addHashEnforcement(MethodDefinition method, PdfPTable table) {

		table.addCell("Request Sig");
		if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_NONE)
			table.addCell("No");
		else if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_WARN)
			table.addCell("Warn");
		else if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_REJECT)
			table.addCell("Enforce");
	}
	
	private static void addParameters(MethodDefinition method, PdfPTable table) {

		
		List<APICallParam> params = method.getAPIParameters();
		for (APICallParam param : params)
		{
	        table.addCell(param.getParamDetails().getName());
	        table.addCell(param.getDescription());
//	        if (param.req)
//	        	table.addCell("Yes");
//	        else
//	        	table.addCell("No");
	        
        	table.addCell("TBD");

	        table.addCell(param.getVersion().getVersionId());
		}

	}
	
	private static void addHTTPMethods(MethodDefinition method, PdfPTable table) {

        table.addCell("HTTP Method(s)");

        StringBuilder s = new StringBuilder();
        
        // we add the four remaining cells with addCell()
        if (method.isMethodDELETE())
        	s.append("DELETE ");
        
        if (method.isMethodGET())
        	s.append("GET ");
        
        if (method.isMethodPOST())
        	s.append("POST ");
        
        if (method.isMethodPUT())
        	s.append("PUT ");
		
        table.addCell(s.toString());
	}

	/** A font used in our PDF file */
    public static final Font LARGE = new Font(FontFamily.HELVETICA, 24, Font.BOLD);
	/** A font used in our PDF file */
    public static final Font COURIER = new Font(FontFamily.COURIER, 12, Font.NORMAL);
	/** A font used in our PDF file */
    public static final Font NORMAL = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
    /** A font used in our PDF file */
    public static final Font BOLD = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    /** A font used in our PDF file */
    public static final Font ITALIC = new Font(FontFamily.HELVETICA, 12, Font.ITALIC);
    /** A font used in our PDF file */
    public static final Font BOLDITALIC = new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC);
 
}
