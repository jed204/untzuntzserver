package com.untzuntz.ustackserver.loadtest;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class RunTest {

    static Logger           		logger               	= Logger.getLogger(RunTest.class);

//	private final static int TIMEOUT_MS = 45*1000;
//	private final static int INTERVAL_MS = 1* 1000;
	
	private long totalTime;
	private long totalConnTime;
	private int totalCount;
	private long start;
	
	public synchronized void add(int cnt, long time, long connTime)
	{
		totalTime += time;
		totalConnTime += connTime;
		totalCount += cnt;

		if (totalCount % 100 == 0)
		{
			long runTime = System.currentTimeMillis() - start;
			long rps = (long)((float)totalCount / (float)((float)runTime / 1000.0f));
			long avg = (long)((float)totalTime / (float)totalCount);
			long cavg = (long)((float)totalConnTime / (float)totalCount);
			logger.info(runTime + " | " + rps + " req/sec : " + totalCount + " @ " + avg + " ms average (connection time avg: " + cavg + "ms)");
			if (runTime > 300000)
				System.exit(0);
		}		
	}
	
	class Worker extends Thread implements Runnable{

		private int count;
		private long total;
		private long ctotal;
		private boolean stop;
//		private ClientBootstrap bootstrap;
//		private InetSocketAddress server;
		public Worker(InetSocketAddress s, ClientBootstrap bs) {
//			server = s;
//			bootstrap = bs;
			stop = false;
		}
		
		@Override
		public void run() {

			URL url = null;
			try {
				url = new URL("http://ifc4.cvg.intelegrid.net/api/music/status");
//				url = new URL("http://ifc4.cvg.intelegrid.net/index.html");
			} catch (Exception e) {}
			
			while (!stop)
			{
				try{
					
					long cstart = System.currentTimeMillis();
					long st = System.currentTimeMillis();
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();

//					ChannelFuture future = bootstrap.connect(server);
//					
//					Channel channel = future.awaitUninterruptibly().getChannel();
//					if (!future.isSuccess()) {
//						bootstrap.releaseExternalResources();
//						throw future.getCause();
//					}
					long cend = System.currentTimeMillis() - cstart;
	
//					HttpRequest request = new DefaultHttpRequest(
//					HttpVersion.HTTP_1_1, HttpMethod.GET, "/api/music/status");
//					request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
//	
//					channel.write(request);
//					
//					// Wait for the server to close the connection.
//					channel.getCloseFuture().awaitUninterruptibly();
					if (conn.getResponseCode() == 200)
					{
						long tot = (System.currentTimeMillis() - st);
						count++;
						total += tot;
						ctotal += cend;
						
						if (count == 10)
						{
							add(count, total, ctotal);
							total = 0;
							count = 0;
						}
						conn.disconnect();
					}
					else
						logger.warn("Invalid Response");
					
	//				long avg = (long)((float)total / (float)count);
	//				logger.info("SUCCESS! " + tot + " ms : " + count + " @ " + avg + " ms average");
	
					
					// Shut down executor threads to exit.
					//bootstrap.releaseExternalResources();
					
				} catch (Throwable e){
					logger.error("Error while getting status from client", e);
				}
			}		
		}		
	}
	
	public static void main(String[] args){

		int runThreads = 100;
		if (args.length >= 1)
			runThreads = Integer.valueOf(args[0]);
		
        RunTest rt = new RunTest();
        rt.go(runThreads);
        
	}
	
	private void go(int runThreads)
	{
		DOMConfigurator.configure("log4j.xml");
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new HttpClientPipelineFactory());
		bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", false);
        bootstrap.setOption("connectTimeoutMillis", 20000);

//		ScheduledExecutorService service = Executors.newScheduledThreadPool(runThreads);
		start = System.currentTimeMillis();
		InetSocketAddress serverAddr = new InetSocketAddress("twf-api1.untzuntz.com", 80);
		//Set the requesters
		for (int i = 1; i <= runThreads; i++){
			Worker W = new Worker(serverAddr, bootstrap);
			W.start();
			
			if (i % 50 == 0)
				logger.info("Created " + i + " threads...");
		}
		
	}
	
}
