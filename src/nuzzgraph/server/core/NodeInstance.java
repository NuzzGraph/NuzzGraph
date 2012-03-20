package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.server.core.exception.ServerIntegrityException;

public class NodeInstance 
{
    Long id;
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
        throws ServerIntegrityException, NullPointerException
    {
        Vertex v = ServerController.getGraphDB().getVertex(id);
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

        //n.originalNodeData = new NodeDataContainer();
        //n.nodeData.CopyTo(n.originalNodeData);

        return n;
    }

    public long getId()
    {
        return id;
    }

    public NodeSchema getNodeType()
    {
        return nodeType;
    }

    public NodeDataContainer getNodeData()
    {
        return nodeData;
    }
}

