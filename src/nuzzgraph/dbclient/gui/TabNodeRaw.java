package nuzzgraph.dbclient.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.tinkerpop.blueprints.pgm.Vertex;
import nuzzgraph.dbclient.DBClientMain;
import nuzzgraph.dbclient.NodeHelper;
import nuzzgraph.server.core.NodeInstance;
import nuzzgraph.server.core.NodePropertiesContainer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Mark Nuzzolilo
 * Date: 3/28/12
 * Time: 12:09 PM
 */
public class TabNodeRaw extends JComponent
{
    private JPanel panelMain;
    private JSplitPane split1;
    private JPanel panelHeader;
    private JPanel panelNodeRawPropertiesData;
    private JPanel panelListRaw;
    private JList listRawNodes;
    private JScrollBar scrollbarNodeSelectionRaw;
    private JPanel panelPropertyHeader;
    JScrollPane spListRaw;
    JSplitPane split2;
    JPanel panelNodeRaw;
    JSplitPane split3;
    JPanel panelNodeRawPropertiesOuter;
    JPanel panelNodeRawPropertiesInner;
    JScrollPane spNodeRawProperties;
    JButton bAddProperty;
    JScrollBar scrollBarNodeRawProperties;
    JPanel panelNodeRelationships;
    JSplitPane split4;
    JButton bNewNode;
    JButton bSaveNode;
    JButton bReloadNode;
    JButton bDeleteNode;
    JLabel lblNodeId;

    private DefaultListModel listModel;
    private GridBagConstraints gbcItem;
    private GridBagConstraints gbcItemLast;

    private String selectedNodeRaw = "";
    private boolean changesMadeToNode;

    public TabNodeRaw()
    {
        // add(panelMain);
        //$$$setupUI$$$();
        createUIComponents();
    }

