package nuzzgraph.dbclient.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * User: Mark Nuzzolilo
 * Date: 3/26/12
 * Time: 9:22 PM
 */
public class NodePropertyComponent extends JComponent
{
    private JButton bDelete;
    private JTextField txtValue;
    private JLabel lblName;
    private JPanel panelMain;

    public NodePropertyComponent()
    {
        add(panelMain);
        createUIComponents();
    }

    private void createUIComponents()
    {
    }

    /**
     * Initializes the component based on a property's name and value
     *
     * @param pName  The name of the property
     * @param pValue The value of the property
     */
    public void initValues(String pName, String pValue)
    {
        lblName.setText(pName);
        txtValue.setText(pValue);
    }

    public void addNodePropertyChangedListener(DocumentListener l)
    {
        txtValue.getDocument().addDocumentListener(l);
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
        panelMain.setLayout(new FormLayout("fill:93px:grow(0.5),left:10dlu:noGrow,fill:max(d;150px):grow(0.5),left:10dlu:noGrow,fill:max(d;20px):noGrow", "center:max(d;30px):grow"));
        txtValue = new JTextField();
        txtValue.setText(" ");
        txtValue.putClientProperty("caretWidth", new Integer(10));
        CellConstraints cc = new CellConstraints();
        panelMain.add(txtValue, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.CENTER));
        bDelete = new JButton();
        bDelete.setBackground(new Color(-16777216));
        bDelete.setFont(new Font("Courier New", Font.BOLD, 8));
        bDelete.setForeground(new Color(-65536));
        bDelete.setHideActionText(false);
        bDelete.setText("X");
        panelMain.add(bDelete, cc.xy(5, 1));
        lblName = new JLabel();
        lblName.setText("Label");
        panelMain.add(lblName, cc.xy(1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        lblName.setLabelFor(txtValue);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    { return panelMain; }
}
