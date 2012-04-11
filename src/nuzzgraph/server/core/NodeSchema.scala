package nuzzgraph.server.core

/**
 * Defines a type of node
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 12:16 PM
 */
class NodeSchema {
  private[core] var name: String = null
  private[core] var functions: NodeFunctionsContainer = null
  private[core] var allowedIncomingRelationships: RelationshipTypeContainer = null
  private[core] var allowedOutgoingRelationships: RelationshipTypeContainer = null
}
