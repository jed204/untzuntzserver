package com.untzuntz.ustackserver.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.DiskAttribute;
import org.jboss.netty.handler.codec.http.multipart.DiskFileUpload;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.jboss.netty.handler.codec.http.multipart.HttpDataFactory;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.mongodb.util.JSON;
import com.untzuntz.ustack.data.UDataCache;
import com.untzuntz.ustack.main.UAppCfg;
import com.untzuntz.ustack.main.UFile;
import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.InvalidAPIRequestException;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.MethodDefinition.RateLimit;
import com.untzuntz.ustackserverapi.auth.AuthorizationInt;
import com.untzuntz.ustackserverapi.params.ParamNames;
import com.untzuntz.ustackserverapi.util.UploadedFile;

public class ServerHandler extends SimpleChannelUpstreamHandler {
	
    static Logger           		logger               	= Logger.getLogger(ServerHandler.class);
    
    static final ConcurrentHashMap<String, ChannelGroup> channels = new ConcurrentHashMap<String, ChannelGroup>();
    private static final boolean stackDumpErrors;
    
    static {
        UOpts.addMessageBundle("com.untzuntz.ustack.resources.Messages");
        stackDumpErrors = true; //"true".equals(System.getProperty("StackDumpErrors"));
    }
    
    private HttpRequest request;
    private boolean readingChunks;
    private String userName;
    private boolean noLogging;
    private List<UploadedFile> uploadedFiles;
    private File targetFile;
	private OutputStream targetOutputStream;
    private final StringBuilder buf = new StringBuilder();

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(0); // Disk if size exceed MINSIZE

