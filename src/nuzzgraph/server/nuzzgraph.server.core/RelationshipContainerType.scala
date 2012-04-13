package nuzzgraph.server.core

/**
 * Incoming or outgoing
 * User: Mark Nuzzolilo
 * Date: 4/12/12
 * Time: 8:50 PM
 */
object RelationshipContainerType extends Enumeration {
  type RelationshipContainerType = Value
  val Incoming = Value("Incoming")
  val Outgoing = Value("Outgoing")
}
