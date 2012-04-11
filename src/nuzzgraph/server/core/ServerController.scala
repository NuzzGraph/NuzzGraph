package nuzzgraph.server.core

import com.orientechnologies.orient.client.remote.OEngineRemote
import com.orientechnologies.orient.core.Orient
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.metadata.schema.OClass
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph

object ServerController {
  def main(args: Array[String]): Unit = {
    new Console
    startServer
  }

  private[core] def startServer: Unit = {
    initializeServer
    mainLoop
  }

  private[core] def stopServer: Unit = {
    System.out.println("Shutting down server.")
    graphdb.shutdown
  }

  /**
   * Connects to the graph DB at location and allows the API to be used
   * @param location
   */
  def ConnectToGraphDB(location: String): Unit = {
    Orient.instance.registerEngine(new OEngineRemote)
    graphdb = new OrientGraph(orientLocation)
    NodeController.setVertexClusterId(graphdb.getRawGraph.getClusterIdByName("OGraphVertex"))
    NodeController.setEdgeClusterId(graphdb.getRawGraph.getClusterIdByName("OGraphEdge"))
  }

  def getGraphDB: OrientGraph = {
    return graphdb
  }

  /**
   * Gets a list of all classes existing in the database
   * @return A list of all classes, one per line
   */
  def getAllClasses: String = {
    var sBuilder: StringBuilder = new StringBuilder
    import scala.collection.JavaConversions._
    for (c <- graphdb.getRawGraph.getMetadata.getSchema.getClasses) {
      sBuilder.append(c.getName + System.getProperty("line.separator"))
    }
    return sBuilder.toString
  }

  private def initializeServer: Unit = {
    System.out.println("Initializing server.")
    status = ServerStatus.Initializing
    System.out.println("Connecting to OrientDB at " + orientLocation)
    try {
      ConnectToGraphDB(orientLocation)
    }
    catch {
      case e: Exception => {
        status = ServerStatus.Failed
        System.out.println("Unable to connect to OrientDB")
        return
      }
    }
    beginHTTPListen
    status = ServerStatus.Available
  }

  private def mainLoop: Unit = {
    try {
      while (status eq ServerStatus.Available) {
        Thread.sleep(1000)
      }
    }
    catch {
      case e: InterruptedException => {
        stopServer
      }
    }
  }

  private def beginHTTPListen: Unit = {
    val server: JettyServer = new JettyServer(604)
    try {
      server.start
      System.out.println("Server is ready.")
    }
    catch {
      case e: Exception => {
        System.out.println("Error starting server.")
      }
    }
  }

  def getDocumentDB: ODatabaseDocumentTx = {
    return documentDb
  }

  def getStatus: ServerStatus = {
    return status
  }

  private[core] var graphdb: OrientGraph = null
  private[core] var documentDb: ODatabaseDocumentTx = null
  private[core] var status: ServerStatus = null
  private[core] var orientLocation: String = "remote:localhost/nuzzgraph-test"
}
