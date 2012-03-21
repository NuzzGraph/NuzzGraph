package nuzzgraph.server.core;

import com.orientechnologies.orient.client.remote.OEngineRemote;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;
        
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
            NodeController.setVertexClusterId(rawdb.getClusterIdByName("OGraphVertex"));
            NodeController.setEdgeClusterId(rawdb.getClusterIdByName("OGraphEdge"));
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
        final JettyServer server = new JettyServer(604);

        try
        {
            server.start();
            System.out.println("Server is ready.");
        }
        catch (Exception e)
        {
            System.out.println("Error starting server.");
        }
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

    /**
     * Gets a list of all classes existing in the database
     * @return A list of all classes, one per line
     */
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



