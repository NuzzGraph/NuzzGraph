package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Edge;

import java.util.HashMap;

/**
 * Contains a list of relationships for a node
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:04 PM
 */
public class NodeRelationshipsContainer extends HashMap<String, NodeCollection>
{
    public NodeRelationshipsContainer()
    {
        super();
    }

    public NodeRelationshipsContainer(Iterable<Edge> relationships, RelationshipContainerType containerType)
    {
        super();
        for (Edge relationship : relationships)
        {
            if (!containsKey(relationship.getLabel()))
                put(relationship.getLabel(), new NodeCollection());
            
            if (containerType == RelationshipContainerType.Incoming)
                get(relationship.getLabel()).add(new NodeReference(Long.valueOf(relationship.getOutVertex().getId().toString())));
            if (containerType == RelationshipContainerType.Outgoing)
                get(relationship.getLabel()).add(new NodeReference(Long.valueOf(relationship.getInVertex().getId().toString())));
        }
    }

    enum RelationshipContainerType
    {
        Incoming,
        Outgoing
    }
}
