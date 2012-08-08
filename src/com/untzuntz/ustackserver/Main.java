package com.untzuntz.ustackserver;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.untzuntz.ustackserver.peer.PeerFactory;
import com.untzuntz.ustackserver.server.ServerFactory;

public class Main {

    static Logger           		logger               	= Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("log4j.xml");
    	int port = 8081;
    	int peerPort = 8082;
    	int otherPeer = 8084;
    	
    	if (args.length >= 3)
    	{
    		port = Integer.valueOf(args[1]);
    		peerPort = Integer.valueOf(args[2]);
    		otherPeer = Integer.valueOf(args[3]);
    	}
    	
    	Main main = new Main(args[0], port, peerPort, otherPeer);
    	
		main.run();
    }

    private int port;
    private int peerPort;
    private int otherPeer;
    private String apiVersion;
    
    public String getAPIVersion() {
    	return apiVersion;
    }
    
    public Main(String code, int p, int pp, int op) {

    	port = p;
    	peerPort = pp;
    	otherPeer = op;
    	
    	try {
    		
			Class apiClass = Class.forName(code);
			Object apiObj = apiClass.newInstance();
				
    		Method m = apiClass.getMethod("setup", null);
    		m.invoke(apiObj, null);

    		m = apiClass.getMethod("getAPIVersion", null);
    		Object o = m.invoke(apiObj, null);
    		apiVersion = (String)o;

    	} catch (Exception e) {
    		logger.warn("Failed to load API subset", e);
    	}
    	
    }
    	
	public void run() {
		
		logger.info("Staring client server on port " + port + ", peer server on " + peerPort);
		
		ServerBootstrap bootstrap = new ServerBootstrap(
											new NioServerSocketChannelFactory(
													Executors.newCachedThreadPool(),
													Executors.newCachedThreadPool()));
				
		bootstrap.setOption("backlog", 1000);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ServerFactory());
		
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
		
		ServerBootstrap peerBootstrap = new ServerBootstrap(
											new NioServerSocketChannelFactory(
													Executors.newCachedThreadPool(),
													Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		peerBootstrap.setPipelineFactory(new PeerFactory());

		// Bind and start to accept incoming connections.
		peerBootstrap.bind(new InetSocketAddress(peerPort));	
	}
}
