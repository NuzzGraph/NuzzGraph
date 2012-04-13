package nuzzgraph.server.core

import com.orientechnologies.orient.core.exception.ODatabaseException
import com.tinkerpop.blueprints.pgm.TransactionalGraph
import com.tinkerpop.blueprints.pgm.Vertex
import exception.ServerIntegrityException
import java.util.HashMap

/**
 * Represents a fully operable node.  This object takes time to initialize.
 * Use NodeReference instead if you want to store a node placeholder quickly
 */
object NodeInstance {
  /**
   * Returns an instance of the node
   * @param id  the id of the node
   * @return an instance of the node
   */
  def get(id: Long): NodeInstance = {
    var internal_id: String = NodeController.getVertexClusterId + ":" + id
    var v: Vertex = null
    try {
      v = ServerController.getGraphDB.getVertex(internal_id)
    }
    catch {
      case e: ODatabaseException => {
        v = null
      }
    }
    if (v == null) throw new NullPointerException("Node with ID " + id + " was not found.")
    var n: NodeInstance = new NodeInstance(v)
    n.id = id
    n.nodeData = new NodeDataContainer
    n.nodeData.load(v)
    n.internalData = v
    if (n.nodeData.outgoingRelationships.containsKey("IsNodeType")) {
      if (n.nodeData.outgoingRelationships.get("IsNodeType").size != 1) throw new ServerIntegrityException("Expected 1 NodeType for node " + id)
      var nodeTypeNode: NodeReference = n.nodeData.outgoingRelationships.get("IsNodeType").get(0)
    }
    return n
  }
}

class NodeInstance {
  private def this(v: Vertex) {
    this()
  }

  /**
   * Gets the node's ID
   * @return the node's ID
   */
  def getId: Long = {
    return id
  }

  /**
   * Gets the type information for this node
   * @return the type information for this node
   */
  def getNodeType: NodeSchema = {
    return nodeType
  }

  /**
   * Gets the node's data
   * @return the node's data
   */
  def getNodeData: NodeDataContainer = {
    return nodeData
  }

  def getInternalData: Vertex = {
    return internalData
  }

  def uploadDataForSave(properties: HashMap[String, String]): Unit = {
    beginUploadData
    newNodeData.properties.putAll(properties)
  }

  /**
   * Initializes changed data objects if necessary
   */
  private def beginUploadData: Unit = {
    if (!changesMade) {
      changesMade = true
      newNodeData = new NodeDataContainer
    }
  }

  /**
   * Saves the node.  Data must be uploaded for saving first
   * @throws Exception
   */
  def saveNode: Unit = {
    if (!changesMade) return
    ServerController.getGraphDB.startTransaction
    try {
      import scala.collection.JavaConversions._
      for (propertyKey <- internalData.getPropertyKeys) internalData.removeProperty(propertyKey)
      import scala.collection.JavaConversions._
      for (propertyKey <- newNodeData.properties.keySet) internalData.setProperty(propertyKey, newNodeData.properties.get(propertyKey))
      ServerController.getGraphDB.stopTransaction(TransactionalGraph.Conclusion.SUCCESS)
      nodeData = newNodeData
      newNodeData = null
      changesMade = false
    }
    catch {
      case e: Exception => {
        ServerController.getGraphDB.stopTransaction(TransactionalGraph.Conclusion.FAILURE)
      }
    }
  }

  private[core] var id: Long = 0L
  private[core] var nodeData: NodeDataContainer = null
  private[core] var nodeType: NodeSchema = null
  private[core] var internalData: Vertex = null
  private[core] var changesMade: Boolean = false
  private[core] var newNodeData: NodeDataContainer = null
}
