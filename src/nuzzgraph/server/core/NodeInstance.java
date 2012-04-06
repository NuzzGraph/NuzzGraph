package nuzzgraph.server.core;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.server.core.exception.ServerIntegrityException;

import java.util.HashMap;

/**
 * Represents a fully operable node.  This object takes time to initialize.
 * Use NodeReference instead if you want to store a node placeholder quickly
 */
public class NodeInstance 
{
    //data components
    long id;
    NodeDataContainer nodeData;
    NodeSchema nodeType;

    //internal data for underlying data store
    Vertex internalData;

    //data for committing
    boolean changesMade;
    NodeDataContainer newNodeData;

    private NodeInstance(Vertex v)
    {

    }

    /**
     * Returns an instance of the node
     * @param id  the id of the node
     * @return an instance of the node
     */
    public static NodeInstance get(long id)
        throws ServerIntegrityException, NullPointerException, ODatabaseException
    {
        String internal_id = NodeController.getVertexClusterId() + ":" + id;
        Vertex v;

        try
        {
            v = ServerController.getGraphDB().getVertex(internal_id);
        }
        catch (ODatabaseException e)
        {
            v = null;
        }

        if (v == null)
            throw new NullPointerException("Node with ID " + id + " was not found.");
        
        NodeInstance n = new NodeInstance(v);
        n.id = id;
        n.nodeData = new NodeDataContainer(v);
        n.internalData = v;
        
        if (n.nodeData.getOutgoingRelationships().containsKey("IsNodeType"))
        {
            if (n.nodeData.getOutgoingRelationships().get("IsNodeType").size() != 1)
                throw new ServerIntegrityException("Expected 1 NodeType for node " + id);
            NodeReference nodeTypeNode = n.nodeData.getOutgoingRelationships().get("IsNodeType").get(0);
            //nodeType = new NodeSchema(nodeTypeNode); //hack: inefficient and will need serious re-factor
        }

        return n;
    }

    /**
     * Gets the node's ID
     * @return the node's ID
     */
    public long getId()
    {
        return id;
    }

    /**
     * Gets the type information for this node
     * @return the type information for this node
     */
    public NodeSchema getNodeType()
    {
        return nodeType;
    }

    /**
     * Gets the node's data
     * @return the node's data
     */
    public NodeDataContainer getNodeData()
    {
        return nodeData;
    }

    public Vertex getInternalData()
    {
        return internalData;
    }

    public void uploadDataForSave(HashMap<String, String> properties)
    {
        beginUploadData();
        newNodeData.getProperties().putAll(properties);
    }

    /**
     * Initializes changed data objects if necessary
     */
    private void beginUploadData()
    {
        if (!changesMade)
        {
            changesMade = true;
            newNodeData = new NodeDataContainer();
        }
    }

    /**
     * Saves the node.  Data must be uploaded for saving first
     * @throws Exception
     */
    public void saveNode() throws Exception
    {
        if (!changesMade)
            return;

        //Create transaction
        ServerController.getGraphDB().startTransaction();

        try
        {
            //Delete all properties
            for(String propertyKey : internalData.getPropertyKeys())
                internalData.removeProperty(propertyKey);

            //Add all properties
            for (String propertyKey : newNodeData.getProperties().keySet())
                internalData.setProperty(propertyKey, newNodeData.getProperties().get(propertyKey));

            //End Transaction
            ServerController.getGraphDB().stopTransaction(TransactionalGraph.Conclusion.SUCCESS);

            //Reset node status
            nodeData = newNodeData;
            newNodeData = null;
            changesMade = false;
        }
        catch (Exception e)
        {
            ServerController.getGraphDB().stopTransaction(TransactionalGraph.Conclusion.FAILURE);
        }
    }

    /**
     * Returns this object in Json format.
     * Formatted for OrientDB
     * @return This object serialized in Json format, for OrientDB
     */
    /*
    public String toJson_Orient()
    {
        Gson gson = new Gson();
        //gson.toJson(new JsonElement());
        JsonObject jo = new JsonObject();
        jo.addProperty("@rid", getInternalData().getId().toString());
        jo.addProperty("@class", getInternalData().getClass().getName());
        jo.addProperty("@version", getInternalData().);


        String j = getInternalData()
    }
    */
}

