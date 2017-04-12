package com.untzuntz.ustackserver.server;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

public class ServerFactory implements ChannelPipelineFactory {

	private Timer timer;
	public ServerFactory()
	{
		timer = new HashedWheelTimer();
	}

	public ChannelPipeline getPipeline() throws Exception {
		
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		
		pipeline.addLast("decoder", new HttpRequestDecoder(16384, 16384, 16384));
		pipeline.addLast("inflater", new HttpContentDecompressor());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("sencoder", new StringEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
		pipeline.addLast("idlehandler", new IdleStateHandler(timer, 20, 20, 0));
		pipeline.addLast("deflater", new HttpContentCompressor(1));
		pipeline.addLast("handler", new ServerHandler());
		
		return pipeline;
	}

}
