/*
 * Root.java
 *
 * Created on 27 December 2005, 17:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;

import com.ravnaandtines.ctcjava.KeyConstants.CEM;
import com.ravnaandtines.ctcjava.KeyConstants.CPA;
import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.util.Vector;
import java.util.Properties;

/**
 *
 * @author Steve
 */
public class Root implements javax.swing.tree.TreeNode, 
        javax.swing.table.TableModel {
    
    private javax.swing.table.DefaultTableModel tableNature = null;
    private Vector<javax.swing.tree.TreeNode> childNodes = new
            Vector<javax.swing.tree.TreeNode>();
    
    
    private JComboBox CEAbox = null;
    private JComboBox CEMbox = null;
    private JComboBox MDAbox = null;
    private JComboBox CPAbox = null;
    private JComboBox ARMbox = null;
    
    private JLabel signer = null;
    private JLabel quote = null;
    
    private Properties settings = null;
    
    
    /** Creates a new instance of Root */
    private Root() {
        int index;
        
        loadConfig();

        childNodes.add(PublicKeyRoot.instance());
        childNodes.add(SecretKeyRoot.instance());
        childNodes.add(DocumentRoot.instance());

        Vector<Vector<Object> > matrix = new Vector<Vector<Object> >();

        // 0 - 2
        addLabelData(matrix, "Public Keys", IconSelection.FOLDERCLOSED, "");
        addLabelData(matrix, "Secret Keys", IconSelection.FOLDERCLOSED, "");
        addLabelData(matrix, "Documents", IconSelection.FOLDERCLOSED, "");

        // 3 - 6
        addObjectData(matrix, "Public_Keyring_File", IconSelection.LEAF, 
                PublicKeyRoot.instance());
        addObjectData(matrix, "Secret_Keyring_File", IconSelection.LEAF, 
                SecretKeyRoot.instance());
        quote = addLabelData(matrix, "Quote_string", IconSelection.LEAF, 
            getProperty("QuoteString", "> "));
        signer = addLabelData(matrix, "Default_ID", IconSelection.LEAF, 
            getProperty("DefaultSigner", ""));

        // 7 - 8
        addBooleanData(matrix, "Show_passphrase", IconSelection.LEAF, 
            "ShowChecksums");
        addBooleanData(matrix, "Manual_random", IconSelection.LEAF, 
            "MouseRandom");
               
        
        CEAbox = addSelectionData(matrix, "Conventional_Encryption", 
            IconSelection.LEAF,
            KeyConstants.isIDEAenabled()?algs:algsAlt,
            "EncryptionAlgorithm",
            !KeyConstants.isIDEAenabled(),
             "0");
        CEMbox = addSelectionData(matrix, "Encryption_Mode", 
            IconSelection.LEAF,
            mode,
            "EncryptionMode",
            false,
            "0");
        MDAbox = addSelectionData(matrix, "Message_Digest", 
            IconSelection.LEAF,
            mda,
            "MessageDigestAlgorithm",
            false,
            "0");
        CPAbox = addSelectionData(matrix, "Compression_Scheme", 
            IconSelection.LEAF,
            cpa,
            "CompressionAlgorithm",
            false,
            "1");
        ARMbox = addSelectionData(matrix, "Armour_Style", 
            IconSelection.LEAF,
            arm,
            "Armour",
            false,
            "1");
        
        Vector<String> columns = new Vector<String>();
        columns.add(GlobalData.getResourceString("Item"));
        columns.add(GlobalData.getResourceString("Value"));
            
        tableNature = new javax.swing.table.DefaultTableModel(matrix, columns);
    }
    
    public String toString()
    {
        return "CTCJava";
    }
    
    public void updated() {
        int base = KeyConstants.isIDEAenabled()? 0 : 1;
 
        settings.put("EncryptionAlgorithm", Integer.toString(CEAbox.getSelectedIndex()+base));
        settings.put("EncryptionMode", Integer.toString(CEMbox.getSelectedIndex()));
        settings.put("MessageDigestAlgorithm", Integer.toString(MDAbox.getSelectedIndex()));
        settings.put("CompressionAlgorithm", Integer.toString(CPAbox.getSelectedIndex()));
        settings.put("Armour", Integer.toString(ARMbox.getSelectedIndex()));
        
        settings.put("ShowChecksums", (isVisibleChecksums() ? "true" : ""));
        settings.put("MouseRandom", (isManualRandom() ? "true" : ""));
        settings.put("DefaultSigner", signer.getText());
        settings.put("QuoteString", quote.getText());
    }
    
    public String getQuoteString()
    {
        return quote.getText();
    }
    
    public boolean isManualRandom()
    {
        return ((Boolean)tableNature.getValueAt(8,1)).booleanValue();
    }
    
    public boolean isVisibleChecksums()
    {
        return ((Boolean)tableNature.getValueAt(7,1)).booleanValue();
    }
    
    public String getDefaultSignatureID()
    {
        return signer.getText();
    }
    
    public static enum Operation { ENCRYPTSIGN, SIGNONLY, CLEARSIGN, SPLITSIGN }
    
    public void setAlgs()
    {
        setAlgs(Operation.ENCRYPTSIGN);
    }
    
    public void setAlgs(Operation op)
    {
        KeyConstants.CEA cea = algn[CEAbox.getSelectedIndex() +
                    (KeyConstants.isIDEAenabled()? 0 : 1)];
        KeyConstants.CEM cem = modn[CEMbox.getSelectedIndex()];
        KeyConstants.MDA mda = mdan[MDAbox.getSelectedIndex()];
        KeyConstants.CPA cpa = cpan[CPAbox.getSelectedIndex()];
        KeyConstants.ARM arm = armn[ARMbox.getSelectedIndex()];
        
        switch(op)
        {
            case SIGNONLY:
            cea = KeyConstants.CEA.NONE;
            cem = KeyConstants.CEM.PLAINTEXT;
            break;   
            case CLEARSIGN:
            cea = KeyConstants.CEA.NONE;
            cem = KeyConstants.CEM.PLAINTEXT;
            cpa = KeyConstants.CPA.NONE;
            arm = KeyConstants.ARM.PGP_PLAIN;
            break;
            case SPLITSIGN:
            cea = KeyConstants.CEA.NONE;
            cem = KeyConstants.CEM.PLAINTEXT;
            cpa = KeyConstants.CPA.NONE;
            arm = KeyConstants.ARM.PGP;
            break;
       }
        
        EncryptionParameters.setAlgs(cea, cem, mda, cpa, arm);
    }
    
    public KeyConstants.MDA getMDA()
    {
        return mdan[MDAbox.getSelectedIndex()];
    }
    public boolean isArmoured()
    {
        return ARMbox.getSelectedIndex() > 0;
    }
    
    private static Root theRoot = null;
    
    public static synchronized Root instance()
    {
        if(null == theRoot) theRoot = new Root();
        return theRoot;
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
        boolean possible = rowIndex > 2 && columnIndex > 0;
        if(possible)
        {
            Object o = getValueAt(rowIndex, columnIndex);
            if(o instanceof PublicKeyRoot)
                possible = !(PublicKeyRoot.instance().isValid());
            else if(o instanceof SecretKeyRoot)
                possible = !(SecretKeyRoot.instance().isValid());            
        }
        return possible;
    }
    public void removeTableModelListener(javax.swing.event.TableModelListener l)
    {
        tableNature.removeTableModelListener(l);
    }
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if(aValue instanceof Boolean)
            tableNature.setValueAt(aValue, rowIndex, columnIndex);
        updated();
    }
    
    // Self
    private void addObjectData(Vector<Vector<Object> > matrix, String key, IconSelection icon, Object value)
    {
        Vector<Object> row = new Vector<Object>();
        row.add(new JLabel(
                GlobalData.getResourceString(key),
                new ImageIcon(icon.getImage()),
                JLabel.LEFT
                ));
        row.add(value);
        matrix.add(row); 
    }    
    
    private JLabel addLabelData(Vector<Vector<Object> > matrix, String key, IconSelection icon, String value)
    {
        Vector<Object> row = new Vector<Object>();
        row.add(new JLabel(
                GlobalData.getResourceString(key),
                new ImageIcon(icon.getImage()),
                JLabel.LEFT
                ));
        JLabel label = new JLabel(value);
        row.add(label);
        matrix.add(row); 
        return label;
    }

    private void addBooleanData(Vector<Vector<Object> > matrix, String key, IconSelection icon, String value)
    {
        Vector<Object> row = new Vector<Object>();
        row.add(new JLabel(
                GlobalData.getResourceString(key),
                new ImageIcon(icon.getImage()),
                JLabel.LEFT
                ));
        Boolean b = Boolean.valueOf(
            getProperty(value, "")
            );                
        row.add(b);
        matrix.add(row);        
    }
    
    private JComboBox addSelectionData(Vector<Vector<Object> > matrix, String key, 
            IconSelection icon, String[] values, String select, boolean rebase, String def)
    {
        Vector<Object> row = new Vector<Object>();
        row.add(new JLabel(
                GlobalData.getResourceString(key),
                new ImageIcon(icon.getImage()),
                JLabel.LEFT
                ));
        JComboBox list = new JComboBox(values);
        int index = 0;
        try {
            index = Integer.decode(getProperty(select, def)).intValue();
            if(rebase && index > 0) -- index;
        } 
        catch (NumberFormatException e) {
            index = 0;
        }
        list.setSelectedIndex(index);        
        row.add(list);
        matrix.add(row); 
        return list;
    }
        
    
    static final String[] algs = {
        "IDEA_128bit", "CAST_128bit",
        "Blowfish_128bit",
        "SAFER_SK128", "TEA_128bit", "TripleDES",
        "AES128", "AES256"    };
    static final KeyConstants.CEA []   algn = {
        KeyConstants.CEA.IDEA, KeyConstants.CEA.CAST5,
        KeyConstants.CEA.GPG_BLOW16,
        KeyConstants.CEA.EBP_SAFER_MIN, KeyConstants.CEA.TEA,
        KeyConstants.CEA.TRIPLEDES,
        KeyConstants.CEA.OPGP_AES_128,
        KeyConstants.CEA.OPGP_AES_256    };
        
    static final String[] algsAlt = {
        "CAST_128bit",
        "Blowfish_128bit",
        "SAFER_SK128", "TEA_128bit", "TripleDES",
        "AES128", "AES256"    };
    static final KeyConstants.CEA []   algnAlt = {
        KeyConstants.CEA.CAST5,
        KeyConstants.CEA.GPG_BLOW16,
        KeyConstants.CEA.EBP_SAFER_MIN, KeyConstants.CEA.TEA,
        KeyConstants.CEA.TRIPLEDES,
        KeyConstants.CEA.OPGP_AES_128,
        KeyConstants.CEA.OPGP_AES_256    };
        

    static final String[] mode = {
        "CFB", "CBC"    };
    static final KeyConstants.CEM[]    modn = {
        KeyConstants.CEM.CFB, KeyConstants.CEM.CBC    };

    static final String[] cpa  = {
        "No_Compression", "Deflate", "Splay_tree"    };
    static final KeyConstants.CPA[]    cpan = {
        KeyConstants.CPA.NONE, KeyConstants.CPA.DEFLATE,
        KeyConstants.CPA.SPLAY    };

    static final String[] mda  = {
        "MD5_128bit", "SHA1_160bit",
        "RIPEM_160bit", "HAVEL_256bit"    };
    static final KeyConstants.MDA[]    mdan = {
        KeyConstants.MDA.MD5, KeyConstants.MDA.PGP5_SHA1,
        KeyConstants.MDA.PGP5_RIPEM160,KeyConstants.MDA.EBP_HAVAL_MIN    };

    static final String[] arm  = {
        "Unarmoured", "PGP_armour", "UUencode"    };
    static final KeyConstants.ARM[]      armn = {
        KeyConstants.ARM.NONE, KeyConstants.ARM.PGP,
        KeyConstants.ARM.UUENCODE    };    
        
    private String config = "";
        
    public boolean loadConfig()
    {
        if(CTC.getArgs().length > 0)
            config = CTC.getArgs()[0];
        
        while(!GlobalData.ensureWriteableFileExists(config))
        {
            // ask for the config file
            JFileChooser chooser = new JFileChooser();
            JLabel label1 = new JLabel();
            JLabel label2 = new JLabel();
            JLabel label3 = new JLabel();
            JLabel label4 = new JLabel();
            JLabel label5 = new JLabel();
            JLabel label6 = new JLabel();
            JPanel panel = new JPanel();
            panel.setLayout(new java.awt.GridLayout(6,1));
            panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
            
            panel.add(label1);
            label1.setHorizontalAlignment(JLabel.CENTER);
            panel.add(label2);
            panel.add(label3);
            label3.setHorizontalAlignment(JLabel.CENTER);
            panel.add(label4);
            panel.add(label5);
            label5.setHorizontalAlignment(JLabel.CENTER);
            panel.add(label6);
            label6.setHorizontalAlignment(JLabel.CENTER);
            label5.setText(GlobalData.getResourceString("Please_select_your"));
            label6.setText(GlobalData.getResourceString("If_the_file_does_not"));
            
            if(null == config || config.length() == 0) {
                label1.setText(GlobalData.getResourceString("This_program_expects"));
                label3.setText(GlobalData.getResourceString("Syntax:_java_ctcjava"));
            } else {
                label1.setText(GlobalData.getResourceString("Read_write_access"));
                label3.setText(GlobalData.getResourceString("Filename:")+config);
            }
                        
            chooser.setAccessory(panel);
            int returnVal = chooser.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                config = chooser.getSelectedFile().getAbsolutePath();
            } else if (returnVal == JFileChooser.ERROR_OPTION) {
            } else return false;            
        }        
        
        try{
            java.io.FileInputStream f = new java.io.FileInputStream(config);
            Properties p = new Properties();
            p.load(f);
            settings = p;
        }
        catch(Exception e)
        {
        }
        return true;
    }        
        
    public String getProperty(String key, String defaultValue)
    {
        if(null == settings)
            return defaultValue;
        return settings.getProperty(key, defaultValue);
    }
    
    public void setPublicKeyRing(String pd, String pf)
    {
         settings.put("PublicKeyringDirectory", pd);
         settings.put("PublicKeyring", pf);
    }
    
    public void setSecretKeyRing(String sd, String sf)
    {
         settings.put("SecretKeyringDirectory", sd);
         settings.put("SecretKeyring", sf);
    }
    
    public void setSMTPOptions(String server, String username, String subject)
    {
        settings.put("SMTPservername", server);
        settings.put("SMTPusername", username);
        settings.put("SMTPsubject", subject);
    }
    
    public void setPOP3Options(String server, String username)
    {
        settings.put("POP3servername", server);
        settings.put("POP3username", username);
    }
        
    public void saveConfig()
    {
        if(null == settings)
            settings = new Properties();
                
        try{
            java.io.FileOutputStream f = new java.io.FileOutputStream(config);
            settings.store(f, "CTC Java configuration file");
        }
        catch(Exception e)
        {
        }
    }
    
    public void fireKeyringUpdate()
    {
        tableNature.fireTableCellUpdated(3,1);
        tableNature.fireTableCellUpdated(4,1);
    }
    
}
