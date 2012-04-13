package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Vertex

/**
 * Contains all of the detailed data for a NodeInstance
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 9:42 PM
 */
class NodeDataContainer() {
  private var _properties = new NodePropertiesContainer
  private var _incomingRelationships = new NodeRelationshipsContainer(RelationshipContainerType.Incoming)
  private var _outgoingRelationships = new NodeRelationshipsContainer(RelationshipContainerType.Outgoing)

  def load(v: Vertex){
    properties.load(v)
    incomingRelationships.load(v)
    outgoingRelationships.load(v)
  }

  /**
   * Copies this data into a new NodeDataContainer
   * @param destination
   */
  def copyTo(destination: NodeDataContainer): Unit = {
    destination._properties = new NodePropertiesContainer
    destination.properties.putAll(properties)
    destination._incomingRelationships = new NodeRelationshipsContainer(RelationshipContainerType.Incoming)
    destination.incomingRelationships.putAll(incomingRelationships)
    destination._outgoingRelationships = new NodeRelationshipsContainer(RelationshipContainerType.Outgoing)
    destination.outgoingRelationships.putAll(outgoingRelationships)
  }

  /**
   * Gets the properties for this node
   * @return the node's properties
   */
  def properties = _properties

  /**
   * Gets the incoming relationships for this node
   * @return the node's incoming relationships
   */
  def incomingRelationships = _incomingRelationships

  /**
   * Gets the outgoing relationships for this node
   * @return the node's outgoing relationships
   */
  def outgoingRelationships = _outgoingRelationships
}
