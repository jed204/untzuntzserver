package com.untzuntz.ustackserver.loadtest;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

public class HttpResponseHandler extends SimpleChannelUpstreamHandler {

    static Logger           		logger               	= Logger.getLogger(HttpResponseHandler.class);

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			
		HttpResponse response = (HttpResponse) e.getMessage();
		
		ChannelBuffer content = response.getContent();
		if (content.readable() && content.toString(CharsetUtil.UTF_8).indexOf("SUCCESS") == -1) {
			throw new Exception("Invalid response");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		logger.warn("Failed during request : " + e.getCause().toString() + " -> " + ctx.getChannel().getLocalAddress());
	}
	
}
