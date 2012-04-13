package nuzzgraph.server.core

import com.tinkerpop.blueprints.pgm.Vertex
import java.lang.{Iterable => JavaItb}
import java.util.{Iterator => JavaItr}


/**
 * Implicit type conversions. Allows us to use "extension methods"
 */
object Implicits {
  implicit def VertexExtensions(v : Vertex) = new {
    def nuzzGraphId : Int = {
      //Vertex id is "#schemaid:NuzzGraphId"
      var vId: String = v.getId.toString
      //Get everything to the right of ":"
      return Integer.parseInt(vId.substring(vId.indexOf(":") + 1))
    }
  }

  implicit def NodeReferenceExtensions(){}

}
