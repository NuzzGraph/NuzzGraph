package nuzzgraph.server.core

import scala.collection.JavaConversions._
import Implicits._

import java.util.HashMap
import nuzzgraph.dbclient.NodeHelper
import RelationshipContainerType._
import com.tinkerpop.blueprints.pgm.{Vertex, Edge}


/*
  Contains a list of relationships for a node
  Key: Relationship Type
  Value: NodeCollection representing list of nodes
 */
class NodeRelationshipsContainer(containerType: RelationshipContainerType) extends HashMap[String, NodeCollection] {

  def load(v: Vertex){
    val edges = if (containerType eq RelationshipContainerType.Incoming) v.getInEdges() else v.getOutEdges()

//    edges.foreach{ e =>
    for (e <- edges) yield
    {
      if (!containsKey(e.getLabel)) //Add new key if it does not exist
        put(e.getLabel, new NodeCollection)

      if (containerType eq RelationshipContainerType.Incoming)
        get(e.getLabel).add(new NodeReference(e.getOutVertex.nuzzGraphId))

      else if (containerType eq RelationshipContainerType.Outgoing)
        get(e.getLabel).add(new NodeReference(e.getInVertex.nuzzGraphId))
    }
  }
}

