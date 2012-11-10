package com.untzuntz.ustackserverapi;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class TestChannelFuture implements ChannelFuture {

	public void addListener(ChannelFutureListener arg0) {
		

	}

	public ChannelFuture await() throws InterruptedException {
		
		return null;
	}

	public boolean await(long arg0) throws InterruptedException {
		
		return false;
	}

	public boolean await(long arg0, TimeUnit arg1) throws InterruptedException {
		
		return false;
	}

	public ChannelFuture awaitUninterruptibly() {
		
		return null;
	}

	public boolean awaitUninterruptibly(long arg0) {
		
		return false;
	}

	public boolean awaitUninterruptibly(long arg0, TimeUnit arg1) {
		
		return false;
	}

	public boolean cancel() {
		
		return false;
	}

	public Throwable getCause() {
		
		return null;
	}

	public Channel getChannel() {
		
		return null;
	}

	public boolean isCancelled() {
		
		return false;
	}

	public boolean isDone() {
		
		return false;
	}

	public boolean isSuccess() {
		
		return false;
	}

	public void removeListener(ChannelFutureListener arg0) {
		

	}

	public ChannelFuture rethrowIfFailed() throws Exception {
		
		return null;
	}

	public boolean setFailure(Throwable arg0) {
		
		return false;
	}

	public boolean setProgress(long arg0, long arg1, long arg2) {
		
		return false;
	}

	public boolean setSuccess() {
		
		return false;
	}

	public ChannelFuture sync() throws InterruptedException {
		
		return null;
	}

	public ChannelFuture syncUninterruptibly() {
		
		return null;
	}

}
