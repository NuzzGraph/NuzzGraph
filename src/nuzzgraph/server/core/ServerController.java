package nuzzgraph.server.core;

import com.orientechnologies.orient.client.remote.OEngineRemote;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;
        
public class ServerController 
{
	static OrientGraph graphdb;
    static ODatabaseDocumentTx documentDb;
	static ServerStatus status;
	static String orientLocation = "remote:localhost/nuzzgraph-test";
	
	public static void main(String[] args)
	{
        new eric.Console();
        startServer();
	}
	
	static void startServer()
	{
        initializeServer();
        mainLoop();
    }

	static void stopServer()
	{
        System.out.println("Shutting down server.");
        graphdb.shutdown();
	}

    /**
     * Connects to the graph DB at location and allows the API to be used
     * @param location
     */
    public static void ConnectToGraphDB(String location)
    {
        Orient.instance().registerEngine(new OEngineRemote());
        graphdb = new OrientGraph(orientLocation);
        NodeController.setVertexClusterId(graphdb.getRawGraph().getClusterIdByName("OGraphVertex"));
        NodeController.setEdgeClusterId(graphdb.getRawGraph().getClusterIdByName("OGraphEdge"));
    }

    /*
    * Gets the graph database for this instance.
    * @return The graph database for this instance.
    */
    public static OrientGraph getGraphDB()
    {
        return graphdb;
    }

    /**
     * Gets a list of all classes existing in the database
     * @return A list of all classes, one per line
     */
	public static String getAllClasses()
	{
        StringBuilder sBuilder = new StringBuilder();

        for (OClass c : graphdb.getRawGraph().getMetadata().getSchema().getClasses())
        {
            sBuilder.append(c.getName() + System.getProperty("line.separator"));
        }

        return sBuilder.toString();
	}

    private static void initializeServer()
    {
        System.out.println("Initializing server.");
        status = ServerStatus.Initializing;
        System.out.println("Connecting to OrientDB at " + orientLocation);
        try
        {
            ConnectToGraphDB(orientLocation);
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

    private static void beginHTTPListen()
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

    public static ODatabaseDocumentTx getDocumentDB()
    {
        return documentDb;
    }
}



