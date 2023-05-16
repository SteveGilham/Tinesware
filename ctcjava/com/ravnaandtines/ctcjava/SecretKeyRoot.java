/*
 * SecretKeyRoot.java
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

/**
 *
 * @author Steve
 */
public class SecretKeyRoot implements javax.swing.tree.TreeNode, 
        javax.swing.table.TableModel {
    
    private javax.swing.table.DefaultTableModel tableNature = null;
    private Vector<SecretKey> childNodes = null;
    
    private java.awt.FileDialog filer = null;
    private String secfile;
    private String secdir;
    private boolean secValid = false;
    
    /** Creates a new instance of PublicKeyRoot */
    public SecretKeyRoot() {
    }
    
    private void initFileLocation()
    {
        if(null == secfile) secfile = Root.instance().getProperty("SecretKeyring", "");
        if(null == secdir) secdir  = Root.instance().getProperty("SecretKeyringDirectory", "");
    }

    public String toString()
    {
        return GlobalData.getResourceString("Secret Keys");
    }
    
    private static SecretKeyRoot theRoot = null;
    
    public static synchronized SecretKeyRoot instance()
    {
        if(null == theRoot) theRoot = new SecretKeyRoot();
        return theRoot;
    }
        
    void loadSecretKeys()
    {
        loadKeys();
        if(!isValid())
        {
                JOptionPane.showMessageDialog(
                        Application.instance().getFrame(),
                        GlobalData.getResourceString("noSecring"),
                        GlobalData.getResourceString("Secret_keys"),
                        JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(PublicKeyRoot.instance().isValid())
        {
            NativeSecretKey key = PublicKeyRoot.instance().firstSecKey();
            Vector<SecretKey> v = new Vector<SecretKey>();
            while(key != null && !key.isNull())
            {
                SecretKey kf = new SecretKey(this, key);
                v.addElement(kf);
                key = key.nextSeckey();
            }

            java.util.Collections.sort(v);
            childNodes = v;
        }
    }
    
    JList getSignatoryList()
    {
        if(null == childNodes)
            loadSecretKeys();
        if(childNodes == null)
            return null;
        
        JList nameList = new JList(childNodes);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        String signID = Root.instance().getDefaultSignatureID();
        if(signID != null && signID.length() > 0) {
            for(int i=0; i<childNodes.size(); ++i) {
                String name = childNodes.elementAt(i).toString();
                if(name.indexOf(signID) >= 0) {
                    nameList.setSelectedIndex(i);
                    break;
                }
            }
        }        
        return nameList;
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
            loadSecretKeys();
        
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
        filer.setDirectory(secdir);
        filer.setFile(secfile);
        filer.setVisible(true);
        String sd = filer.getDirectory();
        String sf = filer.getFile();
        if(sd != null && sf != null)
        {
            if(!GlobalData.ensureWriteableFileExists(sd+sf)) return;
            Root.instance().setSecretKeyRing(sd, sf);
            secdir = sd;
            secfile = sf;
            PublicKeyRoot.instance().invalidateKeyrings();
            PublicKeyRoot.instance().loadKeys();
        }
    }
    
    void loadKeys()
    {
        if(secValid) return;
        initFileLocation();
        if(0==secdir.length() && 0==secfile.length()) return;
        if(!GlobalData.ensureWriteableFileExists(secdir+secfile)) return;
        PublicKeyRoot.instance().loadKeys();
        if(PublicKeyRoot.instance().isValid())
        {
            secValid = readSecring(secdir+secfile);
        }
        if(secValid)
        {
            // force an update of the table
            Root.instance().fireKeyringUpdate();
        }
        
    }
    private native boolean readSecring(String filename);
    
    public boolean isValid()
    {
        return secValid;
    }
}
