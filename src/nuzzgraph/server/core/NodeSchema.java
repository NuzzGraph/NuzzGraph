package nuzzgraph.server.core;

/**
 * Defines a type of node
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 12:16 PM
 */
public class NodeSchema
{
    String name;
    NodeFunctionsContainer functions;
    RelationshipTypeContainer allowedIncomingRelationships;
    RelationshipTypeContainer allowedOutgoingRelationships;

}
