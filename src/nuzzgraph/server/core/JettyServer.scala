package nuzzgraph.server.core

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandlerCollection

class JettyServer {
  def this() {
    this()
    `this`(604)
  }

  def this(runningPort: Integer) {
    this()
    server = new Server(runningPort)
    handler = new ServerHandler
    server.setHandler(handler)
  }

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

  private var server: Server = null
  private var handler: ServerHandler = null
}
