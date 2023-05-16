/*
 * PublicKey.java
 *
 * Created on 30 December 2005, 16:38
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
public class PublicKey implements javax.swing.tree.TreeNode, Comparable<PublicKey> {
    private javax.swing.tree.TreeNode parent = null;
    private NativePublicKey key = null;
    private Vector<javax.swing.tree.TreeNode> childNodes = null;
    private NativeUsername name = null;    
    
    /** Creates a new instance of PublicKey */
    public PublicKey(javax.swing.tree.TreeNode parent,
            NativePublicKey key
            ) {
        this(parent, key, null);
    }
    
    public PublicKey(javax.swing.tree.TreeNode parent,
            NativePublicKey key,
            NativeUsername name
            ) {
        this.parent = parent;
        this.key = key;
        this.name = name;
    }
    
    public String toString()
    {
        if(name != null) 
            return name.getName();
        return key.getName();
    }
    
    public int compareTo(PublicKey b)
    {
        if(null == b)
            throw new NullPointerException();
        return toString().toUpperCase().compareTo(
            b.toString().toUpperCase());
    }
    
    public long cHandle()
    {
        return key.cHandle();
    }
    
    public boolean isLocalSecret()
    {
        return key.getSeckey() != null;
    }
    
    public void able()
    {
        key.able();
    }

    public void delete()
    {
        key.delete();
    }
    
    public void loadChildren()
    {
        childNodes = new Vector<javax.swing.tree.TreeNode>();
        KeyName name = new KeyName(this, key.data()); // may want to be able to update 
        childNodes.add(name);
        
        if(key.isRevoked()) { // will want to be able to update !!!
            KeyName revoke = new KeyName(this, GlobalData.getResourceString("NOTE:_This_key_has"));
            childNodes.add(revoke);
        }
        
        for(NativeSignature s = key.getSignature(); !s.isNull();
        s = s.nextSignature()) {
            Signature f = new Signature(this, s, key, null);
            childNodes.add(f);
        }
        
        
        for(NativePublicKey k = key.getSubkey(); (k!=null) && !k.isNull();
        k = k.getSubkey()) {
            PublicKey f = new PublicKey(this, k);
            childNodes.add(f);
        }
        
        
        for(NativeUsername u = key.getUsername(); !u.isNull();
        u = u.nextUsername()) {
            Username f = new Username(this, u, key);
            childNodes.add(f);
        }
        
    }
    
    void reset()
    {
        childNodes = null;
        loadChildren();
    }
    
    public void sign(SecretKey sk)
    {
        key.sign(sk);
    }
    
    public boolean extract()
    {
        return key.extract();
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
        if(null == childNodes)
            loadChildren();
        
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
