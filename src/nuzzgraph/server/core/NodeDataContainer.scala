package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Vertex

/**
 * Contains all of the detailed data for a NodeInstance
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 9:42 PM
 */
class NodeDataContainer {
  def this() {
    this()
    properties = new NodePropertiesContainer
    incomingRelationships = new NodeRelationshipsContainer
    outgoingRelationships = new NodeRelationshipsContainer
  }

  /**
   * Creates a new NodeDataContainer
   * @param v The underlying Vertex object associated with this node
   */
  def this(v: Vertex) {
    this()
    if (v != null) {
      properties = new NodePropertiesContainer(v)
      incomingRelationships = new NodeRelationshipsContainer(v.getInEdges, NodeRelationshipsContainer.RelationshipContainerType.Incoming)
      outgoingRelationships = new NodeRelationshipsContainer(v.getOutEdges, NodeRelationshipsContainer.RelationshipContainerType.Outgoing)
    }
  }

  /**
   * Copies this data into a new NodeDataContainer
   * @param destination
   */
  def copyTo(destination: NodeDataContainer): Unit = {
    destination.properties = new NodePropertiesContainer
    destination.properties.putAll(properties)
    destination.incomingRelationships = new NodeRelationshipsContainer
    destination.incomingRelationships.putAll(incomingRelationships)
    destination.outgoingRelationships = new NodeRelationshipsContainer
    destination.outgoingRelationships.putAll(outgoingRelationships)
  }

  /**
   * Gets the properties for this node
   * @return the node's properties
   */
  def getProperties: NodePropertiesContainer = {
    return properties
  }

  /**
   * Gets the incoming relationships for this node
   * @return the node's incoming relationships
   */
  def getIncomingRelationships: NodeRelationshipsContainer = {
    return incomingRelationships
  }

  /**
   * Gets the outgoing relationships for this node
   * @return the node's outgoing relationships
   */
  def getOutgoingRelationships: NodeRelationshipsContainer = {
    return outgoingRelationships
  }

  private[core] var properties: NodePropertiesContainer = null
  private[core] var incomingRelationships: NodeRelationshipsContainer = null
  private[core] var outgoingRelationships: NodeRelationshipsContainer = null
}
