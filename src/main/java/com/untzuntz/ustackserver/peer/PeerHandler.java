package com.untzuntz.ustackserver.peer;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.untzuntz.ustackserver.server.ServerHandler;

public class PeerHandler extends SimpleChannelUpstreamHandler {
	
    static Logger           		logger               	= Logger.getLogger(PeerHandler.class);
    
    static final ChannelGroup peers = new DefaultChannelGroup();
    
    public static ChannelGroup getPeerChannelGroup() { 
    	return peers;
    }
    
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)	throws Exception {
		super.channelOpen(ctx, e);
		
		peers.add(ctx.getChannel());
	}

    
    public static void sendToPeers(PeerDelivery delivery) {
    	
    	logger.info("\t- Sending to " + peers.size() + " peers");
		Iterator<Channel> it = PeerHandler.getPeerChannelGroup().iterator();
		while (it.hasNext())
		{
			Channel c = it.next();
			c.write(delivery);
		}

    }
    
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		
		if (e.getMessage() instanceof PeerDelivery)
		{
			PeerDelivery delivery = (PeerDelivery)e.getMessage();
			
			ChannelGroup tgt = ServerHandler.getChannel(delivery.getTarget(), false);
			if (tgt != null)
			{
				logger.info("Delivery to : " + delivery.getTarget());
				ServerHandler.sendToGroup(tgt, "Test Message");
			}
			else
				logger.info("Delivery to unknown : " + delivery.getTarget());
		}
		
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.warn("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}
}
