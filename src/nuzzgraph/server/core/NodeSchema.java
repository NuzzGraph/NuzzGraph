package nuzzgraph.server.core;

/**
 * Created by IntelliJ IDEA.
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class NodeSchema
{
    String name;
    NodeFunctionsContainer functions;
    RelationshipTypeContainer allowedIncomingRelationships;
    RelationshipTypeContainer allowedOutgoingRelationships;

}
