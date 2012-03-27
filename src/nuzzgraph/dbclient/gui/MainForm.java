package nuzzgraph.dbclient.gui;

import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.dbclient.NodeHelper;
import nuzzgraph.server.core.NodeInstance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Mark Nuzzolilo
 * Date: 3/20/12
 * Time: 6:46 PM
 */
public class MainForm extends JFrame
{
    private static MainForm mainForm;
    
    private JButton bTest;
    private JPanel panelMain;
    private JSplitPane splitRaw;
    private JList listRawNodes;
    private JTabbedPane tabs;
    private JTextPane txtLog;
    private JPanel tabRaw;
    private JPanel tabRawGraph;
    private JPanel tabHighLevel1;
    private JSplitPane splitMain;
    private JPanel panelIntro;
    private JLabel lblIntro;
    
    private DefaultListModel listModel;
    
    public MainForm()
    {
        add(panelMain);

        createUIComponents();

    }

    private void createUIComponents()
    {
        panelIntro.setVisible(true);
        splitMain.setVisible(false);
        listModel = new DefaultListModel();
        listRawNodes.setModel(listModel);
        listRawNodes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                String selectedText = listModel.getElementAt(e.getFirstIndex()).toString();
                int iId = Integer.parseInt(selectedText.substring(selectedText.indexOf(" ") + 1));
                try
                {
                    NodeInstance n = NodeInstance.get(iId);
                }
                catch (Exception ex)
                {
                    logText("Error: " + ex.getMessage());
                }
                String sId = NodeHelper.getVertexId(iId);

            }
        });

        bTest.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                bTest.setText("Clicked");
            }
        });
    }

    public static MainForm getMainForm()
    {
        return mainForm;
    }
    
    public static void setMainForm(MainForm form)
    {
        mainForm = form;
    }

    public void enableGUI()
    {
        panelIntro.setVisible(false);
        splitMain.setVisible(true);
    }

    public void loadVerticesIntoList(Iterable<Vertex> vertices)
    {
        listModel.clear();

        for (Vertex v : vertices)
        {
            listModel.addElement("Node " + NodeHelper.getNodeId(v));
            //v.getId()
        }
    }

    public void logText(String text)
    {
        txtLog.setText(txtLog.getText() + text);
    }
    

}
