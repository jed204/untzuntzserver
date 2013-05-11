package com.untzuntz.ustackserver;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.mongodb.DBObject;
import com.untzuntz.ustack.aaa.ResourceDefinition;
import com.untzuntz.ustack.aaa.ResourceLink;
import com.untzuntz.ustack.aaa.RoleDefinition;
import com.untzuntz.ustack.data.APIClient;
import com.untzuntz.ustackserver.server.ServerFactory;
import com.untzuntz.ustackserverapi.util.APIPerms;

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
    	
    public static ResourceDefinition setupAPIResource() throws Exception
    {
    	ResourceDefinition rs = ResourceDefinition.getByInternalName("API");
		
		if (rs == null)
		{
			rs = ResourceDefinition.createResource("API", ResourceDefinition.TYPE_APIACCESS);
			rs.setInternalName("API");
			
			RoleDefinition role = new RoleDefinition("API Manager");
			role.addPermission(APIPerms.APIClientManager.getPermission());
			role.addPermission(APIPerms.APIResourceManager.getPermission());
			role.addPermission(APIPerms.APIRoleManager.getPermission());
			rs.addRole(role);
			rs.save("Initial Run");
		}

		return rs;
    }
    
	public void run() throws Exception {
		
		// check if we have at least one API client
		long clientCnt = APIClient.getDBCollection().count();
		if (clientCnt == 0)
		{
			// create initial API client with rights to manage resources and other API clients
			APIClient client = APIClient.createAPI("Initial Run", "admin-api");
			
			// add rights
			ResourceDefinition rs = setupAPIResource();
			
			ResourceLink rl = new ResourceLink(rs, "API Manager");
			client.addResourceLink(rl);
			client.save("Initial Run");
			
			DBObject k = (DBObject)client.getAPIKeys().get(0);
			
			// output key to log for user to grab
 			logger.info(String.format("Initial API Client/Key: %s/%s", client.getClientId(), client.getKey((String)k.get("uid"))));
		}
		
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
