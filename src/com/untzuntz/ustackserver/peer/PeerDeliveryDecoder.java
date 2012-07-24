package com.untzuntz.ustackserver.peer;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class PeerDeliveryDecoder extends FrameDecoder {

    static Logger           		logger               	= Logger.getLogger(PeerDeliveryDecoder.class);

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {
		
		if (buf.readableBytes() < 4) {
			
			return null;
		}
		
		buf.markReaderIndex();
		int length = buf.readInt();
		logger.debug("Reading message from remote : " + length + " bytes");
		
		if (buf.readableBytes() < length)
		{
			logger.debug("\t- Not enough bytes : Needs " + length + " has " + buf.readableBytes());
			buf.resetReaderIndex();
			return null;
		}

		int nameLen = buf.readInt();
		ChannelBuffer nameBuf = buf.readBytes(nameLen);
		PeerDelivery delivery = new PeerDelivery(new String(nameBuf.array()));
		logger.debug("Handle delivery to : " + delivery.getTarget() + " via " + channel.getRemoteAddress());
		return delivery;
	}

}
