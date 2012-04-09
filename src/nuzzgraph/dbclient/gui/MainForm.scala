package nuzzgraph.dbclient.gui

import com.tinkerpop.blueprints.pgm.Vertex
import javax.swing._

/**
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 6:46 PM
 */
/*
object MainForm
{
  def getMainForm: MainForm = {
    return mainForm
  }

  def setMainForm(form: MainForm): Unit = {
    mainForm = form
  }

  private var mainForm: MainForm = null
}
*/
class MainForm(gui : MainForm_gui) extends JFrame
{
  gui.add(gui.panelMain)
  gui.panelIntro.setVisible(true)
  gui.splitMain.setVisible(false)

  private def createUIComponents: Unit = {
  }

  def enableGUI: Unit = {
    gui.panelIntro.setVisible(false)
    gui.splitMain.setVisible(true)
  }

  def loadVerticesIntoList(vertices: Iterable[Vertex]): Unit = {
    gui.cTabNodeRaw.logic.loadVerticesIntoList(vertices)
  }

  /**
   * Logs text to the screen.  Do not use this method.
   * Use DBClientMain.logText instead
   *
   * @param text
   */
  def logText(text: String): Unit = {
    gui.txtLog.setText(gui.txtLog.getText + text)
  }

}
