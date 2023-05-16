/*
 * SecretKey.java
 *
 * Created on 30 December 2005, 16:51
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
public class SecretKey implements javax.swing.tree.TreeNode, Comparable<SecretKey> {
    private javax.swing.tree.TreeNode parent = null;
    private Vector<javax.swing.tree.TreeNode> childNodes = 
            new Vector<javax.swing.tree.TreeNode>();
    private NativeSecretKey key;
    private NativeUsername name;    
    
    /** Creates a new instance of SecretKey */
    public SecretKey(javax.swing.tree.TreeNode parent,
            NativeSecretKey key
            ) {
        this(parent, key, null);
    }
    
    public SecretKey(javax.swing.tree.TreeNode parent,
            NativeSecretKey key,
            NativeUsername name
            ) {
        this.parent = parent;
        this.key = key;
        this.name = name;
        
        PublicKey f = new PublicKey(this, key.getPubkey());
        childNodes.add(f);        
    }
    
    boolean isLocked()
    {
        if(null == key)
            return true;
        return key.isLocked();
    }
    
    public int compareTo(SecretKey b)
    {
        if(null == b)
            throw new NullPointerException();
        return toString().toUpperCase().compareTo(
            b.toString().toUpperCase());
    }

    public String toString()
    {
        if(name != null) return name.getName();
        return key.getPubkey().getName();
    }

    public long cHandle()
    {
        return key.cHandle();
    }
    
    public void lock()
    {
        key.lock();
    }
    
    public boolean unlock()
    {
        return key.unlock();
    }
    
    public boolean unlock(CryptoString suggested)
    {
        ByteArrayWrapper bytes = new ByteArrayWrapper();
        bytes.data = new byte[suggested.utf8length()];
        suggested.getUTF8(bytes.data);
        suggested.wipe();
                
        boolean b = false;
        try {
            b = key.unlock(bytes);
        } finally {
            java.util.Arrays.fill(bytes.data, (byte)0);
        }
        return b;
    }
    
    public void addUser(String name)
    {
        key.addUser(name);
    }
    public void changePassphrase(KeyConstants.MDA hash, KeyConstants.CEA kpalg, byte[] phrase)
    {
        key.changePassphrase(hash, kpalg, phrase);
    }
    public boolean revoke(boolean permanently)
    {
        return key.revoke(permanently);
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
        return Root.instance();
    }
    public boolean isLeaf()
    {
        return false;
    }        
}
