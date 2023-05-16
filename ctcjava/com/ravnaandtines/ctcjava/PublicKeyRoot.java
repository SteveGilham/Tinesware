/*
 * PublicKeyRoot.java
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
import java.awt.Cursor;

/**
 *
 * @author Steve
 */
public class PublicKeyRoot implements javax.swing.tree.TreeNode, 
        javax.swing.table.TableModel {
    
    private javax.swing.table.DefaultTableModel tableNature = null;
    private Vector<PublicKey> childNodes = null;
    
    private java.awt.FileDialog filer = null;
    private String pubfile;
    private String pubdir;

    
    /** Creates a new instance of PublicKeyRoot */
    public PublicKeyRoot() {
    }

    private void initFileLocation()
    {
        if(null == pubfile) pubfile = Root.instance().getProperty("PublicKeyring", "");
        if(null == pubdir) pubdir  = Root.instance().getProperty("PublicKeyringDirectory", "");
    }
    
    
    public String toString()
    {
        return GlobalData.getResourceString("Public Keys");
    }
    
    private static PublicKeyRoot theRoot = null;
    
    public static synchronized PublicKeyRoot instance()
    {
        if(null == theRoot) theRoot = new PublicKeyRoot();
        return theRoot;
    }
    
    void loadPublicKeys()
    {
        java.awt.Frame parent = Application.instance().getFrame();
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            Application.instance().setStatusText(GlobalData.getResourceString("Reading_public_keys"));
            loadKeys();
            if(!isValid())
            {
                JOptionPane.showMessageDialog(
                        parent,
                        GlobalData.getResourceString("noPubring"),
                        GlobalData.getResourceString("Reading_public_keys"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(ptr_to_decode_context != 0)
            {
                NativePublicKey key = firstPubKey();
                Vector<PublicKey> v = new Vector<PublicKey>();
                while(key != null && !key.isNull())
                {
                    PublicKey kf = new PublicKey(this, key);
                    v.addElement(kf);
                    key = key.nextPubkey();
                }

                Application.instance().setStatusText(GlobalData.getResourceString("Sorting_public_keys"));
                java.util.Collections.sort(v);
                Application.instance().setStatusText(GlobalData.getResourceString("Loading_public_keys"));                
                childNodes = v;
            }
        }
        finally {
            Application.instance().setStatusText(GlobalData.getResourceString("Public_keys_loaded"));
            parent.setCursor(Cursor.getDefaultCursor());
            System.gc();
        }
    }
    
    void reset()
    {
         childNodes = null;
         loadPublicKeys();
    }
    
    JList getRecipientList()
    {
        if(null == childNodes)
            loadPublicKeys();
        if(childNodes == null)
            return null;
        return new JList(childNodes);
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
            loadPublicKeys();
        
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
    
    // self
    public void findKeyRing()
    {
        if(null == filer)
        {
            filer = new java.awt.FileDialog(
                    Application.instance().getFrame(),
                    GlobalData.getResourceString("Open_keyring_"),
                    java.awt.FileDialog.LOAD);
        }
        filer.setDirectory(pubdir);
        filer.setFile(pubfile);
        filer.setVisible(true);
        String pd = filer.getDirectory();
        String pf = filer.getFile();
        if(pd != null && pf != null)
        {
            if(!GlobalData.ensureWriteableFileExists(pd+pf)) return;            
            Root.instance().setPublicKeyRing(pd, pf);
            pubdir = pd;
            pubfile = pf;
            invalidateKeyrings();
        }
    }
    
    void invalidateKeyrings()
    {
        ptr_to_decode_context = 0;
        if(!clearKeyrings())
        {
                JOptionPane.showMessageDialog(
                        Application.instance().getFrame(),
                        GlobalData.getResourceString("Changes_to_keyrings"),
                        GlobalData.getResourceString("CTC_Keyring_shutdown"),
                        JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    void loadKeys()
    {
        if(isValid()) return;
        initFileLocation();
        if(0==pubdir.length() && 0==pubfile.length()) return;
        if(!GlobalData.ensureWriteableFileExists(pubdir+pubfile)) return;
        ptr_to_decode_context = readPubring(pubdir+pubfile);
        if(isValid())
        {
            // force an update of the table
            Root.instance().fireKeyringUpdate();
        }
    }
    
    private native boolean clearKeyrings();
    private native long readPubring(String filename);   
    
    private long ptr_to_decode_context = 0;

    public NativePublicKey firstPubKey()
    {
        return getFirstPubkey(ptr_to_decode_context);
    }
    private native NativePublicKey getFirstPubkey(long ptr);

    public NativeSecretKey firstSecKey()
    {
        return getFirstSeckey(ptr_to_decode_context);
    }
    private native NativeSecretKey getFirstSeckey(long ptr);
    
    
    public boolean isValid()
    {
        return ptr_to_decode_context != 0;
    }
    
}
