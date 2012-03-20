package nuzzgraph.server.core;

import com.orientechnologies.orient.client.remote.*;
import com.orientechnologies.orient.core.*;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.engine.*;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.tinkerpop.blueprints.pgm.*;
import com.tinkerpop.blueprints.pgm.impls.orientdb.*;
import eric.Console;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
        
public class ServerController 
{
	static OrientGraph graphdb;
	static OGraphDatabase rawdb;
	static ServerStatus status;
	static String orientLocation = "remote:localhost/nuzzgraph-test";
	
	public static void main(String[] args)
	{
            new eric.Console();
            startServer();
	}
	
	public static void startServer()
	{
            initializeServer();
            mainLoop();
        }
	
	public static void stopServer()
	{
            System.out.println("Shutting down server.");
            graphdb.shutdown();
	}
	
	private static void initializeServer()
	{
            System.out.println("Initializing server.");
            status = ServerStatus.Initializing;

            System.out.println("Connecting to OrientDB at " + orientLocation);
            try
            {
                Orient.instance().registerEngine(new OEngineRemote());
                graphdb = new OrientGraph(orientLocation);
                rawdb = graphdb.getRawGraph();
            }
            catch (Exception e)
            {
                status = ServerStatus.Failed;
                System.out.println("Unable to connect to OrientDB");
                return;
            }

            beginHTTPListen();
            status = ServerStatus.Available;
            
	}
	
	private static void mainLoop() 
	{
            try
            {
                while(status == ServerStatus.Available)
                {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e)
            {
                stopServer();
            }
	}
	
	static void beginHTTPListen()
	{
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] 
                            {new AppContextBuilder().buildWebAppContext()});
            final JettyServer server = new JettyServer(604);
            //server.setHandler(contexts);
            try
            {
                server.start();
                System.out.println("Server is ready.");
            }
            catch (Exception e)
            {
                System.out.println("Error starting server.");
            }
            
            /*
            Runnable runner = new Runnable() {
                    @Override
                    public void run(){
                            new ServerRunner(server);
                    }
            }

            EventQueue.invokeLater(runner);
	*/
	}
	
        public static OrientGraph getGraphDB()
        {
            return graphdb;
        }
        
        /*
         * Gets the raw graph database for this instance.
         * @return The raw graph database for this instance.
         */
        public static OGraphDatabase getGraphDBRaw()
        {
            return rawdb;
        }
        
	public static String getAllClasses()
	{
            StringBuilder sBuilder = new StringBuilder();

            for (OClass c : rawdb.getMetadata().getSchema().getClasses())
            {
                sBuilder.append(c.getName() + System.getProperty("line.separator"));
            }

            return sBuilder.toString();		  
	}
}



