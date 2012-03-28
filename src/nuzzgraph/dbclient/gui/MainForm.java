package nuzzgraph.dbclient.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.dbclient.DBClientMain;
import nuzzgraph.dbclient.NodeHelper;
import nuzzgraph.server.core.NodeInstance;
import nuzzgraph.server.core.NodePropertiesContainer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

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
    private JPanel panelNodeRaw;
    private JScrollBar scrollBarNodeRawRelationships;
    private JScrollPane spNodeRawRelationships;
    private JPanel panelListRaw;
    private JPanel panelNodeRawPropertiesOuter;
    private JScrollPane spNodeRawProperties;
    private JScrollBar scrollBarNodeRawProperties;
    private JSplitPane splitNodeRaw;
    private JButton bSaveNodeRaw;
    private JScrollBar scrollbarNodeSelectionRaw;
    private JPanel panelNodeRawPropertiesInner;
    private JPanel panelNodeRawRelationshipsOuter;
    private JPanel panelNodeRawPropertiesData;

    private DefaultListModel listModel;
    private GridBagConstraints gbcItem;
    private GridBagConstraints gbcItemLast;

    private String selectedNodeRaw = "";

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
        listRawNodes.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                //Check text of selected node
                String selectedText = listModel.getElementAt(listRawNodes.getSelectedIndex()).toString();
                if (selectedText.equals(selectedNodeRaw))
                    return;
                selectedNodeRaw = selectedText;

                int iId = Integer.parseInt(selectedText.substring(selectedText.indexOf(" ") + 1));
                try
                {
                    NodeInstance n = NodeInstance.get(iId);
                    loadNode_Raw(n);
                }
                catch (Exception ex)
                {
                    DBClientMain.logText("Error: " + ex.getMessage());
                }
                String sId = NodeHelper.getVertexId(iId);

            }

            private void loadNode_Raw(NodeInstance n)
            {
                panelNodeRawPropertiesData.removeAll(); //clear out any old data

                NodePropertiesContainer props = n.getNodeData().getProperties();
                String[] keys = new String[props.keySet().size()];
                props.keySet().toArray(keys);

                int count = 0;

                for (int i = 0; i < keys.length; i++)
                {
                    //Get key and value
                    String pKey = keys[i];
                    String pValue = props.get(pKey);

                    //Load GUI component
                    final NodePropertyComponent c = new NodePropertyComponent();
                    c.initValues(pKey, pValue);

                    if (i == keys.length - 1)
                    {
                        gbcItemLast.gridy = i;
                        panelNodeRawPropertiesData.add(c.$$$getRootComponent$$$(), gbcItemLast);
                    }
                    else
                    {
                        gbcItem.gridy = i;
                        panelNodeRawPropertiesData.add(c.$$$getRootComponent$$$(), gbcItem);
                    }

                    //c.setVisible(true);

                }

                //
                // panelNodeRawPropertiesData.repaint();
                //invalidate();
                panelNodeRawPropertiesData.revalidate();
                panelNodeRawPropertiesData.repaint();
            }
        });

        //Create constraints to allow for dynamic add of components
        gbcItem = new GridBagConstraints();
        gbcItem.gridx = 0;
        gbcItem.weightx = 1.0;
        gbcItem.weighty = 0.0;
        gbcItem.anchor = GridBagConstraints.NORTHWEST;
        gbcItem.fill = GridBagConstraints.HORIZONTAL;
        gbcItemLast = new GridBagConstraints();
        gbcItemLast.gridx = 0;
        gbcItemLast.weightx = 1.0;
        gbcItemLast.weighty = 1.0;
        gbcItemLast.fill = GridBagConstraints.HORIZONTAL;
        gbcItemLast.anchor = GridBagConstraints.NORTHWEST;
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

    /**
     * Logs text to the screen.  Do not use this method.
     * Use DBClientMain.logText instead
     *
     * @param text
     */
    public void logText(String text)
    {
        txtLog.setText(txtLog.getText() + text);
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        panelMain = new JPanel();
        panelMain.setLayout(new FormLayout("fill:max(d;600px):grow", "fill:max(d;500px):grow,fill:max(d;1px):noGrow"));
        panelMain.setMinimumSize(new Dimension(278, 300));
        panelMain.setVisible(true);
        panelMain.setBorder(BorderFactory.createTitledBorder(""));
        splitMain = new JSplitPane();
        splitMain.setContinuousLayout(false);
        splitMain.setDividerLocation(288);
        splitMain.setOneTouchExpandable(false);
        splitMain.setOrientation(0);
        splitMain.setResizeWeight(1.0);
        splitMain.setVisible(true);
        CellConstraints cc = new CellConstraints();
        panelMain.add(splitMain, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        tabs = new JTabbedPane();
        tabs.setVisible(true);
        splitMain.setLeftComponent(tabs);
        tabRaw = new JPanel();
        tabRaw.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        tabs.addTab("Raw", tabRaw);
        splitRaw = new JSplitPane();
        splitRaw.setContinuousLayout(false);
        splitRaw.setDividerLocation(180);
        splitRaw.setDividerSize(0);
        splitRaw.setOneTouchExpandable(false);
        splitRaw.setOrientation(1);
        splitRaw.setResizeWeight(0.0);
        tabRaw.add(splitRaw, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        panelNodeRaw = new JPanel();
        panelNodeRaw.setLayout(new FormLayout("fill:d:grow", "fill:d:grow"));
        splitRaw.setRightComponent(panelNodeRaw);
        splitNodeRaw = new JSplitPane();
        splitNodeRaw.setDividerSize(0);
        splitNodeRaw.setResizeWeight(0.5);
        panelNodeRaw.add(splitNodeRaw, cc.xy(1, 1));
        splitNodeRaw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        panelNodeRawPropertiesOuter = new JPanel();
        panelNodeRawPropertiesOuter.setLayout(new FormLayout("center:max(d;300px):grow,fill:d:noGrow", "fill:197px:grow,center:max(d;4px):noGrow"));
        splitNodeRaw.setLeftComponent(panelNodeRawPropertiesOuter);
        panelNodeRawPropertiesOuter.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        panelNodeRawPropertiesInner = new JPanel();
        panelNodeRawPropertiesInner.setLayout(new FormLayout("fill:d:grow,right:d:noGrow", "center:56px:noGrow,fill:d:grow"));
        panelNodeRawPropertiesOuter.add(panelNodeRawPropertiesInner, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        panelNodeRawPropertiesInner.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        spNodeRawProperties = new JScrollPane();
        panelNodeRawPropertiesInner.add(spNodeRawProperties, cc.xy(1, 2));
        spNodeRawProperties.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        panelNodeRawPropertiesData = new JPanel();
        panelNodeRawPropertiesData.setLayout(new GridBagLayout());
        spNodeRawProperties.setViewportView(panelNodeRawPropertiesData);
        panelNodeRawPropertiesData.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        checkBox1 = new JCheckBox();
        checkBox1.setText("CheckBox");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelNodeRawPropertiesData.add(checkBox1, gbc);
        checkBox2 = new JCheckBox();
        checkBox2.setText("CheckBox");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelNodeRawPropertiesData.add(checkBox2, gbc);
        scrollBarNodeRawProperties = new JScrollBar();
        panelNodeRawPropertiesInner.add(scrollBarNodeRawProperties, cc.xy(2, 2, CellConstraints.RIGHT, CellConstraints.FILL));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("right:d:grow,fill:d:noGrow", "fill:d:grow"));
        panelNodeRawPropertiesInner.add(panel1, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JLabel label1 = new JLabel();
        label1.setText("Node Properties");
        panel1.add(label1, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
        bSaveNodeRaw = new JButton();
        bSaveNodeRaw.setText("Save Node");
        panel1.add(bSaveNodeRaw, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.CENTER));
        panelNodeRawRelationshipsOuter = new JPanel();
        panelNodeRawRelationshipsOuter.setLayout(new FormLayout("fill:max(d;300px):grow", "fill:d:grow"));
        splitNodeRaw.setRightComponent(panelNodeRawRelationshipsOuter);
        panelNodeRawRelationshipsOuter.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        panelNodeRawRelationshipsOuter.add(panel2, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JScrollPane scrollPane1 = new JScrollPane();
        splitRaw.setLeftComponent(scrollPane1);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        panelListRaw = new JPanel();
        panelListRaw.setLayout(new FormLayout("fill:max(d;150px):grow,fill:d:noGrow", "fill:d:grow"));
        scrollPane1.setViewportView(panelListRaw);
        panelListRaw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        listRawNodes = new JList();
        panelListRaw.add(listRawNodes, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        scrollbarNodeSelectionRaw = new JScrollBar();
        panelListRaw.add(scrollbarNodeSelectionRaw, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.FILL));
        tabRawGraph = new JPanel();
        tabRawGraph.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        tabs.addTab("Raw Graph", tabRawGraph);
        tabRawGraph.setBorder(BorderFactory.createTitledBorder(""));
        final JLabel label2 = new JLabel();
        label2.setText("Graph view not available");
        tabRawGraph.add(label2, cc.xy(1, 1));
        tabHighLevel1 = new JPanel();
        tabHighLevel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        tabs.addTab("High-Level", tabHighLevel1);
        final JLabel label3 = new JLabel();
        label3.setText("High-Level view not available");
        tabHighLevel1.add(label3, cc.xy(1, 1));
        final JScrollPane scrollPane2 = new JScrollPane();
        splitMain.setRightComponent(scrollPane2);
        txtLog = new JTextPane();
        txtLog.setText("");
        scrollPane2.setViewportView(txtLog);
        panelIntro = new JPanel();
        panelIntro.setLayout(new FormLayout("fill:d:grow", "center:15px:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panelIntro.setVisible(false);
        panelMain.add(panelIntro, cc.xy(1, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
        lblIntro = new JLabel();
        lblIntro.setText("Connect to server first");
        panelIntro.add(lblIntro, cc.xy(1, 1));
        spNodeRawRelationships = new JScrollPane();
        panelIntro.add(spNodeRawRelationships, cc.xy(1, 3));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:d:grow", "fill:d:grow"));
        spNodeRawRelationships.setViewportView(panel3);
        scrollBarNodeRawRelationships = new JScrollBar();
        panel3.add(scrollBarNodeRawRelationships, cc.xy(1, 1, CellConstraints.RIGHT, CellConstraints.FILL));
        spNodeRawProperties.setVerticalScrollBar(scrollBarNodeRawProperties);
        scrollPane1.setVerticalScrollBar(scrollbarNodeSelectionRaw);
        spNodeRawRelationships.setVerticalScrollBar(scrollBarNodeRawRelationships);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    { return panelMain; }
}
