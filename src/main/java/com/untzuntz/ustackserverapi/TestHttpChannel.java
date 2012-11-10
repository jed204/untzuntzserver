package com.untzuntz.ustackserverapi;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class TestHttpChannel implements Channel {

	public int compareTo(Channel arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ChannelFuture bind(SocketAddress arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture close() {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture connect(SocketAddress arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture disconnect() {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture getCloseFuture() {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInterestOps() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public Channel getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelPipeline getPipeline() {
		// TODO Auto-generated method stub
		return null;
	}

	public SocketAddress getRemoteAddress() {
		return new InetSocketAddress("localhost", 12345);
	}

	public boolean isBound() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWritable() {
		// TODO Auto-generated method stub
		return false;
	}

	public ChannelFuture setInterestOps(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture setReadable(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture unbind() {
		// TODO Auto-generated method stub
		return null;
	}

	private HttpResponse resp;
	public ChannelFuture write(Object arg0) {
		
		if (arg0 instanceof HttpResponse)
			resp = (HttpResponse)arg0;
		else
			resp = null;
		
		return new TestChannelFuture();
	}
	
	public boolean hasHeader(String name) {
		if (resp == null)
			return false;
		
		return resp.getHeader(name) != null;
	}
	
	public String getHeader(String name) {
		if (resp == null)
			return null;
		
		return resp.getHeader(name);
	}
	
	public int getResponseCode() {
		if (resp == null)
			return 550;
		
		return resp.getStatus().getCode();
	}
	
	public String getResponseString() {
		
		if (resp == null || resp.getContent() == null)
			return "";
		
		return resp.getContent().toString(java.nio.charset.Charset.forName("UTF-8"));
		
	}

	public ChannelFuture write(Object arg0, SocketAddress arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAttachment() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttachment(Object arg0) {
		// TODO Auto-generated method stub
		
	}

}
