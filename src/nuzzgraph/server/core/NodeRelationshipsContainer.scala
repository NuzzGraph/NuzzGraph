package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Edge
import java.util.HashMap

/**
 * Contains a list of relationships for a node
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:04 PM
 */
object NodeRelationshipsContainer {

  private[core] final object RelationshipContainerType extends Enumeration {
    val Incoming = Value("Incoming")
    val Outgoing = Value("Outgoing")
  }
}

class NodeRelationshipsContainer extends HashMap[String, NodeCollection] {
  def this() {
    this()
    `super`
  }

  def this(relationships: Iterable[Edge], containerType: NodeRelationshipsContainer.RelationshipContainerType) {
    this()
    `super`
    import scala.collection.JavaConversions._
    for (relationship <- relationships) {
      if (!containsKey(relationship.getLabel)) put(relationship.getLabel, new NodeCollection)
      if (containerType eq RelationshipContainerType.Incoming) get(relationship.getLabel).add(new NodeReference(Long.valueOf(relationship.getOutVertex.getId.toString)))
      if (containerType eq RelationshipContainerType.Outgoing) get(relationship.getLabel).add(new NodeReference(Long.valueOf(relationship.getInVertex.getId.toString)))
    }
  }
}
