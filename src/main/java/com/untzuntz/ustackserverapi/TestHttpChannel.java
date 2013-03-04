package com.untzuntz.ustackserverapi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.stream.ChunkedInput;
import org.jboss.netty.handler.stream.ChunkedStream;

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

	public File getOutputFile() {
		return outputFile;
	}
	
	private File outputFile;
	private HttpResponse resp;
	public ChannelFuture write(Object arg0) {
		
		if (arg0 instanceof HttpResponse)
			resp = (HttpResponse)arg0;
		else if (arg0 instanceof String)
		{
			if (outputFile != null)
			{
				BufferedOutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(outputFile, true));
					out.write(((String)arg0).getBytes());	
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try { out.close(); } catch (Exception e) {}
				}
			}
		}
		else if (arg0 instanceof ChunkedInput)
		{
			String fileName = "tmpfile." + UUID.randomUUID() + "-" + Thread.currentThread().getName().replace("/", "").replace("#", "").replace(" ", "") + ".tmp";
			outputFile = new File(getTempDir(), fileName);
			BufferedOutputStream out = null;

			ChunkedInput cs = (ChunkedInput)arg0;
			try {
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
				
				while (cs.hasNextChunk())
				{
					Object nc = cs.nextChunk();
					if (nc instanceof ChannelBuffer)
					{
						ChannelBuffer buf = (ChannelBuffer)nc;
						out.write(buf.array());
					}
					else
						System.err.println("Unknown chunk type => " + nc.getClass().getName());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { out.close(); } catch (Exception e) {}
			}
		}
		else if (arg0 instanceof ChunkedStream)
		{
			String fileName = "tmpfile." + UUID.randomUUID() + "-" + Thread.currentThread().getName().replace("/", "").replace("#", "").replace(" ", "") + ".tmp";
			outputFile = new File(getTempDir(), fileName);
			BufferedOutputStream out = null;

			ChunkedStream cs = (ChunkedStream)arg0;
			try {
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
				
				while (cs.hasNextChunk())
				{
					Object nc = cs.nextChunk();
					if (nc instanceof ChannelBuffer)
					{
						ChannelBuffer buf = (ChannelBuffer)nc;
						out.write(buf.array());
					}
					else
						System.err.println("Unknown chunk type => " + nc.getClass().getName());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { out.close(); } catch (Exception e) {}
			}
		}
		else
		{
			resp = null;
		}
		
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
		if (resp == null && outputFile == null)
			return 550;
		
		if (outputFile != null)
			return 200;
		
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

    public static String getTempDir()
    {
        String fileSeparator = File.separator;
    	String ret = System.getProperty("java.io.tempdir");
    	if (ret == null)
    		ret = System.getProperty("java.io.tmpdir");
    	
    	if (ret == null)
    	{
    		String osName = System.getProperty("os.name");
    		if (osName == null)
    			return fileSeparator + "tmp" + fileSeparator;

    		if (osName.toLowerCase().indexOf("windows") > -1)
    		{
    			return fileSeparator + "windows" + fileSeparator + "temp" + fileSeparator;
    		}
    		
			return fileSeparator + "tmp" + fileSeparator;
    	}

    	if ( !ret.endsWith( fileSeparator ) )
    		ret += fileSeparator;

    	return ret;
    }

}
