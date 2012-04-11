package nuzzgraph.server.core.exception

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 3/20/12
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ServerIntegrityException extends Exception {
  def this() {
    this()
    `super`
  }

  def this(message: String) {
    this()
    `super`(message)
  }
}
