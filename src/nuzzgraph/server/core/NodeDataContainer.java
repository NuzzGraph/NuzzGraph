package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 9:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeDataContainer
{
    NodePropertiesContainer properties;
    NodeRelationshipsContainer incomingRelationships;
    NodeRelationshipsContainer outgoingRelationships;

    public NodeDataContainer()
    {
    }

    public NodeDataContainer(Vertex v)
    {
        if (v != null)
        {
            properties = new NodePropertiesContainer(v);
            incomingRelationships = new NodeRelationshipsContainer(v.getInEdges(), NodeRelationshipsContainer.RelationshipContainerType.Incoming);
            outgoingRelationships = new NodeRelationshipsContainer(v.getOutEdges(), NodeRelationshipsContainer.RelationshipContainerType.Outgoing);
        }
    }

    public void CopyTo(NodeDataContainer destination)
    {
        destination.properties = new NodePropertiesContainer();
        destination.properties.putAll(properties);
        
        destination.incomingRelationships = new NodeRelationshipsContainer();
        destination.incomingRelationships.putAll(incomingRelationships);

        destination.outgoingRelationships = new NodeRelationshipsContainer();
        destination.outgoingRelationships.putAll(outgoingRelationships);
    }

    public NodePropertiesContainer getProperties()
    {
        return properties;
    }

    public NodeRelationshipsContainer getIncomingRelationships()
    {
        return incomingRelationships;
    }

    public NodeRelationshipsContainer getOutgoingRelationships()
    {
        return outgoingRelationships;
    }
}
