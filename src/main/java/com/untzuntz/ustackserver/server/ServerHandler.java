package com.untzuntz.ustackserver.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
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
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.untzuntz.ustack.main.UOpts;
import com.untzuntz.ustackserver.peer.PeerDelivery;
import com.untzuntz.ustackserver.peer.PeerHandler;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.InvalidAPIRequestException;
import com.untzuntz.ustackserverapi.MethodDefinition;
import com.untzuntz.ustackserverapi.auth.AuthorizationInt;
import com.untzuntz.ustackserverapi.params.ParamNames;

public class ServerHandler extends IdleStateAwareChannelUpstreamHandler {
	
    static Logger           		logger               	= Logger.getLogger(ServerHandler.class);
    
    static final ConcurrentHashMap<String, ChannelGroup> channels = new ConcurrentHashMap<String, ChannelGroup>();
    private static final boolean stackDumpErrors;
    
    static {
        UOpts.addMessageBundle("com.untzuntz.ustack.resources.Messages");
        stackDumpErrors = "true".equals(System.getProperty("StackDumpErrors"));
    }
    
    private HttpRequest request;
    private boolean readingChunks;
    private String userName;
    private boolean realtimeEnabled;
    private final StringBuilder buf = new StringBuilder();
    
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)	throws Exception {
		super.channelOpen(ctx, e);
		logger.debug(String.format("%s => Connection started", e.getChannel().getRemoteAddress()));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		if (!readingChunks) {

			Object msg = e.getMessage();
			if (msg instanceof HttpRequest)
			{
				request = (HttpRequest)msg;
				if (is100ContinueExpected(request)) {
					send100Continue(e);
				}
				
				if (request.isChunked()) {
					readingChunks = true;
				}
				else
				{
					ChannelBuffer content = request.getContent();
					handleHttpRequest(ctx, (HttpRequest)msg, content.toString(CharsetUtil.UTF_8) );
				}
			}

		}
		else
		{
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				readingChunks = false;
				handleHttpRequest(ctx, request, buf.toString() );
			}
			else {
				buf.append(chunk.getContent().toString(CharsetUtil.UTF_8));
			}
		}
	}
	
	private void send100Continue(MessageEvent e) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.CONTINUE);
		e.getChannel().write(response);
	}
	
	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
		super.channelIdle(ctx, e);
		if (realtimeEnabled)
			logger.info(e.getChannel().getRemoteAddress() + " => IDLE : " + e.getLastActivityTimeMillis() + " - " + e.getState());
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req, String params) throws Exception {
		
		String[] uri = req.getUri().split("/");
		
		if ("index.html".equalsIgnoreCase(uri[1]))
		{
			APIResponse.httpOk(ctx.getChannel(), " ", "text/plain", null, null);
		}
		else if ("favicon.ico".equalsIgnoreCase(uri[1]) || uri.length < 1)
		{
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));
		}
		else if ("api".equalsIgnoreCase(uri[1]))
		{
			handleAPI(ctx, req, params);
		}
		else
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));

	}
	
	private void handleAPI(ChannelHandlerContext ctx, HttpRequest req, String paramStr)
	{
		String path = req.getUri().substring(5);
		
		if (req.getMethod() == HttpMethod.POST)
		{
			if (!path.endsWith("?") && paramStr.length() > 0)
				path += "?";
			
			path += paramStr;
		}
		
		CallParameters params = new CallParameters(path);
		params.setRemoteIpAddress( ((InetSocketAddress)ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress() );
		if (req.getHeader("X-Real-IP") != null)
			params.setRemoteIpAddress( req.getHeader("X-Real-IP") );
		if (req.getHeader("X-Country-Code") != null)
			params.setRemoteCountry( req.getHeader("X-Country-Code") );
		else
			params.setRemoteCountry("UNK");
		if (params.get(ParamNames.app_name) == null)
			params.setParameterValue( ParamNames.app_name.getName(), req.getHeader("User-Agent") );
		
		if (params.get(ParamNames.token) == null && req.getHeader("Cookie") != null)
		{
			Set<Cookie> cookies = new CookieDecoder().decode(req.getHeader("Cookie"));
			Iterator<Cookie> it = cookies.iterator();
			while (it.hasNext())
			{
				Cookie cookie = it.next();
				if ("UNTZ".equals(cookie.getName()))
				{
					logger.info("\t Cookie for 'UNTZ' found => " + cookie.getValue());
					params.setParameterValue(ParamNames.token.getName(), cookie.getValue());
				}
			}
		}

		path = params.getPath();

		logger.debug(String.format("%s => API Path: %s [Client Ver: %s]", params.getRemoteIpAddress(), path, params.get(ParamNames.client_ver)));
		long apiCallStart = System.currentTimeMillis();
		
		MethodDefinition cls = APICalls.getCallByURI(path);
		if (cls == null)
		{
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Unknown API Call Requested"), HttpResponseStatus.NOT_FOUND, params);
			return;
		}
		
		if (!cls.isClientVerCheckDisabled() && (params.get(ParamNames.client_ver) == null || params.get(ParamNames.client_ver).length() == 0))
		{
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Client Version not provided"), HttpResponseStatus.BAD_REQUEST, params);
			return;
		}

		if (!cls.isMethodEnabled(req.getMethod()))
		{
			logger.info(String.format("%s => API Path: %s || Invalid Method: %s", ctx.getChannel().getRemoteAddress(), path, req.getMethod().toString()));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Invalid HTTP Method for API Call"), HttpResponseStatus.BAD_REQUEST, params);
			return;
		}
		
		if (cls.isAuthenticationRequired())
		{
			try {
				params.setAuthInfo(cls.getAuthenticationMethod().authenticate(cls, req, params));
			} catch (APIException e) {
				APIResponse.httpError(ctx.getChannel(), APIResponse.error(e.getMessage()), HttpResponseStatus.UNAUTHORIZED, params);
				return;
			}
		}

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
					logger.warn(String.format("%s [%s] Request Signature Mismatch -> Client Sent [%s], we expected [%s]", ctx.getChannel().getRemoteAddress(), path, params.get(ParamNames.RequestSignature), sig));
				else if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_REJECT)
				{
					APIResponse.httpError(ctx.getChannel(), APIResponse.error("Bad Request Signature"), HttpResponseStatus.BAD_REQUEST, params);
					return;
				}
			}
		}

		/*
		 * Verify parameters of call - set default values
		 */
		try {
			cls.validateCall(params);
		} catch (APIException apiErr) {
			logger.warn(String.format("%s [%s] API Exception => %s", ctx.getChannel().getRemoteAddress(), path, apiErr));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), HttpResponseStatus.BAD_REQUEST, params);
			return;
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
				logger.error(String.format("%s [%s] Authorization failed due to an invalid authentication/authorization combo", ctx.getChannel().getRemoteAddress(), path), cce);
				APIResponse.httpError(ctx.getChannel(), APIResponse.error("Invalid Authentication/Authorization Combo"), HttpResponseStatus.BAD_REQUEST, params);
				return;
			} catch (APIException e) {
				APIResponse.httpError(ctx.getChannel(), APIResponse.error(e.toDBObject()), HttpResponseStatus.BAD_REQUEST, params);
				return;
			}
		}
		
		/*
		 * Do the actual call
		 */
		try {
			cls.handleCall(ctx.getChannel(), req, params);
		} catch (APIException apiErr) {
			logger.warn(String.format("%s [%s] API Exception => %s", ctx.getChannel().getRemoteAddress(), path, apiErr));
			APIResponse.httpError(ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), HttpResponseStatus.BAD_REQUEST, params);
		} catch (InvalidAPIRequestException iar) {
			logger.warn(String.format("%s [%s] Bad API Call", ctx.getChannel().getRemoteAddress(), path), iar);
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Invalid Request to API Call"), HttpResponseStatus.BAD_REQUEST, params);
		} catch (InvocationTargetException ierr) {
			if (ierr.getCause() != null)
			{
				if (ierr.getCause() instanceof APIException)
				{
					APIException apiErr = (APIException)ierr.getCause();
					logger.warn(String.format("%s [%s] API Exception => %s", ctx.getChannel().getRemoteAddress(), path, apiErr));
					APIResponse.httpError(ctx.getChannel(), APIResponse.error(apiErr.toDBObject()), HttpResponseStatus.BAD_REQUEST, params);
				}
				else
				{
					if (ierr.getCause() instanceof NullPointerException)
						logger.warn(String.format("%s [%s] Bad API Call => %s", ctx.getChannel().getRemoteAddress(), path, ierr.getCause()), ierr.getCause());
					else
					{
						if (stackDumpErrors)
							logger.warn(String.format("%s [%s] Bad API Call => %s", ctx.getChannel().getRemoteAddress(), path, ierr.getCause()), ierr.getCause());
						else
							logger.warn(String.format("%s [%s] Bad API Call => %s", ctx.getChannel().getRemoteAddress(), path, ierr.getCause()));
					}
					APIResponse.httpError(ctx.getChannel(), APIResponse.error(ierr.getCause().getMessage()), HttpResponseStatus.BAD_REQUEST, params);
				}
					
			}
			else
			{
				logger.warn(String.format("%s [%s] Bad API Call", ctx.getChannel().getRemoteAddress(), path), ierr);
				APIResponse.httpError(ctx.getChannel(), APIResponse.error("Bad Request to API Call"), HttpResponseStatus.BAD_REQUEST, params);
			}
		} catch (Exception err) {
			logger.warn(String.format("%s [%s] Uncaught Exception during API call", ctx.getChannel().getRemoteAddress(), path), err);
			APIResponse.httpError(ctx.getChannel(), APIResponse.error("Unknown Error"), HttpResponseStatus.BAD_REQUEST, params);
		}
		
		long apiCallFinish = System.currentTimeMillis();
		logger.info(String.format("%s => API Path: %s [Client Ver: %s|%s] -> %d ms", ctx.getChannel().getRemoteAddress(), path, params.get(ParamNames.app_name), params.get(ParamNames.client_ver), (apiCallFinish - apiCallStart)));

	}
	
	public void handleRealtime(ChannelHandlerContext ctx, String[] uri)
	{
		realtimeEnabled = true;
		userName = uri[2];
		String targetName = uri[3];
		
		logger.info("LOGIN: " + userName);
		ChannelGroup cg = getChannel(userName, true);
		cg.add(ctx.getChannel());
		
		ChannelGroup tgt = getChannel(targetName, false);
		if (tgt == null)
		{
			logger.info("Sending message to target via peers: " + targetName);
			PeerHandler.sendToPeers(new PeerDelivery(targetName));
			return;
		}
		
		logger.info("Sending message to target: " + targetName + " (" + tgt.size() + " connections)");
		
		sendToGroup(tgt, targetName);
	}
	
	public static void sendToGroup(ChannelGroup tgt, String message) {
		
		Iterator<Channel> it = tgt.iterator();
		while (it.hasNext())
		{
			Channel c = it.next();
			HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
			res.setContent(ChannelBuffers.copiedBuffer(message + "\r\n", CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
			c.write(res).addListener(ChannelFutureListener.CLOSE);
		}

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
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelClosed(ctx, e);
		cleanup(e.getChannel());

		long timing = 0;
		if (e.getChannel().getAttachment() != null)
			timing = (System.currentTimeMillis() - (Long)e.getChannel().getAttachment());
		logger.info(String.format("%s => Connection Closed [%d ms]", e.getChannel().getRemoteAddress(), timing));
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
		cleanup(e.getChannel());
	}

}
