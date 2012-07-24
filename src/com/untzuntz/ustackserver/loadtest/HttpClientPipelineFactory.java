package com.untzuntz.ustackserver.loadtest;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	public ChannelPipeline getPipeline() throws Exception {
	
		ChannelPipeline pipeline = pipeline();
		pipeline.addLast("codec", new HttpClientCodec());
		pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("handler", new HttpResponseHandler());
		return pipeline;

	}
	
}
