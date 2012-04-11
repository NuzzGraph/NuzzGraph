package nuzzgraph.server.core

/**
 * Represents a placeholder for a Node
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 1:36 AM
 */
class NodeReference {
  /**
   * Creates a new NodeReference
   * @param id The id of the node
   */
  def this(id: Long) {
    this()
    this.id = id
  }

  private[core] var id: Long = 0L
}
