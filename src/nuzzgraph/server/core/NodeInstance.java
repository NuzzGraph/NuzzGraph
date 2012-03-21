package nuzzgraph.server.core;

import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.server.core.exception.ServerIntegrityException;

/**
 * Represents a fully operable node.  This object takes time to initialize.
 * Use NodeReference instead if you want to store a node placeholder quickly
 */
public class NodeInstance 
{
    long id;
    NodeDataContainer nodeData;
    NodeSchema nodeType;

    //NodeDataContainer originalNodeData;
    boolean changesMade;
    boolean accessed;

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
}

