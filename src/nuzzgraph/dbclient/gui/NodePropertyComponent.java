package nuzzgraph.dbclient.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javaEventing.EventManager;
import javaEventing.EventObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * User: Mark Nuzzolilo
 * Date: 3/26/12
 * Time: 9:22 PM
 */
public class NodePropertyComponent extends JComponent
{
    private JButton bDelete;
    private JTextField txtName;
    private JTextField txtValue;
    private JPanel panelMain;

    public NodePropertyComponent()
    {
        add(panelMain);
        createUIComponents();
    }

    private void createUIComponents()
    {
        txtValue.putClientProperty("caretWidth", 2); //disable annoying wide caret/cursor on text
    }

    /**
     * Initializes the component based on a property's name and value
     *
     * @param pName  The name of the property
     * @param pValue The value of the property
     */
    public void initValues(String pName, String pValue)
    {
        txtName.setText(pName);
        txtValue.setText(pValue);
        DocumentListener listener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                EventManager.triggerEvent(this, new PropertyChangedEvent());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                EventManager.triggerEvent(this, new PropertyChangedEvent());
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                EventManager.triggerEvent(this, new PropertyChangedEvent());
            }
        };

        txtValue.getDocument().addDocumentListener(listener);
        txtName.getDocument().addDocumentListener(listener);
    }

    public String getPropertyName()
    {
        return txtName.getText();
    }

    public String getPropertyValue()
    {
        return txtValue.getText();
    }

    public void addButtonListener(ActionListener l)
    {
        bDelete.addActionListener(l);
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
        panelMain.setLayout(new FormLayout("fill:120px:noGrow,left:4dlu:noGrow,fill:d:grow,left:10dlu:noGrow,fill:max(d;20px):noGrow", "center:max(d;30px):grow"));
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
        txtName = new JTextField();
        panelMain.add(txtName, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    { return panelMain; }

    class PropertyChangedEvent extends EventObject {}   // <-- Define your own event, by implementing an interface or inheriting a class.

}