    private HttpPostRequestDecoder decoder;
    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
                                                         // on exit (in normal
                                                         // exit)
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
                                                        // exit (in normal exit)
    }    
    
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)	throws Exception {
		super.channelOpen(ctx, e);
		logger.debug(String.format("%s => Connection started", e.getChannel().getRemoteAddress()));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		if (!readingChunks) {

            if (decoder != null) {
                decoder.cleanFiles();
                decoder = null;
            }

			Object msg = e.getMessage();
			if (msg instanceof HttpRequest)
			{
				request = (HttpRequest)msg;
				if (is100ContinueExpected(request)) {
					send100Continue(e);
				}
				
				String uri = request.getUri().substring(5);
				if (uri.indexOf("?") > -1)
					uri = uri.substring(0, uri.indexOf("?"));
				MethodDefinition cls = APICalls.getCallByURI(uri);
				if (cls != null && cls.isExpectingFile())
				{
					if (isMultiPartOrForm(request))
						decoder = new HttpPostRequestDecoder(factory, request);
					else
					{
						UFile directory = UOpts.getDirectory( UAppCfg.DIRECTORY_SCRATCH );
						directory.mkdirs();

						targetFile = new File(directory + "/incoming-" + System.currentTimeMillis() + ".dat");
						logger.info("Receiving upload from caller, saving to: " + targetFile);
						targetOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
					}
				}
				
				
				if (request.isChunked())
					readingChunks = true;
				else
				{
					if (decoder != null)
						decoder.offer(new DefaultHttpChunk(request.getContent()));
					
					String params = getParamsFromRequest();
					handleHttpRequest(ctx, (HttpRequest)msg, params, cls );
				}
			}

		}
		else
		{
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (decoder != null)
				decoder.offer(chunk);

			if (chunk.isLast()) {
				readingChunks = false;
				String params = null;
				if (decoder != null || targetOutputStream != null)
					params = getParamsFromRequest();
				else
				{
					buf.append(chunk.getContent().toString(CharsetUtil.UTF_8));
					params = buf.toString();
				}
				handleHttpRequest(ctx, request, params, null );
			}
			else if (targetOutputStream != null)
			{
				ChannelBuffer buffer = chunk.getContent();
				buffer.readBytes(targetOutputStream, buffer.readableBytes());
			}
			else if (decoder == null) 
				buf.append(chunk.getContent().toString(CharsetUtil.UTF_8));
		}
	}
    
	private String getHeader(HttpRequest req, String headerName) {
		List<String> headerValues = req.getHeaders(headerName);
		if (headerValues.size() == 0)
			return null;

		return headerValues.get(0);
	}
	
	private boolean isMultiPartOrForm(HttpRequest req) {
		
		String contentTypeStr = getHeader(req, "Content-Type");
		if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentTypeStr))
			return true;
		else if (contentTypeStr != null && contentTypeStr.indexOf("multipart/form-data") > -1)
			return true;
		
		return false;
		
	}
	
	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.CONTINUE);
		e.getChannel().write(response);
	}
	
	private String getParamsFromRequest() throws EndOfDataDecoderException, IOException {
		String params = null;
		if (decoder != null)
		{
			StringBuffer paramBuf = new StringBuffer();
			try {
				while (decoder.hasNext())
				{
					InterfaceHttpData data = decoder.next();
				
					if (data.getHttpDataType() == HttpDataType.Attribute)
					{
						if (paramBuf.length() > 0)
							paramBuf.append("&");
	
						Attribute attribute = (Attribute) data;
						paramBuf.append(attribute.getName()).append("=").append(URLEncoder.encode(attribute.getValue(), "UTF-8"));
					}
					else if (data.getHttpDataType() == HttpDataType.FileUpload)
					{
						if (uploadedFiles == null)
							uploadedFiles = new ArrayList<UploadedFile>();
	
		                FileUpload fileUpload = (FileUpload) data;
		                if (fileUpload.isCompleted())
		                	uploadedFiles.add( new UploadedFile(fileUpload.getFile(), fileUpload.getFilename(), fileUpload.getContentType()) );
					}
				}
			} catch (EndOfDataDecoderException e) {}
			params = paramBuf.toString();
			
			logger.info("Parameters: " + params);
		}
		else if (targetOutputStream != null)
		{
			ChannelBuffer buffer = request.getContent();
			if (buffer.readableBytes() > 0)
				buffer.readBytes(targetOutputStream, buffer.readableBytes());

			targetOutputStream.flush();
			targetOutputStream.close();
			targetOutputStream = null;
			
			String fileName = getHeader(request, "X-Filename");
			String contentType = getHeader(request, "Content-Type");
			
			if (fileName == null)
				fileName = "Unknown-" + System.currentTimeMillis();
			if (contentType == null)
				contentType = "application/binary";
			
			if (uploadedFiles == null)
				uploadedFiles = new ArrayList<UploadedFile>();
			
        	uploadedFiles.add( new UploadedFile(targetFile, fileName, contentType) );

		}
		else
		{
			ChannelBuffer content = request.getContent();
			params = content.toString(CharsetUtil.UTF_8);
		}

		return params;
	}
		
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req, String params, MethodDefinition cls) throws Exception {
		
		String[] uri = req.getUri().split("/");
		
		if ("index.html".equalsIgnoreCase(uri[1]))
		{
			APIResponse.httpOk(ctx.getChannel(), " ", "text/plain", null, null, null, false);
		}
		else if ("favicon.ico".equalsIgnoreCase(uri[1]) || uri.length < 1)
		{
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));
		}
		else if ("api".equalsIgnoreCase(uri[1]))
			doAPICall(resolveAPICall(ctx, req, params, cls));
		else
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));

	}
	
	/**
	 * Looks at the URL and parameters to determine the caller's ability to make this call
	 * 
	 * @param ctx
	 * @param req
	 * @param paramStr
	 * @return
	 */
	private CallInstance resolveAPICall(ChannelHandlerContext ctx, HttpRequest req, String paramStr, MethodDefinition cls)
	{
		String path = req.getUri().substring(5);
		
		/*
		 * Setup Parameters
		 */
		if (req.getMethod() == HttpMethod.POST && paramStr != null)
		{
			if (!path.endsWith("?") && paramStr.length() > 0)
				path += "?";
			
			path += paramStr;
		}
		
		CallParameters params = new CallParameters(path);
		params.setRemoteIpAddress( ((InetSocketAddress)ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress() );
		if (req.getHeader("X-Real-IP") != null)
			params.setRemoteIpAddress( req.getHeader("X-Real-IP") );
		if (req.getHeader("X-Forwarded-For") != null)
			params.setRemoteIpAddress( req.getHeader("X-Forwarded-For") );
		if (req.getHeader("X-Country-Code") != null)
			params.setRemoteCountry( req.getHeader("X-Country-Code") );
		else
			params.setRemoteCountry("UNK");
		if (params.get(ParamNames.app_name) == null)
			params.setParameterValue( ParamNames.app_name.getName(), req.getHeader("User-Agent") );
		
		if (params.getRemoteIpAddress().startsWith("/"))
			params.setRemoteIpAddress( params.getRemoteIpAddress().substring(1) );
		if (params.getRemoteIpAddress().indexOf(":") > -1)
			params.setRemoteIpAddress( params.getRemoteIpAddress().substring(0, params.getRemoteIpAddress().indexOf(":")) );
			
		
		/*
		 * Check for Token in Cookie
		 */
		if (params.get(ParamNames.token) == null && req.getHeader("Cookie") != null)
		{
			Set<Cookie> cookies = new CookieDecoder().decode(req.getHeader("Cookie"));
			Iterator<Cookie> it = cookies.iterator();
			while (it.hasNext())
			{
				Cookie cookie = it.next();
				if ("UNTZ".equals(cookie.getName()))
				{
					logger.debug("\t Cookie for 'UNTZ' found => " + cookie.getValue());
					params.setParameterValue(ParamNames.token.getName(), cookie.getValue());
				}
			}
		}

		path = params.getPath();

		/*
		 * Timing
		 */
		logger.debug(String.format("%s => API Path: %s [Client Ver: %s]", params.getRemoteIpAddress(), path, params.get(ParamNames.client_ver)));
		long apiCallStart = System.currentTimeMillis();
		
		/*
		 * Determine the actual call
		 */
		if (cls == null)
			cls = APICalls.getCallByURI(path);
		if (cls == null)
		{
			logger.info(String.format("%s => UNKNOWN API Path: %s [Client Ver: %s]", params.getRemoteIpAddress(), path, params.get(ParamNames.client_ver)));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Unknown API Call Requested"), req, HttpResponseStatus.NOT_FOUND, params);
			return null;
		}
		
		if (cls.getRateLimits() != null)
		{
			for (RateLimit limit : cls.getRateLimits())
			{
				long block = (System.currentTimeMillis() / 1000L / limit.timeframe);
				String key = String.format("%s_%s_%d_%d", params.getRemoteIpAddress(), limit.key, limit.timeframe, block);
				long reqs = UDataCache.getInstance().incr(key, limit.timeframe, 1);
				if (reqs > limit.maxRequests)
				{
					logger.error(String.format("%s requested '%s' %d times in %d seconds, allowed max requests is %d", params.getRemoteIpAddress(), limit.key, reqs, limit.timeframe, limit.maxRequests));
					APIResponse.httpError(ctx.getChannel(), APIResponse.error("Too many requests"), req, HttpResponseStatus.NOT_FOUND, params);
					return null;
				}
					
			}
		}
		
		if (req.getMethod().equals(HttpMethod.OPTIONS))
		{
			APIResponse.httpOk(ctx.getChannel(), JSON.serialize(APIResponse.success()), APIResponse.ContentTypeJSON, params, null, req, true);
			return null;
		}
		
		/*
		 * Check if the client has provided a version #
		 */
		if (!cls.isClientVerCheckDisabled() && (params.get(ParamNames.client_ver) == null || params.get(ParamNames.client_ver).length() == 0))
		{
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Client Version not provided"), req, HttpResponseStatus.BAD_REQUEST, params);
			return null;
		}

		/*
		 * Confirm HTTP Method - only if there is no JSONP
		 */
		if (params.get(ParamNames.json_callback) == null && !cls.isMethodEnabled(req.getMethod()))
		{
			logger.info(String.format("%s => API Path: %s || Invalid Method: %s", params.getRemoteIpAddress(), path, req.getMethod().toString()));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Invalid HTTP Method for API Call"), req, HttpResponseStatus.BAD_REQUEST, params);
			return null;
		}

		/*
		 * Confirm the caller is authenticated to make this call
		 */
		if (cls.isAuthenticationRequired())
		{
			try {
				params.setAuthInfo(cls.getAuthenticationMethod().authenticate(cls, req, params));
			} catch (APIException e) {
				APIResponse.httpError(ctx.getChannel(), APIResponse.error(e.getMessage()), req, HttpResponseStatus.UNAUTHORIZED, params);
				return null;
			}
		}

		/*
		 * Check the call hash
		 */
		if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_NONE)
		{
			// order parameters by alpha
			// calculate hash
			String sig = params.getRequestSignature(params.get(ParamNames.api_key));
			
			boolean failed = true;
			if (sig != null && params.has(ParamNames.RequestSignature) && sig.equals(params.get(ParamNames.RequestSignature)))
				failed = false;
			
			if (failed)
			{ 
				if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_REJECT)
					logger.warn(String.format("%s [%s] Request Signature Mismatch -> Client Sent [%s], we expected [%s]", params.getRemoteIpAddress(), path, params.get(ParamNames.RequestSignature), sig));
				else if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_REJECT)
				{
					APIResponse.httpError(ctx.getChannel(), APIResponse.error("Bad Request Signature"), req, HttpResponseStatus.BAD_REQUEST, params);
					return null;
				}
			}
		}

		/*
		 * Verify parameters of call - set default values
		 */
		try {
			cls.validateCall(params);
		} catch (APIException apiErr) {
			logger.warn(String.format("%s [%s] API Exception => %s", params.getRemoteIpAddress(), path, apiErr));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), req, HttpResponseStatus.BAD_REQUEST, params);
			return null;
		}
		
		/*
		 * Authorize call
		 */
		if (cls.isAuthorizationRequired())
		{
			try {
				List<AuthorizationInt> auths = cls.getAuthorizationMethods();
				for (AuthorizationInt auth : auths)	
					auth.authorize(cls, params);
			} catch (ClassCastException cce) {
				logger.error(String.format("%s [%s] Authorization failed due to an invalid authentication/authorization combo", params.getRemoteIpAddress(), path), cce);
				APIResponse.httpError(ctx.getChannel(), APIResponse.error("Invalid Authentication/Authorization Combo"), req, HttpResponseStatus.FORBIDDEN, params);
				return null;
			} catch (APIException e) {
				APIResponse.httpError(ctx.getChannel(), APIResponse.error(e.toDBObject()), req, e.getHttpStatus(), params);
				return null;
			}
		}

		// Setup the response object
		CallInstance ret = new CallInstance();
		ret.cls = cls;
		ret.ctx = ctx;
		ret.req = req;
		ret.params = params;
		ret.path = path;
		ret.apiCallStart = apiCallStart;
		ret.params.setUploadedFiles(uploadedFiles);
		ctx.setAttachment(ret);
		return ret;
	}
	
	private class CallInstance {
		MethodDefinition cls;
		ChannelHandlerContext ctx;
		HttpRequest req;
		CallParameters params;
		String path;
		long apiCallStart;
	}
	
	/**
	 * Make the actual API call into the code
	 * 
	 * @param callInstance
	 */
	private void doAPICall(CallInstance callInstance)
	{		
		if (callInstance == null)
			return;
		
		noLogging = callInstance.cls.isNoLogging();
		//callInstance.cls.isExpectingFile() && 
		if (callInstance.req.getMethod().equals(HttpMethod.OPTIONS))
		{
			// handle browser checks for access headers
			APIResponse.httpOk(callInstance.ctx.getChannel(), JSON.serialize(APIResponse.success()), APIResponse.ContentTypeJSON, callInstance.params, null, callInstance.req, true);
			return;
		}
		else
		{
			/*
			 * Do the actual call
			 */
			try {
				callInstance.cls.handleCall(callInstance.ctx.getChannel(), callInstance.req, callInstance.params);
			} catch (APIException apiErr) {
				logger.warn(String.format("%s [%s] API Exception => %s", callInstance.params.getRemoteIpAddress(), callInstance.path, apiErr));
				APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
			} catch (InvalidAPIRequestException iar) {
				logger.warn(String.format("%s [%s] Bad API Call", callInstance.params.getRemoteIpAddress(), callInstance.path), iar);
				APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error("Invalid Request to API Call"), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
			} catch (InvocationTargetException ierr) {
				if (ierr.getCause() != null)
				{
					if (ierr.getCause() instanceof APIException)
					{
						APIException apiErr = (APIException)ierr.getCause();
						logger.warn(String.format("%s [%s] API Exception => %s", callInstance.params.getRemoteIpAddress(), callInstance.path, apiErr));
						APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
					}
					else
					{
						if (ierr.getCause() instanceof NullPointerException)
							logger.warn(String.format("%s [%s] Bad API Call => %s", callInstance.params.getRemoteIpAddress(), callInstance.path, ierr.getCause()), ierr.getCause());
						else
						{
							if (stackDumpErrors)
								logger.warn(String.format("%s [%s] Bad API Call => %s", callInstance.params.getRemoteIpAddress(), callInstance.path, ierr.getCause()), ierr.getCause());
							else
								logger.warn(String.format("%s [%s] Bad API Call => %s", callInstance.params.getRemoteIpAddress(), callInstance.path, ierr.getCause()));
						}
						APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error(ierr.getCause().getMessage()), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
					}
						
				}
				else
				{
					logger.warn(String.format("%s [%s] Bad API Call", callInstance.params.getRemoteIpAddress(), callInstance.path), ierr);
					APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error("Bad Request to API Call"), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
				}
			} catch (Exception err) {
				logger.warn(String.format("%s [%s] Uncaught Exception during API call", callInstance.params.getRemoteIpAddress(), callInstance.path), err);
				APIResponse.httpError(callInstance.ctx.getChannel(), APIResponse.error("Unknown Error"), callInstance.req, HttpResponseStatus.BAD_REQUEST, callInstance.params);
			}
		}
		
		long apiCallFinish = System.currentTimeMillis();
		if (!noLogging)
			logger.info(String.format("%s => API Path: %s [Client Ver: %s|%s] -> %d ms", callInstance.params.getRemoteIpAddress(), callInstance.path, callInstance.params.get(ParamNames.app_name), callInstance.params.get(ParamNames.client_ver), (apiCallFinish - callInstance.apiCallStart)));

	}
	
	public static ChannelGroup getChannel(String u, boolean add) {
		
		ChannelGroup cg = channels.get(u);
		if (cg == null && add)
		{
			cg = new DefaultChannelGroup();
			channels.put(u, cg);
		}
		return cg;

	}
	
	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}
		
		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		
		if (e.getCause() instanceof IOException)
			logger.info(String.format("%s => Client closed their request", e.getChannel().getRemoteAddress()));
		else
			logger.warn(String.format("%s => Error during API handling [%s]", e.getChannel().getRemoteAddress(), e.getCause()), e.getCause());
		
		e.getChannel().close();
		cleanup(e.getChannel());
	}
	
	private void cleanup(Channel channel)
	{
		if (userName != null)
		{
			logger.info("Cleaning channel : " + userName);
			ChannelGroup cg = channels.get(userName);
			if (cg != null)
			{
				cg.remove(channel);
				if (cg.size() == 0)
					channels.remove(userName);
			}
		}
		
        if (decoder != null) 
            decoder.cleanFiles();
        
        if (targetFile != null)
        {
        	try { 
        		targetOutputStream.close();
        	} catch (Exception e) {}

        	targetOutputStream = null;
        	targetFile.delete();
        }
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		cleanup(e.getChannel());

		long timing = 0;
		if (e.getChannel().getAttachment() != null)
			timing = (System.currentTimeMillis() - (Long)e.getChannel().getAttachment());
		if (!noLogging) {
			CallInstance ci = (CallInstance)ctx.getAttachment();
			if (ci != null)
				logger.info(String.format("%s => Connection Closed [%d ms]", ci.params.getRemoteIpAddress(), timing));
		}
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
		cleanup(e.getChannel());
	}

}
