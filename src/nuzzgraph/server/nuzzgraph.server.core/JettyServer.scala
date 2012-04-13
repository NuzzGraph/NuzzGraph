package nuzzgraph.server.core

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection

class JettyServer(runningPort:Int) {
  def this() = this(604) //default port

  val server : Server = new Server(runningPort)
  val handler : ServerHandler = new ServerHandler
  server.setHandler(handler)

  def setHandler(contexts: ContextHandlerCollection): Unit = {
    server.setHandler(contexts)
  }

  def start: Unit = {
    server.start
  }

  def stop: Unit = {
    server.stop
    server.join
  }

  def isStarted: Boolean = {
    return server.isStarted
  }

  def isStopped: Boolean = {
    return server.isStopped
  }
}
