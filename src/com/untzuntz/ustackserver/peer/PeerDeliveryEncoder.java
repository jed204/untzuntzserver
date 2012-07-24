package com.untzuntz.ustackserver.peer;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class PeerDeliveryEncoder extends OneToOneEncoder {

    static Logger           		logger               	= Logger.getLogger(PeerDeliveryEncoder.class);

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		
		if (!(msg instanceof PeerDelivery)) {
			// Ignore what this encoder can't encode.
			return msg;
		}
		
		
		PeerDelivery delivery = (PeerDelivery)msg;

		logger.debug("Encoding delivery to : " + delivery.getTarget() + " to peer : " + channel.getRemoteAddress());

		String target = delivery.getTarget();
		int len = target.length() + 4;
		
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeInt(len);
		buf.writeInt(target.length());
		buf.writeBytes(target.getBytes());
		return buf;
	}


}