    private void createUIComponents()
    {
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
                    loadNode(n);
                }
                catch (Exception ex)
                {
                    DBClientMain.logText("Error: " + ex.getMessage());
                }
                String sId = NodeHelper.getVertexId(iId);

            }

        });

        //Buttons
        bNewNode.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doNewNode();
            }
        });

        bReloadNode.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doReloadNode();
            }
        });

        bSaveNode.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSaveNode();
            }
        });

        bDeleteNode.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDeleteNode();
            }
        });

        bAddProperty.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doAddProperty();
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

        panelPropertyHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        panelNodeRawPropertiesOuter.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
    }

    private void doSaveNode()
    {
        changesMadeToNode = false;
    }

    private void doAddProperty()
    {
        changesMadeToNode = true;
    }

    private void doDeleteNode()
    {
        //Are you sure?
    }

    private void doReloadNode()
    {
        checkSaveNode();
    }

    private void doNewNode()
    {
        checkSaveNode();
    }

    private void checkSaveNode()
    {
        if (changesMadeToNode && JOptionPane.showConfirmDialog(DBClientMain.frame, "Save changes to node?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            doSaveNode();

    }

    private void loadNode(NodeInstance n)
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

            //detect changes made
            c.addNodePropertyChangedListener(new DocumentListener()
            {
                @Override
                public void insertUpdate(DocumentEvent e) {}

                @Override
                public void removeUpdate(DocumentEvent e) {}

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    changesMadeToNode = true;
                }
            });

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


        }

        //
        // panelNodeRawPropertiesData.repaint();
        //invalidate();
        panelNodeRawPropertiesData.revalidate();
        panelNodeRawPropertiesData.repaint();

        lblNodeId.setText("Node " + n.getId());
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
        panelMain.setLayout(new FormLayout("fill:d:grow", "fill:d:grow"));
        split1 = new JSplitPane();
        split1.setContinuousLayout(true);
        split1.setDividerLocation(160);
        split1.setDividerSize(0);
        split1.setOneTouchExpandable(false);
        split1.setOrientation(1);
        split1.setResizeWeight(0.0);
        CellConstraints cc = new CellConstraints();
        panelMain.add(split1, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        spListRaw = new JScrollPane();
        split1.setLeftComponent(spListRaw);
        spListRaw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        panelListRaw = new JPanel();
        panelListRaw.setLayout(new FormLayout("fill:max(d;150px):grow,fill:d:noGrow", "fill:d:grow"));
        spListRaw.setViewportView(panelListRaw);
        panelListRaw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        scrollbarNodeSelectionRaw = new JScrollBar();
        panelListRaw.add(scrollbarNodeSelectionRaw, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.FILL));
        listRawNodes = new JList();
        panelListRaw.add(listRawNodes, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        split2 = new JSplitPane();
        split2.setDividerLocation(51);
        split2.setDividerSize(0);
        split2.setOrientation(0);
        split1.setRightComponent(split2);
        split2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
        panelNodeRaw = new JPanel();
        panelNodeRaw.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        split2.setRightComponent(panelNodeRaw);
        panelNodeRaw.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        split3 = new JSplitPane();
        split3.setDividerLocation(380);
        split3.setDividerSize(0);
        split3.setOrientation(1);
        split3.setResizeWeight(0.5);
        panelNodeRaw.add(split3, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        split3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        panelNodeRawPropertiesOuter = new JPanel();
        panelNodeRawPropertiesOuter.setLayout(new FormLayout("center:max(d;300px):grow,fill:d:noGrow", "center:d:grow"));
        split3.setLeftComponent(panelNodeRawPropertiesOuter);
        panelNodeRawPropertiesOuter.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        split4 = new JSplitPane();
        split4.setDividerSize(0);
        split4.setOrientation(0);
        panelNodeRawPropertiesOuter.add(split4, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        split4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        panelNodeRawPropertiesInner = new JPanel();
        panelNodeRawPropertiesInner.setLayout(new BorderLayout(0, 0));
        split4.setLeftComponent(panelNodeRawPropertiesInner);
        panelNodeRawPropertiesInner.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        panelPropertyHeader = new JPanel();
        panelPropertyHeader.setLayout(new FormLayout("right:d:grow,fill:d:noGrow", "fill:35px:grow"));
        panelNodeRawPropertiesInner.add(panelPropertyHeader, BorderLayout.CENTER);
        panelPropertyHeader.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(panelPropertyHeader.getFont().getName(), panelPropertyHeader.getFont().getStyle(), panelPropertyHeader.getFont().getSize())));
        final JLabel label1 = new JLabel();
        label1.setText("Node Properties");
        panelPropertyHeader.add(label1, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
        bAddProperty = new JButton();
        bAddProperty.setText("Add Property");
        panelPropertyHeader.add(bAddProperty, cc.xy(2, 1, CellConstraints.RIGHT, CellConstraints.CENTER));
        spNodeRawProperties = new JScrollPane();
        split4.setRightComponent(spNodeRawProperties);
        spNodeRawProperties.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        panelNodeRawPropertiesData = new JPanel();
        panelNodeRawPropertiesData.setLayout(new GridBagLayout());
        spNodeRawProperties.setViewportView(panelNodeRawPropertiesData);
        panelNodeRawPropertiesData.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        scrollBarNodeRawProperties = new JScrollBar();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelNodeRawPropertiesData.add(scrollBarNodeRawProperties, gbc);
        panelNodeRelationships = new JPanel();
        panelNodeRelationships.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        split3.setRightComponent(panelNodeRelationships);
        panelHeader = new JPanel();
        panelHeader.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:grow"));
        split2.setLeftComponent(panelHeader);
        panelHeader.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        bNewNode = new JButton();
        bNewNode.setText("New Node");
        panelHeader.add(bNewNode, cc.xy(1, 1));
        bSaveNode = new JButton();
        bSaveNode.setText("Save Node");
        panelHeader.add(bSaveNode, cc.xy(3, 1));
        bReloadNode = new JButton();
        bReloadNode.setText("Reload Node");
        panelHeader.add(bReloadNode, cc.xy(5, 1));
        bDeleteNode = new JButton();
        bDeleteNode.setText("Delete Node");
        panelHeader.add(bDeleteNode, cc.xy(7, 1));
        lblNodeId = new JLabel();
        lblNodeId.setText("No node loaded");
        panelHeader.add(lblNodeId, cc.xy(9, 1));
        spListRaw.setVerticalScrollBar(scrollbarNodeSelectionRaw);
        label1.setLabelFor(spListRaw);
        spNodeRawProperties.setVerticalScrollBar(scrollBarNodeRawProperties);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    { return panelMain; }
}
