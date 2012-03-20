package nuzzgraph.server.core;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.Handler;

public class JettyServer 
{

	private Server server;
	private ServerHandler handler;
        
	public JettyServer() 
	{
            this(604);
	}
	public JettyServer(Integer runningPort) 
	{
            server = new Server(runningPort);
            handler = new ServerHandler();
            server.setHandler(handler);
	}
	
	public void setHandler(ContextHandlerCollection contexts) 
	{
            server.setHandler(contexts);
	}
	
	public void start() throws Exception
	{
            server.start();
	}
	
	public void stop() throws Exception 
	{
            server.stop();
            server.join();
	}
	
	public boolean isStarted() 
	{
            return server.isStarted();
	}
	
	public boolean isStopped() 
	{
            return server.isStopped();
	}
}