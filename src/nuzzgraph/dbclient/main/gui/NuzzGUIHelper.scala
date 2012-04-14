package nuzzgraph.dbclient.main.gui

import javax.swing._
import java.awt._

/**
 */
object NuzzGUIHelper {
  def SetScrollbarToEnd(scrollPane: JScrollPane): Unit = {
    scrollPane.getViewport.setViewPosition(new Point(0, 100000))
    scrollPane.getViewport.setViewPosition(new Point(0, scrollPane.getViewport.getViewPosition.getY.asInstanceOf[Int] + 30))
  }
}
