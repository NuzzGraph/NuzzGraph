package nuzzgraph.dbclient.gui;

import javax.swing.*;
import java.awt.*;

/**
 * User: Mark Nuzzolilo
 * Date: 4/3/12
 * Time: 10:59 PM
 */
public class NuzzGUIHelper
{
    public static void SetScrollbarToEnd(JScrollPane scrollPane)
    {
        scrollPane.getViewport().setViewPosition(new Point(0, 100000));
        scrollPane.getViewport().setViewPosition(new Point(0, (int) scrollPane.getViewport().getViewPosition().getY() + 30));
    }
}
