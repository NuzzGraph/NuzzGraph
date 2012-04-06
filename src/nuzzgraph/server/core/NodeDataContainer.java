package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Contains all of the detailed data for a NodeInstance
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 9:42 PM
 */
public class NodeDataContainer
{
    NodePropertiesContainer properties;
    NodeRelationshipsContainer incomingRelationships;
    NodeRelationshipsContainer outgoingRelationships;

    public NodeDataContainer()
    {
        properties = new NodePropertiesContainer();
        incomingRelationships = new NodeRelationshipsContainer();
        outgoingRelationships = new NodeRelationshipsContainer();
    }

    /**
     * Creates a new NodeDataContainer
     * @param v The underlying Vertex object associated with this node
     */
    public NodeDataContainer(Vertex v)
    {
        if (v != null)
        {
            properties = new NodePropertiesContainer(v);
            incomingRelationships = new NodeRelationshipsContainer(v.getInEdges(), NodeRelationshipsContainer.RelationshipContainerType.Incoming);
            outgoingRelationships = new NodeRelationshipsContainer(v.getOutEdges(), NodeRelationshipsContainer.RelationshipContainerType.Outgoing);
        }
    }

    /**
     * Copies this data into a new NodeDataContainer
     * @param destination
     */
    public void copyTo(NodeDataContainer destination)
    {
        destination.properties = new NodePropertiesContainer();
        destination.properties.putAll(properties);
        
        destination.incomingRelationships = new NodeRelationshipsContainer();
        destination.incomingRelationships.putAll(incomingRelationships);

        destination.outgoingRelationships = new NodeRelationshipsContainer();
        destination.outgoingRelationships.putAll(outgoingRelationships);
    }

    /**
     * Gets the properties for this node
     * @return the node's properties
     */
    public NodePropertiesContainer getProperties()
    {
        return properties;
    }

    /**
     * Gets the incoming relationships for this node
     * @return the node's incoming relationships
     */
    public NodeRelationshipsContainer getIncomingRelationships()
    {
        return incomingRelationships;
    }

    /**
     * Gets the outgoing relationships for this node
     * @return the node's outgoing relationships
     */
    public NodeRelationshipsContainer getOutgoingRelationships()
    {
        return outgoingRelationships;
    }
}
