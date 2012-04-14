package nuzzgraph.dbclient.main.gui

import com.jgoodies.forms.layout.CellConstraints
import com.jgoodies.forms.layout.FormLayout
import com.tinkerpop.blueprints.pgm.Vertex
import javaEventing.EventManager
import javaEventing.interfaces.Event
import javaEventing.interfaces.GenericEventListener
import nuzzgraph.server.core.NodeInstance
import nuzzgraph.server.core.NodePropertiesContainer
import nuzzgraph.server.core.exception.ServerIntegrityException
import javax.swing._
import javax.swing.border.TitledBorder
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import util.Sorting
import nuzzgraph.dbclient.{NodeHelper, DBClientMain}

/**
 */
class TabNodeRaw(gui: TabNodeRaw_gui) extends JComponent
{
  var listModel : DefaultListModel[String] = new DefaultListModel()
  private var gbcItem: GridBagConstraints = null
  private var gbcItemLast: GridBagConstraints = null
  private var changesMadeToNode: Boolean = false
  private var selectedNodeFriendlyId: String = ""
  private var currentNodeId: Long = -1
  private[gui] var currentNodeProperties: ArrayList[NodePropertyComponent] = null
  private[gui] var currentNodeInstance: NodeInstance = null

  createUIComponents




  private def createUIComponents: Unit = {
    gui.listRawNodes.setModel(listModel)
    gui.listRawNodes.addListSelectionListener(new ListSelectionListener {
      def valueChanged(e: ListSelectionEvent): Unit = {
        var selectedText: String = listModel.getElementAt(gui.listRawNodes.getSelectedIndex).toString
        if (selectedText == selectedNodeFriendlyId) return
        selectedNodeFriendlyId = selectedText
        var iId: Int = Integer.parseInt(selectedText.substring(selectedText.indexOf(" ") + 1))
        try {
          loadNode(iId)
        }
        catch {
          case ex: Exception => {
            DBClientMain.logText("Error: " + ex.getMessage)
          }
        }
      }
    })
    gui.bNewNode.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        doNewNode
      }
    })
    gui.bReloadNode.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        doReloadNode
      }
    })
    gui.bSaveNode.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        doSaveNode
      }
    })
    gui.bDeleteNode.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        doDeleteNode
      }
    })
    gui.bAddProperty.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        doAddProperty
      }
    })
    gbcItem = new GridBagConstraints
    gbcItem.gridx = 0
    gbcItem.weightx = 1.0
    gbcItem.weighty = 0.0
    gbcItem.anchor = GridBagConstraints.NORTHWEST
    gbcItem.fill = GridBagConstraints.HORIZONTAL
    gbcItemLast = new GridBagConstraints
    gbcItemLast.gridx = 0
    gbcItemLast.weightx = 1.0
    gbcItemLast.weighty = 1.0
    gbcItemLast.fill = GridBagConstraints.HORIZONTAL
    gbcItemLast.anchor = GridBagConstraints.NORTHWEST
    gui.panelPropertyHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black))
    gui.panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black))
    gui.panelNodeRawPropertiesOuter.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black))
  }

  private def doSaveNode: Unit = {
    try {
      var propertiesToSave: HashMap[String, String] = new HashMap[String, String]
      import scala.collection.JavaConversions._
      for (pComp <- currentNodeProperties) propertiesToSave.put(pComp.getPropertyName, pComp.getPropertyValue)
      currentNodeInstance.uploadDataForSave(propertiesToSave)
      currentNodeInstance.saveNode
    }
    catch {
      case e: Exception => {
        DBClientMain.logText(e.getMessage)
      }
    }
    changesMadeToNode = false
  }

  private def doAddProperty: Unit = {
    changesMadeToNode = true
    addPropertyGUI("", "")
    repaintGUI
    NuzzGUIHelper.SetScrollbarToEnd(gui.spNodeRawProperties)
  }

  private def doDeleteNode: Unit = {
  }

  private def doReloadNode: Unit = {
    if (currentNodeInstance == null) return
    if (JOptionPane.showConfirmDialog(DBClientMain.frame, "All changes will be lost.  Continue?", "Reload Node", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      try {
        loadNode(currentNodeInstance.getId)
      }
      catch {
        case ex: Exception => {
          DBClientMain.logText(ex.getMessage)
        }
      }
    }
  }

  private def doNewNode: Unit = {
    checkSaveNode
  }

  private def checkSaveNode: Unit = {
    if (changesMadeToNode && JOptionPane.showConfirmDialog(DBClientMain.frame, "Save changes to node?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) doSaveNode
  }

  private def loadNode(nodeid: Long): Unit = {
    currentNodeProperties = new ArrayList[NodePropertyComponent]
    gui.panelNodeRawPropertiesData.removeAll
    var n: NodeInstance = NodeInstance.get(nodeid)
    var props: NodePropertiesContainer = n.getNodeData.properties
    var keys: Array[String] = new Array[String](props.keySet.size)
    props.keySet.toArray(keys)
    Sorting.quickSort(keys)
    for (i <- 0 until keys.length)
    {
      var pKey: String = keys(i)
      var pValue: String = props.get(pKey)
      addPropertyGUI(pKey, pValue)
    }
    repaintGUI
    gui.lblNodeId.setText("Node " + n.getId)
    currentNodeInstance = n
  }

  private def addPropertyGUI(pKey: String, pValue: String): Unit = {
    val c: NodePropertyComponent = new NodePropertyComponent
    c.initValues(pKey, pValue)
    EventManager.registerEventListener(new GenericEventListener {
      def eventTriggered(o: AnyRef, event: Event): Unit = {
        changesMadeToNode = true
      }
    }, classOf[NodePropertyComponent#PropertyChangedEvent])
    currentNodeProperties.add(c)
    gbcItem.gridy = currentNodeProperties.size - 1
    gui.panelNodeRawPropertiesData.add(c.$$$getRootComponent$$$, gbcItem)
    c.addButtonListener(new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        try {
          gui.panelNodeRawPropertiesData.remove(c.$$$getRootComponent$$$)
          repaintGUI
        }
        catch {
          case ex: Exception => {
            DBClientMain.logText("Error deleting property: " + ex.getMessage)
          }
        }
      }
    })
  }

  /**
   * Repaints the GUI (such as after adding or removing a component
   */
  private def repaintGUI: Unit = {
    gui.panelNodeRawPropertiesData.revalidate
    gui.panelNodeRawPropertiesData.repaint()
  }

  def loadVerticesIntoList(vertices: Iterable[Vertex]): Unit = {
    listModel.clear
    import scala.collection.JavaConversions._
    for (v <- vertices) {
      listModel.addElement("Node " + NodeHelper.getNodeId(v))
    }
  }


}
