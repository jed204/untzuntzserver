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
		
		return 0;
	}

	public ChannelFuture bind(SocketAddress arg0) {
		
		return null;
	}

	public ChannelFuture close() {
		
		return null;
	}

	public ChannelFuture connect(SocketAddress arg0) {
		
		return null;
	}

	public ChannelFuture disconnect() {
		
		return null;
	}

	public ChannelFuture getCloseFuture() {
		
		return null;
	}

	public ChannelConfig getConfig() {
		
		return null;
	}

	public ChannelFactory getFactory() {
		
		return null;
	}

	public Integer getId() {
		
		return null;
	}

	public int getInterestOps() {
		
		return 0;
	}

	public SocketAddress getLocalAddress() {
		
		return null;
	}

	public Channel getParent() {
		
		return null;
	}

	public ChannelPipeline getPipeline() {
		
		return null;
	}

	public SocketAddress getRemoteAddress() {
		return new InetSocketAddress("localhost", 12345);
	}

	public boolean isBound() {
		
		return false;
	}
	
	public boolean isConnected() {
		
		return false;
	}

	public boolean isOpen() {
		
		return false;
	}

	public boolean isReadable() {
		
		return false;
	}

	public boolean isWritable() {
		
		return false;
	}

	public ChannelFuture setInterestOps(int arg0) {
		
		return null;
	}

	public ChannelFuture setReadable(boolean arg0) {
		
		return null;
	}

	public ChannelFuture unbind() {
		
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
		
		return null;
	}

	public Object getAttachment() {
		
		return null;
	}

	public void setAttachment(Object arg0) {
		
		
	}

}
