package com.untzuntz.ustackserverapi;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class TestChannelFuture implements ChannelFuture {

	public void addListener(ChannelFutureListener arg0) {
		// TODO Auto-generated method stub

	}

	public ChannelFuture await() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean await(long arg0) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean await(long arg0, TimeUnit arg1) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public ChannelFuture awaitUninterruptibly() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean awaitUninterruptibly(long arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean awaitUninterruptibly(long arg0, TimeUnit arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}

	public Throwable getCause() {
		// TODO Auto-generated method stub
		return null;
	}

	public Channel getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ChannelFutureListener arg0) {
		// TODO Auto-generated method stub

	}

	public ChannelFuture rethrowIfFailed() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setProgress(long arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean setSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	public ChannelFuture sync() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture syncUninterruptibly() {
		// TODO Auto-generated method stub
		return null;
	}

}
