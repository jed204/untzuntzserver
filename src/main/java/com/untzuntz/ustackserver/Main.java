package com.untzuntz.ustackserver;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.untzuntz.ustackserver.server.ServerFactory;

public class Main {

    static Logger           		logger               	= Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("log4j.xml");
    	int port = 8081;
    	
    	if (args.length >= 1)
    		port = Integer.valueOf(args[1]);
    	
    	Main main = new Main(args[0], port);
    	
		main.run();
    }

    private int port;
    private String apiVersion;
    
    public String getAPIVersion() {
    	return apiVersion;
    }
    
    public Main(String code, int p) {

    	port = p;
    	
    	try {
    		
			Class<?> apiClass = Class.forName(code);
			Object apiObj = apiClass.newInstance();
				
    		Method m = apiClass.getMethod("setup");
    		m.invoke(apiObj, (Object[])null);

    		m = apiClass.getMethod("getAPIVersion");
    		
    		Object o = m.invoke(apiObj);
    		apiVersion = (String)o;

    	} catch (Exception e) {
    		logger.warn("Failed to load API subset", e);
    	}
    	
    }
    	
	public void run() {
		
		logger.info("Staring client server on port " + port);
		
		ServerBootstrap bootstrap = new ServerBootstrap(
											new NioServerSocketChannelFactory(
													Executors.newCachedThreadPool(),
													Executors.newCachedThreadPool()));
				
		bootstrap.setOption("backlog", 1000);
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ServerFactory());
		
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
	}
}
