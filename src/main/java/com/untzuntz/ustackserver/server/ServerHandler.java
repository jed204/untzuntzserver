package com.untzuntz.ustackserver.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
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

import com.untzuntz.ustack.aaa.Authentication;
import com.untzuntz.ustackserver.peer.PeerDelivery;
import com.untzuntz.ustackserver.peer.PeerHandler;
import com.untzuntz.ustackserverapi.APICalls;
import com.untzuntz.ustackserverapi.APIResponse;
import com.untzuntz.ustackserverapi.CallParameters;
import com.untzuntz.ustackserverapi.InvalidAPIRequestException;
import com.untzuntz.ustackserverapi.MethodDefinition;

public class ServerHandler extends IdleStateAwareChannelUpstreamHandler {
	
    static Logger           		logger               	= Logger.getLogger(ServerHandler.class);
    
    static final ConcurrentHashMap<String, ChannelGroup> channels = new ConcurrentHashMap<String, ChannelGroup>();
    
    private HttpRequest request;
    private boolean readingChunks;
    private String userName;
    private boolean realtimeEnabled;
    private final StringBuilder buf = new StringBuilder();
    
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)	throws Exception {
		super.channelOpen(ctx, e);
		logger.info(e.getChannel().getRemoteAddress() + " => Connection started");
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
			APIResponse.httpOk(ctx.getChannel(), " ", "text/plain");
		}
		else if ("favicon.ico".equalsIgnoreCase(uri[1]) || uri.length < 1)
		{
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));
		}
		else if ("realtime".equalsIgnoreCase(uri[1]))
		{
			handleRealtime(ctx, uri);
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
		
//		long lus = System.currentTimeMillis();
		if (req.getMethod() == HttpMethod.POST)
		{
			if (!path.endsWith("?") && paramStr.length() > 0)
				path += "?";
			
			path += paramStr;
		}
		
		logger.info("Full Path => " + path);
		
		CallParameters params = new CallParameters(path);

		path = params.getPath();

		logger.info(ctx.getChannel().getRemoteAddress() + " => API Path: " + path);
		
//		long lup = System.currentTimeMillis();
		MethodDefinition cls = APICalls.getCallByURI(path);
		if (cls == null)
		{
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND));
			return;
		}
//		long lum = System.currentTimeMillis();
		
		if (!cls.isMethodEnabled(req.getMethod()))
		{
			logger.info(ctx.getChannel().getRemoteAddress() + " => API Path: " + path + " || Invalid Method: " + req.getMethod().toString());
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
//		long lumc = System.currentTimeMillis();
		
		if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_NONE)
		{
			// order parameters by alpha
			// calculate hash
			
			String sig = params.getRequestSignature(cls.getHashKey());
			
			boolean failed = true;
			if (sig != null && params.getParameter("sig") != null && sig.equals(params.getParameter("sig")))
				failed = false;
			
			if (failed)
			{
				if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_REJECT)
					logger.warn("Request Signature Mismatch -> Client Sent [" + params.getParameter("sig") + "], we expected [" + params.getParameter("sig") + "]");
				else if (cls.getHashEnforcement() > MethodDefinition.HASH_ENFORCEMENT_REJECT)
					sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			}
		}
		
		if (cls.isAuthenticationRequired())
		{
			try {
				
				if ("true".equalsIgnoreCase(params.getParameter("s2")))
					params.setUser(Authentication.authenticateUserHash(params.getParameter("username"), new String(Base64.decodeBase64(params.getParameter("accesscode").getBytes()))));
				else
					params.setUser(Authentication.authenticateUser(params.getParameter("username"), params.getParameter("accesscode")));
				
			} catch (Exception e) {
				APIResponse.httpResponse(ctx.getChannel(), APIResponse.error("Invalid Username/Password"), HttpResponseStatus.UNAUTHORIZED);
				logger.warn("Invalid User Login => " + params.getParameter("username") + " => Reason: " + e);
				return;
			}
		}
//		long lua = System.currentTimeMillis();
//		long tlup = lup - lus;
//		long tlum = lum - lup;
//		long tlua = lua - lumc;

//		long start = System.currentTimeMillis();
		try {
			cls.handleCall(ctx.getChannel(), req, params);
		} catch (InvalidAPIRequestException iar) {
			logger.warn("Bad API Call", iar);
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
		} catch (InvocationTargetException ierr) {
			logger.warn("Bad API Call", ierr);
			if (ierr.getCause() != null)
				APIResponse.httpError(ctx.getChannel(), APIResponse.error(ierr.getCause().getMessage()));
			else
				sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
		} catch (Exception err) {
			logger.warn("Bad API Call", err);
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
		}
//		long timing = System.currentTimeMillis() - start;
//		logger.info(ctx.getChannel().getRemoteAddress() + " => API Complete: " + path + " [" + timing + " ms] lup: " + tlup + " | lum: " + tlum + " | lua: " + tlua);

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
		logger.warn("Error during iFlowConnect handling", e.getCause());
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
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
		cleanup(e.getChannel());
	}

}
