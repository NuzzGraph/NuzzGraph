package nuzzgraph.dbclient.gui;

import nuzzgraph.dbclient.DBClientMain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Contains the components for the Menu Bar
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 8:03 PM
 */
public class MainMenuBar extends MenuBar
{
    public MainMenuBar()
    {
        super();
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuConnect = new Menu("Connect");
        Menu menuHelp = new Menu("Help");
        
        //File -> Exit
        MenuItem menuFileExit = new MenuItem("Exit");
        menuFileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DBClientMain.exit();
            }
        });
        
        //Edit -> Preferences
            //nothing yet
        
        //Connect -> Connect to server
        MenuItem menuConnectServer = new MenuItem("Connect to server");
        menuConnectServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DBClientMain.connectToServer();
            }
        });
        
        add(menuFile);
        add(menuEdit);
        //add(menuConnect);  - not used currently
        add(menuHelp);

        menuFile.add(menuFileExit);
        menuConnect.add(menuConnectServer);
    }
}
