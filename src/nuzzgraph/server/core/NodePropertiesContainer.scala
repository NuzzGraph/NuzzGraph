package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Vertex
import java.util.HashMap

/**
 * Contains all the properties belonging to a node.
 * Key = Property Name
 * Value = Property Value
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */
class NodePropertiesContainer extends HashMap[String, String] {
  def this() {
    this()
    `super`
  }

  def this(v: Vertex) {
    this()
    `super`
    import scala.collection.JavaConversions._
    for (key <- v.getPropertyKeys) {
      super.put(key, v.getProperty(key).asInstanceOf[String])
    }
  }
}
