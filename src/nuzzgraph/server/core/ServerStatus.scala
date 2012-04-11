package nuzzgraph.server.core

object ServerStatus extends Enumeration {
  type ServerStatus = Value
  val Stopped = Value("Stopped")
  val Initializing = Value("Initializing")
  val Available = Value("Available")
  val Failed = Value("Failed")
}
