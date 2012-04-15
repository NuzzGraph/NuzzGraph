package nuzzgraph.dbclient.main

import com.tinkerpop.blueprints.pgm.Vertex

/**
 * User: Mark Nuzzolilo
 * Date: 3/21/12
 * Time: 1:18 AM
 */
object NodeHelper {
  def getNodeId(v: Vertex): Int = {
    var vId: String = v.getId.toString
    return Integer.parseInt(vId.substring(vId.indexOf(":") + 1))
  }

  def getVertexId(nodeId: Int): String = {
    return "#" + vertexClusterId + ":" + nodeId
  }

  private[dbclient] var vertexClusterId: Int = 0
}
