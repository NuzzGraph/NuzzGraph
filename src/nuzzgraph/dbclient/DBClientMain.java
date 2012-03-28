package nuzzgraph.dbclient;

import nuzzgraph.dbclient.gui.MainForm;
import nuzzgraph.dbclient.gui.MainMenuBar;
import nuzzgraph.server.core.ServerController;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for dbclient
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 6:43 PM
 */
public class DBClientMain
{
    public static JFrame frame;
    public static MainForm form;
    public static boolean exiting = false;

    public static void main(String[] args)
    {
        frame = new JFrame("NuzzGraph");
        frame.setMenuBar(new MainMenuBar());
        frame.setMinimumSize(new Dimension(900, 600));
        form = new MainForm();

        MainForm.setMainForm(form);
        frame.setContentPane(form.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 600);
        connectToServer();

        if (!exiting)
            frame.setVisible(true);

    }

    public static void connectToServer()
    {
        String dbName = "nuzzgraph-test";
        try
        {
            ServerController.ConnectToGraphDB("remote:localhost/" + dbName);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(form, "Unable to connect to server.  Ensure that NuzzGraph server is running.");
            DBClientMain.exit();
            return;
        }
        logText("Connected to NuzzGraph on instance " + dbName);
        
        form.enableGUI();
        refreshRootData();
    }

    private static void refreshRootData()
    {
        NodeHelper.vertexClusterId = ServerController.getGraphDB().getRawGraph().getClusterIdByName("OGraphVertex");

        form.loadVerticesIntoList(ServerController.getGraphDB().getVertices());
    }

    public static void exit()
    {
        frame.dispose();
        exiting = true;
    }
    
    public static void logText(String text)
    {
        form.logText(text + System.getProperty("line.separator"));
    }
}
