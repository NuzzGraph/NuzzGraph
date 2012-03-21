package nuzzgraph.dbclient;

import com.orientechnologies.orient.client.remote.OEngineRemote;
import com.orientechnologies.orient.core.Orient;
import com.tinkerpop.blueprints.pgm.impls.orientdb.OrientGraph;
import nuzzgraph.dbclient.gui.MainForm;
import nuzzgraph.dbclient.gui.MainMenuBar;

import javax.swing.*;

/**
 * Main entry point for dbclient
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 6:43 PM
 */
public class DBClientMain
{
    static JFrame frame;
    static OrientGraph graphdb;
    static MainForm form;
    
    public static void main(String[] args)
    {
        frame = new JFrame("NuzzGraph");
        frame.setMenuBar(new MainMenuBar());

        form = new MainForm();

        MainForm.setMainForm(form);
        frame.setContentPane(form.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        connectToServer();
        frame.setVisible(true);
    }

    public static void connectToServer()
    {
        String dbName = "nuzzgraph-test";
        try
        {
            Orient.instance().registerEngine(new OEngineRemote());
            graphdb = new OrientGraph("remote:localhost/" + dbName);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(form, "Unable to connect to server.  Ensure that NuzzGraph server is running.");
            return;
        }
        LogText("Connected to NuzzGraph on instance " + dbName);
        
        form.enableGUI();
        refreshRootData();
    }

    private static void refreshRootData()
    {
        NodeHelper.vertexClusterId = graphdb.getRawGraph().getClusterIdByName("OGraphVertex");

        form.loadVerticesIntoList(graphdb.getVertices());
    }

    public static void exit()
    {
        frame.dispose();
    }
    
    public static void LogText(String text)
    {
        form.logText(text + System.getProperty("line.separator"));
    }
}
