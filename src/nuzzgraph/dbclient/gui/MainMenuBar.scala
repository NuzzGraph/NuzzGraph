package nuzzgraph.dbclient.gui

import nuzzgraph.dbclient.DBClientMain
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import nuzzgraph.dbclient.DBClientMain

/**
 * Contains the components for the Menu Bar
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 8:03 PM
 */
class MainMenuBar extends MenuBar()
{
    var menuFile: Menu = new Menu("File")
    var menuEdit: Menu = new Menu("Edit")
    var menuConnect: Menu = new Menu("Connect")
    var menuHelp: Menu = new Menu("Help")
    var menuFileExit: MenuItem = new MenuItem("Exit")
    menuFileExit.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        DBClientMain.exit
      }
    })
    var menuConnectServer: MenuItem = new MenuItem("Connect to server")
    menuConnectServer.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        DBClientMain.connectToServer
      }
    })
    add(menuFile)
    add(menuEdit)
    add(menuHelp)
    menuFile.add(menuFileExit)
    menuConnect.add(menuConnectServer)
}
