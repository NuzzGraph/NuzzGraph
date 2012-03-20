package nuzzgraph.server.core;

import com.tinkerpop.blueprints.pgm.Edge;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
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
