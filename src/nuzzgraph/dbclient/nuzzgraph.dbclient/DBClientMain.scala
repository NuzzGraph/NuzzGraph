package nuzzgraph.dbclient

import gui.{MainForm_gui, MainForm, MainMenuBar}
import nuzzgraph.server.core.ServerController
import javax.swing._
import java.awt._
import scala.collection.JavaConverters._

/**
 * Main entry point for dbclient
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 6:43 PM
 */
object DBClientMain
{
  var frame: JFrame = null
  var form: MainForm_gui = null
  var exiting: Boolean = false

  def startMain(args: Array[String]): Unit = {
    frame = new JFrame("NuzzGraph")
    frame.setMenuBar(new MainMenuBar)
    frame.setMinimumSize(new Dimension(900, 600))
    form = new MainForm_gui
    form.init
    //MainForm.setMainForm(form)
    frame.setContentPane(form.getContentPane)
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.pack
    frame.setSize(900, 600)
    connectToServer
    if (!exiting) frame.setVisible(true)
  }

  def connectToServer: Unit = {
    var dbName: String = "nuzzgraph-test"
    try {
      ServerController.ConnectToGraphDB("remote:localhost/" + dbName)
    }
    catch {
      case e: Exception => {
        JOptionPane.showMessageDialog(form, "Unable to connect to server.  Ensure that NuzzGraph server is running.")
        DBClientMain.exit
        return
      }
    }
    logText("Connected to NuzzGraph on instance " + dbName)
    form.logic.enableGUI
    refreshRootData
  }

  private def refreshRootData: Unit = {
    NodeHelper.vertexClusterId = ServerController.getGraphDB.getRawGraph.getClusterIdByName("OGraphVertex")
    form.logic.loadVerticesIntoList(ServerController.getGraphDB.getVertices.asScala)
  }

  def exit: Unit = {
    frame.dispose
    exiting = true
  }

  def logText(text: String): Unit = {
    form.logic.logText(text + System.getProperty("line.separator"))
  }

}
