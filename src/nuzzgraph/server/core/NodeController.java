/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nuzzgraph.server.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nuzzgraph.server.core.exception.ServerIntegrityException;

/**
 * The handler for the /node/ requests
 */
public class NodeController
{
    private static int vertexClusterId;
    private static int edgeClusterId;

    /**
     * Processes the incoming REST request and returns a text response
     * @param args An array containing first the node id, then the function name (and arguments, if any)
     * @return the text response for this request
     */
    public static String processRequest(String[] args)
    {
        String output;
        NodeInstance node;

        //args[0] = node id
        //args[1] = function name
        try
        {
            if (args == null || args.length < 1)
                return "";

            //get id from URL /node/id
            long id = Long.parseLong(args[0]);
    
            try
            {
                node = NodeInstance.get(id);
            }
            catch (ServerIntegrityException ex)  //problem with DB structure
            {
                output = ex.getMessage();
                return output;
            }
    
            try
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                output = gson.toJson(node).toString();
            }
            catch (Exception e) //Unable to display node
            {
                output = "Output is of type " + node.getClass() +
                    " but it cannot be displayed.";
                return output;
            }
        }
        catch (Exception e)
        {
            output = "Error processing request." + System.getProperty("line.separator") +
                    e.toString();
        }
        return output;
    }

    /**
     * During server initialization, sets the vertex cluster ID
     * See http://code.google.com/p/orient/wiki/Concepts#Cluster
     * @param id the id of the vertex cluster
     */
    public static void setVertexClusterId(int id)
    {
        NodeController.vertexClusterId = id;
    }

    /**
     * During server initialization, sets the edge cluster ID
     * See http://code.google.com/p/orient/wiki/Concepts#Cluster
     * @param id the id of the edge cluster
     */
    public static void setEdgeClusterId(int id)
    {
        NodeController.edgeClusterId = id;
    }

    /**
     * Gets the ID of the underlying vertex cluster
     * @return the ID of the underlying vertex cluster
     */
    public static int getVertexClusterId()
    {
        return vertexClusterId;
    }

    /**
     * Gets the ID of the underlying edge cluster
     * @return the ID of the underlying edge cluster
     */
    public static int getEdgeClusterId()
    {
        return edgeClusterId;
    }
        
}
