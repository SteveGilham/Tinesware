/*
 * DocumentRoot.java
 *
 * Created on 27 December 2005, 19:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.util.Vector;
import java.util.Iterator;

/**
 *
 * @author Steve
 */
public class DocumentRoot implements javax.swing.tree.TreeNode, 
        javax.swing.table.TableModel {
    
    private javax.swing.table.DefaultTableModel tableNature = null;
    
    /** Creates a new instance of PublicKeyRoot */
    public DocumentRoot() {
    }

    public String toString()
    {
        return GlobalData.getResourceString("Documents");
    }
    
    private static DocumentRoot theRoot = null;
    
    public static synchronized DocumentRoot instance()
    {
        if(null == theRoot) theRoot = new DocumentRoot();
        return theRoot;
    }
    
    public void append(Document d)
    {
        childNodes.add(d);
        Application.instance().getTreeModel().nodeStructureChanged(this);
    }
    public void remove(Document d)
    {
        childNodes.remove(d);
        Application.instance().getTreeModel().nodeStructureChanged(this);
    }
    
    public Iterator<Document> iterator()
    {
        return childNodes.iterator();
    }
    
    private Vector<Document> childNodes = new
            Vector<Document>();
    
    // TreeNode
    public java.util.Enumeration children()
    {
        return childNodes.elements();
    }
    public boolean getAllowsChildren()
    {
        return true;
    }
    public javax.swing.tree.TreeNode getChildAt(int childIndex)
    {
        return childNodes.elementAt(childIndex);
    }
    public int getChildCount()
    {
        return childNodes.size();
    }
    public int getIndex(javax.swing.tree.TreeNode node)
    {
        return childNodes.indexOf(node);
    }
    public javax.swing.tree.TreeNode getParent()
    {
        return Root.instance();
    }
    public boolean isLeaf()
    {
        return false;
    }
    
    // TableModel
    public void addTableModelListener(javax.swing.event.TableModelListener l)
    {
        tableNature.addTableModelListener(l);
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return JLabel.class;
    }
    public int getColumnCount()
    {
        return tableNature.getColumnCount();
    }
    public String getColumnName(int columnIndex)
    {
        return tableNature.getColumnName(columnIndex);
    }

    public int getRowCount()
    {
        return tableNature.getRowCount();
    }
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return tableNature.getValueAt(rowIndex, columnIndex);
    }
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }
    public void removeTableModelListener(javax.swing.event.TableModelListener l)
    {
        tableNature.removeTableModelListener(l);
    }
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        // no-op
    }    
}
