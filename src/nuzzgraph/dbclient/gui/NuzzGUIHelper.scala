package nuzzgraph.dbclient.gui

import javax.swing._
import java.awt._

/**
 * User: Mark Nuzzolilo
 * Date: 4/3/12
 * Time: 10:59 PM
 */
object NuzzGUIHelper {
  def SetScrollbarToEnd(scrollPane: JScrollPane): Unit = {
    scrollPane.getViewport.setViewPosition(new Point(0, 100000))
    scrollPane.getViewport.setViewPosition(new Point(0, scrollPane.getViewport.getViewPosition.getY.asInstanceOf[Int] + 30))
  }
}
