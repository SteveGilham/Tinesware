/*
 * KeyName.java
 *
 * Created on 30 December 2005, 18:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;

/**
 *
 * @author Steve
 */
public class KeyName implements javax.swing.tree.TreeNode {
    private javax.swing.tree.TreeNode parent = null;
    private String name = null;
    
    /** Creates a new instance of KeyName */
    public KeyName(javax.swing.tree.TreeNode parent, String name) {
        this.parent = parent;
        this.name = name;
    }
    
    public String toString()
    {
        return name;
    }
      
    // TreeNode
    public java.util.Enumeration children()
    {
        return null;
    }
    public boolean getAllowsChildren()
    {
        return false;
    }
    public javax.swing.tree.TreeNode getChildAt(int childIndex)
    {
        return null;
    }
    public int getChildCount()
    {
        return 0;
    }
    public int getIndex(javax.swing.tree.TreeNode node)
    {
        return -1;
    }
    public javax.swing.tree.TreeNode getParent()
    {
        return parent;
    }
    public boolean isLeaf()
    {
        return true;
    }        
    
}
