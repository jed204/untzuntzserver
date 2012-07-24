package com.untzuntz.ustackserver.peer;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public class PeerClientFactory implements ChannelPipelineFactory {

	public PeerClientFactory()
	{
	}

	public ChannelPipeline getPipeline() throws Exception {
		
		
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		
		pipeline.addLast("decoder", new PeerDeliveryDecoder());
		pipeline.addLast("encoder", new PeerDeliveryEncoder());
		pipeline.addLast("handler", new PeerHandler());
		
		return pipeline;
	}


}
