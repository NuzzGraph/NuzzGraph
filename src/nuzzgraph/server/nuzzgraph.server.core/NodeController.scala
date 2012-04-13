/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nuzzgraph.server.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * The handler for the /node/ requests
 */
object NodeController {
  /**
   * Processes the incoming REST request and returns a text response
   * @param args An array containing first the node id, then the function name (and arguments, if any)
   * @return the text response for this request
   */
  def processRequest(args: Array[String]): String = {
    var output: String = null
    var node: NodeInstance = null
    try {
      if (args == null || args.length < 1) return ""
      var id: Long = args(0).toInt
      try {
        node = NodeInstance.get(id)
      }
      catch {
        case ex: Exception => {
          output = ex.getMessage
          return output
        }
      }
      try {
        var gson: Gson = new GsonBuilder().setPrettyPrinting.create
        output = gson.toJson(node).toString
      }
      catch {
        case e: Exception => {
          output = "Output is of type " + node.getClass + " but it cannot be displayed."
          return output
        }
      }
    }
    catch {
      case e: Exception => {
        output = "Error processing request." + System.getProperty("line.separator") + e.toString
      }
    }
    return output
  }

  /**
   * During server initialization, sets the vertex cluster ID
   * See http://code.google.com/p/orient/wiki/Concepts#Cluster
   * @param id the id of the vertex cluster
   */
  def setVertexClusterId(id: Int): Unit = {
    NodeController.vertexClusterId = id
  }

  /**
   * During server initialization, sets the edge cluster ID
   * See http://code.google.com/p/orient/wiki/Concepts#Cluster
   * @param id the id of the edge cluster
   */
  def setEdgeClusterId(id: Int): Unit = {
    NodeController.edgeClusterId = id
  }

  /**
   * Gets the ID of the underlying vertex cluster
   * @return the ID of the underlying vertex cluster
   */
  def getVertexClusterId: Int = {
    return vertexClusterId
  }

  /**
   * Gets the ID of the underlying edge cluster
   * @return the ID of the underlying edge cluster
   */
  def getEdgeClusterId: Int = {
    return edgeClusterId
  }

  private var vertexClusterId: Int = 0
  private var edgeClusterId: Int = 0
}
