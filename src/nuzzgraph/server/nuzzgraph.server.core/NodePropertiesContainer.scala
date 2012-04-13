package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Vertex
import java.util.HashMap
import scala.collection.JavaConversions._

/**
 * Contains all the properties belonging to a node.
 * Key = Property Name
 * Value = Property Value
 * User: Mark Nuzzolilo
 * Date: 3/19/12
 * Time: 10:05 PM
 */
class NodePropertiesContainer() extends HashMap[String, String] {
  def load(v: Vertex)
  {
    for (key <- v.getPropertyKeys)
    {
      super.put(key, v.getProperty(key).asInstanceOf[String])
    }
  }
}
