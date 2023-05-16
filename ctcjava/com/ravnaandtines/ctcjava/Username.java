/*
 * Username.java
 *
 * Created on 30 December 2005, 19:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;

import java.util.Vector;

/**
 *
 * @author Steve
 */
public class Username implements javax.swing.tree.TreeNode {
    private javax.swing.tree.TreeNode parent = null;

    private Vector<javax.swing.tree.TreeNode> childNodes = 
            new Vector<javax.swing.tree.TreeNode>();    
    private NativeUsername name;
    private NativePublicKey key;
    private static java.awt.Image user = IconSelection.USER.getImage();

    public Username(javax.swing.tree.TreeNode parent,
            NativeUsername name, NativePublicKey key)
    {
        this.parent = parent;
        this.name = name;
        this.key = key;

        for(NativeSignature s = name.getSignature(); !s.isNull();
        s = s.nextSignature()) {
            Signature f = new Signature(this, s, key, name);
            childNodes.add(f);
        }
    }

    public String toString()
    {
        return GlobalData.getResourceString("UserID:")+name.getName();
    }

    /*
    public NativeUsername toName()
    {
        return name;
    } 
     */   
    
    public void sign(SecretKey sk, PublicKey owner)
    {
        name.sign(sk, owner);
    }

    
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
        return parent;
    }
    public boolean isLeaf()
    {
        return false;
    }                
}
