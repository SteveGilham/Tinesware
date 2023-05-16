/*
 * Signature.java
 *
 * Created on 30 December 2005, 18:26
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
public class Signature implements javax.swing.tree.TreeNode {
    private javax.swing.tree.TreeNode parent = null;

    private Vector<javax.swing.tree.TreeNode> childNodes = 
            new Vector<javax.swing.tree.TreeNode>();
    private NativeSignature sig;
    private NativePublicKey key;
    private NativeUsername name;
    private static java.awt.Image good = IconSelection.TICK.getImage();
    private static java.awt.Image bad = IconSelection.CROSS.getImage();
    private java.awt.Image image = null;
    private String label = null;

    public Signature(javax.swing.tree.TreeNode parent,
            NativeSignature sig, NativePublicKey key, NativeUsername name)
    {
        this.parent = parent;
        this.sig = sig;
        this.key = key;
        this.name = name;
        
        childNodes.add(new PublicKey(this, sig.toKey()));        
    }
    
    public Signature(javax.swing.tree.TreeNode parent,
            NativeSignature sig, NativePublicKey key)
    {
        this(parent, sig, key, null);
    }
    
    public String toString()
    {
        if(label != null)
            return label;
        
        if(sig.toKey().getUsername().isNull())
        {
            label = GlobalData.getResourceString("Signature_by_unknown") + sig.getName();
            return label;
        }
        boolean x = key.checkSignature(name, sig);
        label = (x ? GlobalData.getResourceString("Good_signature_made") : 
            GlobalData.getResourceString("Bad_signature_made_at")) +
            sig.getName();
        return label;
    }

    public java.awt.Image getImage()
    {
        if(sig.toKey().getUsername().isNull())
            return null;

        if(null == image)
        {
            boolean x = key.checkSignature(name, sig);
            if(x) image = good;
            else image = bad;
        }
        return image;
    }
    
    
    public NativeSignature toSig()
    {
        return sig;
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
