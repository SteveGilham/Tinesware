
//Title:      CTC2.0 for Java
//Version:    
//Copyright:  Copyright (c) 1997
//Author:     Mr. TInes
//Company:    Ravna & Tines
//Description:Free World Freeware 
//Public key encryption
package com.ravnaandtines.ctcjava;

import com.ravnaandtines.ctcjava.EncryptionParameters.Task;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.SystemColor;
import java.awt.event.*;
import javax.swing.*;

public class Application  {
    
    private JFrame shell = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private JToolBar toolBar = new JToolBar();
    private JPanel clientArea = new JPanel();
    
    private JMenu fileMenu = new JMenu(GlobalData.getResourceString("File"));
    private JMenu editMenu = new JMenu(GlobalData.getResourceString("Edit"));
    private JMenu cryptoMenu = new JMenu(GlobalData.getResourceString("Crypto"));            
    private JMenu keysMenu = new JMenu(GlobalData.getResourceString("Keys"));
    private JMenu helpMenu = new JMenu(GlobalData.getResourceString("Help"));        
    
    private JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JPanel stack = new JPanel();
    private CardLayout stackLayout = new CardLayout();
    private String rawTitle = "";
    
    // start of the stack of cards
    private JTree tree;
    private JTable main;
    
    private JLabel theStatusBar = new JLabel(" ");  //used here in callbacks & pubkey panel
    public void setStatusText(String s)
    {
        if(null == s || s.length()==0) 
            theStatusBar.setText(" ");
        else
            theStatusBar.setText(s);
    }
    
    public void updateTitle(Document d, boolean forceTreeSelection)
    {
        if(null == d)
            shell.setTitle(rawTitle);
        else
        {
            shell.setTitle(rawTitle+" \u2014 "+d.getFileName());
            // make this the selected document
            stackLayout.show(stack, "Document:"+d.hashCode()); 
            if(forceTreeSelection)
            {
                javax.swing.tree.DefaultTreeModel model = (javax.swing.tree.DefaultTreeModel) tree.getModel();
                javax.swing.tree.TreeNode[] pathArray = model.getPathToRoot(d);
                javax.swing.tree.TreePath path = new javax.swing.tree.TreePath(pathArray);
                tree.setSelectionPath(path);
            }
            
            if(!d.isTextDocument())
            {
                boolean isTextDocument = false;
                fileSend.setEnabled(isTextDocument);
                filePrint.setEnabled(isTextDocument);
                
                editCut.setEnabled(isTextDocument);
                editCopy.setEnabled(isTextDocument);
                editPaste.setEnabled(isTextDocument);
                editROT13.setEnabled(isTextDocument);
                editEnquote.setEnabled(isTextDocument);
            
                cryptoClearsign.setEnabled(isTextDocument);
            }
        }
        shell.invalidate();
        shell.repaint();
    }
    
    public void register(Document d)
    {
        if(null != d) {
            // put it in the tree
            DocumentRoot.instance().append(d);
            
            // show it
            stack.add(d.getShell(), "Document:"+d.hashCode());
        }
        updateTitle(d, true);
    }
    
    
    private FileDialog openDialog = new FileDialog(shell, GlobalData.getResourceString("Open_"), FileDialog.LOAD);

    private static Application theApplication = null;
    
    public static synchronized Application instance()
    {
        if(null == theApplication)
            theApplication = new Application();
        return theApplication;
    }
        
    public javax.swing.tree.DefaultTreeModel getTreeModel()
    {
        return (javax.swing.tree.DefaultTreeModel)tree.getModel();
    }

    Action fileNew = new AbstractAction(
            GlobalData.getResourceString("New"),
            new ImageIcon(IconSelection.FILENEW.getImage())) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            menuFileNew_actionPerformed();
        }
    };
    Action fileOpen = new AbstractAction(
            GlobalData.getResourceString("Open_"),
            new ImageIcon(IconSelection.FILEOPEN.getImage())) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            menuFileOpen_actionPerformed();
        }
    };
    Action filePOP3 = new AbstractAction(
            GlobalData.getResourceString("Read_POP3_mail_"),
            new ImageIcon(IconSelection.MAILIN.getImage())) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            menuFilePOP3_actionPerformed();
        }
    };
    Action filePref = new AbstractAction(
            GlobalData.getResourceString("Save_Preferences"),
            new ImageIcon(IconSelection.PREFS.getImage())) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            menuFileSavePref_actionPerformed();
        }
    };
    Action fileExit = new AbstractAction(
            GlobalData.getResourceString("Exit")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            menuFileExit_actionPerformed();
        }
    };
    
    Action keysGenerate = new AbstractAction(
            GlobalData.getResourceString("Generate_")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // prepare keyrings!
            PublicKeyRoot.instance().loadKeys();
            if(!PublicKeyRoot.instance().isValid()) {
                JOptionPane.showMessageDialog(
                        shell,
                        GlobalData.getResourceString("noPubring"),
                        "CTCJava",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            SecretKeyRoot.instance().loadKeys();
            if(!SecretKeyRoot.instance().isValid()) {
                JOptionPane.showMessageDialog(
                        shell,
                        GlobalData.getResourceString("noSecring"),
                        "CTCJava",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            KeyGenerationWizard w = new KeyGenerationWizard(Application.this.shell);
            if(w.show() != com.nexes.wizard.Wizard.FINISH_RETURN_CODE)
                return;
            menuKeysGen_actionPerformed(w);
        }
    };
    Action keysLock = new AbstractAction(
            GlobalData.getResourceString("Lock_All")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                menuKeysLock_actionPerformed();
        }
    };    
    
    Action keysExtract = new AbstractAction(
            GlobalData.getResourceString("Extract")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                extract_actionPerformed();
            }
        };
    Action keysSign = new AbstractAction(
            GlobalData.getResourceString("Sign")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                sign_actionPerformed();
            }
        };
    Action keysAble = new AbstractAction(
            GlobalData.getResourceString("En_Disable")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                able_actionPerformed();
            }
        };
    Action keysDelete = new AbstractAction(
            GlobalData.getResourceString("Delete")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                delete_actionPerformed();
            }
        };
    
    Action keysRevoke = new AbstractAction(
            GlobalData.getResourceString("Revoke_")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                revoke_actionPerformed();
            }
        };
    Action keysLockThis = new AbstractAction(
            GlobalData.getResourceString("Lock")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                lock_actionPerformed();
            }
        };
    Action keysUser = new AbstractAction(
            GlobalData.getResourceString("newUID")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                user_actionPerformed();
            }
        };
    Action keysPhrase = new AbstractAction(
            GlobalData.getResourceString("newPphrase")
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                phrase_actionPerformed();
            }
        };
        
        
    
    Action helpAbout = new AbstractAction(
            GlobalData.getResourceString("About_"),
            new ImageIcon(IconSelection.HELP.getImage())
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                menuHelpAbout_actionPerformed();
        }
    };    
    
    Action stopStop = new AbstractAction(
            GlobalData.getResourceString("Break"),
            new ImageIcon(IconSelection.STOP.getImage())
            ) {
        public void actionPerformed(java.awt.event.ActionEvent e) {
                break_actionPerformed();
        }
    };    
    
    
        Action fileSave = new AbstractAction(
                GlobalData.getResourceString("Save_file"),
                new ImageIcon(IconSelection.FILESAVE.getImage()))
        {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if(null == tree.getSelectionPath()) return;
                    javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuFileSave_actionPerformed();
                }            
        };
        Action fileSaveAs = new AbstractAction(
                GlobalData.getResourceString("Save_as"),
                new ImageIcon(IconSelection.FILESAVEAS.getImage()))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuFileSaveAs_actionPerformed();
            }
        };
        
        Action fileSend = new AbstractAction(
                GlobalData.getResourceString("SMTP_"),
                new ImageIcon(IconSelection.MAILOUT.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuFileSend_actionPerformed();
            }
        };
        
        Action filePrint = new AbstractAction(
                GlobalData.getResourceString("Print"),
                new ImageIcon(IconSelection.PRINT.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuFilePrint_actionPerformed();
            }
        };
        Action fileClose = new AbstractAction(
                GlobalData.getResourceString("Close_Window"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuFileClose_actionPerformed();
            }
        };

        Action editCut = new AbstractAction(
                GlobalData.getResourceString("Cut"),
                new ImageIcon(IconSelection.CUT.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuEditCut_actionPerformed();
            }
        };
        Action editCopy = new AbstractAction(
                GlobalData.getResourceString("Copy"),
                new ImageIcon(IconSelection.COPY.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuEditCopy_actionPerformed();
            }
        };
        Action editPaste = new AbstractAction(
                GlobalData.getResourceString("Paste"),
                new ImageIcon(IconSelection.PASTE.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuEditPaste_actionPerformed();
            }
        };
        
        Action editROT13 = new AbstractAction(
                GlobalData.getResourceString("ROT13"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuEditROT13_actionPerformed();
            }
        };
        Action editEnquote = new AbstractAction(
                GlobalData.getResourceString("Quote_text"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuEditEnquote_actionPerformed();
            }
        };
        
        Action cryptoDecrypt = new AbstractAction(
                GlobalData.getResourceString("Decrypt"),
                new ImageIcon(IconSelection.UNLOCK.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoDecrypt_actionPerformed();
            }
        };
        
        Action cryptoDuress = new AbstractAction(
                GlobalData.getResourceString("Extract_session_key"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoDuress_actionPerformed();
            }
        };
        Action cryptoEncrypt = new AbstractAction(
                GlobalData.getResourceString("Encrypt"),
                new ImageIcon(IconSelection.LOCK.getImage())
                )
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoEncrypt_actionPerformed();
            }
        };
        Action cryptoClearsign = new AbstractAction(
                GlobalData.getResourceString("Clearsign"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoClearsign_actionPerformed();
            }
        };
        Action cryptoSplitsign = new AbstractAction(
                GlobalData.getResourceString("Detached_signature"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoSplitsign_actionPerformed();
            }
        };
        Action cryptoSignonly = new AbstractAction(
                GlobalData.getResourceString("Sign_only"))
        {
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                if(null == tree.getSelectionPath()) return;
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) tree.getSelectionPath().getLastPathComponent();
                    if(sel instanceof Document)                    
                        ((Document)sel).menuCryptoSignonly_actionPerformed();
            }
        };
    
    //Construct the frame
    private Application() {
    
        shell.setSize(new Dimension(800, 600));
        
        if(KeyConstants.isIDEAenabled())
        {
            rawTitle = GlobalData.getResourceString("CTC_(IDEA_enabled)");
        }
        else
        {
            rawTitle = GlobalData.getResourceString("CTC_(IDEA_free)");
        }
        shell.setTitle(rawTitle);
        
        
        shell.setIconImage(IconSelection.ICON.getImage());
        shell.setResizable(false);

        shell.getContentPane().setLayout(new BorderLayout());
        theStatusBar.setBorder(BorderFactory.createEmptyBorder(0,7,0,0));
        
        shell.getContentPane().add(theStatusBar, BorderLayout.SOUTH);
        shell.getContentPane().add(splitter, BorderLayout.CENTER);
        
        tree = new JTree(Root.instance());
        tree.setCellRenderer(new TreeRenderer());
        tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                javax.swing.tree.TreeNode sel 
                        = (javax.swing.tree.TreeNode) e.getPath().getLastPathComponent();
                
                if(sel instanceof Root)
                {
                    stackLayout.show(stack, "Root:"+sel.hashCode()); 
                }
                
                boolean isSecret = (sel instanceof SecretKey);
                boolean isPublic = (sel instanceof PublicKey);
                boolean isDocument = (sel instanceof Document);
                
                Application.this.keysRevoke.setEnabled(isSecret);
                Application.this.keysLockThis.setEnabled(isSecret);
                Application.this.keysUser.setEnabled(isSecret);
                Application.this.keysPhrase.setEnabled(isSecret);
                
                Application.this.keysExtract.setEnabled(isPublic);
                Application.this.keysSign.setEnabled((isPublic &&
                        sel.getParent() == PublicKeyRoot.instance()) ||
                        (sel instanceof Username &&
                         sel.getParent().getParent() == PublicKeyRoot.instance())
                        );
                Application.this.keysAble.setEnabled(isPublic);
                Application.this.keysDelete.setEnabled(isPublic 
                        && sel.getParent() == PublicKeyRoot.instance()
                        && !((PublicKey)sel).isLocalSecret());
                
                boolean isTextDocument = false;
                Document theDocument = null;
                if(isDocument)
                {
                    theDocument = (Document)sel;
                    isTextDocument = theDocument.isTextDocument();
                }
                
                updateTitle(theDocument,false);
                
                Application.this.fileSave.setEnabled(isDocument);
                Application.this.fileSaveAs.setEnabled(isDocument);

                Application.this.fileSend.setEnabled(isTextDocument);
                Application.this.filePrint.setEnabled(isTextDocument);
                Application.this.fileClose.setEnabled(isDocument);
                
                Application.this.editCut.setEnabled(isTextDocument);
                Application.this.editCopy.setEnabled(isTextDocument);
                Application.this.editPaste.setEnabled(isTextDocument);
                Application.this.editROT13.setEnabled(isTextDocument);
                Application.this.editEnquote.setEnabled(isTextDocument);
            
                Application.this.cryptoDecrypt.setEnabled(isDocument);
                Application.this.cryptoDuress.setEnabled(isDocument);
                
                Application.this.cryptoEncrypt.setEnabled(isDocument);
                Application.this.cryptoClearsign.setEnabled(isTextDocument);
                Application.this.cryptoSplitsign.setEnabled(isDocument);
                Application.this.cryptoSignonly.setEnabled(isDocument);
        }});
        
        main = new JTable(Root.instance());        
        main.getTableHeader().setReorderingAllowed(false);
        
        TableRenderer renderer = new TableRenderer();        
        main.getTableHeader().setDefaultRenderer(renderer);
        main.setDefaultRenderer(Object.class, renderer);
        main.setRowSelectionAllowed(true);
        TableEditor editor = new TableEditor();
        main.setDefaultEditor(Object.class, editor);
        
        
        {
            JLabel tmp = new JLabel();
            int height = tmp.getFontMetrics(tmp.getFont()).getHeight()+10;
            main.setRowHeight(height);
            main.setShowGrid(false);
        }
        
        splitter.add(new JScrollPane(tree), JSplitPane.LEFT);
        splitter.add(stack, JSplitPane.RIGHT);

        stack.setLayout(stackLayout);
        
        stack.add(new JScrollPane(main), "Root:"+Root.instance().hashCode());
        stackLayout.show(stack, "Root:"+Root.instance().hashCode());

        
        shell.setJMenuBar(menuBar);
        menuBar.add(fileMenu).setMnemonic(java.awt.event.KeyEvent.VK_F);
        menuBar.add(editMenu).setMnemonic(java.awt.event.KeyEvent.VK_E);
        menuBar.add(cryptoMenu).setMnemonic(java.awt.event.KeyEvent.VK_C);
        menuBar.add(keysMenu).setMnemonic(java.awt.event.KeyEvent.VK_K);
        menuBar.add(helpMenu).setMnemonic(java.awt.event.KeyEvent.VK_H);
        
        fileMenu.add(fileNew).setMnemonic(java.awt.event.KeyEvent.VK_N);
        fileMenu.add(fileOpen).setMnemonic(java.awt.event.KeyEvent.VK_O);
        fileMenu.addSeparator();        
        fileMenu.add(fileSave).setMnemonic(java.awt.event.KeyEvent.VK_S);
        fileSave.setEnabled(false);
        fileMenu.add(fileSaveAs).setMnemonic(java.awt.event.KeyEvent.VK_A);
        fileSaveAs.setEnabled(false);
        fileMenu.addSeparator();
        fileMenu.add(filePref).setMnemonic(java.awt.event.KeyEvent.VK_P);
        fileMenu.addSeparator();
        fileMenu.add(filePOP3).setMnemonic(java.awt.event.KeyEvent.VK_R);
        fileMenu.add(fileSend).setMnemonic(java.awt.event.KeyEvent.VK_N);
        fileSend.setEnabled(false);
        fileMenu.add(filePrint).setMnemonic(java.awt.event.KeyEvent.VK_P);
        filePrint.setEnabled(false);
        fileMenu.addSeparator();
        fileMenu.add(fileClose).setMnemonic(java.awt.event.KeyEvent.VK_C);
        fileClose.setEnabled(false);
        fileMenu.addSeparator();                
        fileMenu.add(fileExit).setMnemonic(java.awt.event.KeyEvent.VK_X);
        
        editMenu.add(editCut).setMnemonic(java.awt.event.KeyEvent.VK_X);
        editCut.setEnabled(false);
        editMenu.add(editCopy).setMnemonic(java.awt.event.KeyEvent.VK_C);
        editCopy.setEnabled(false);
        editMenu.add(editPaste).setMnemonic(java.awt.event.KeyEvent.VK_P);
        editPaste.setEnabled(false);
        editMenu.addSeparator();
        editMenu.add(editROT13).setMnemonic(java.awt.event.KeyEvent.VK_R);
        editROT13.setEnabled(false);
        editMenu.add(editEnquote).setMnemonic(java.awt.event.KeyEvent.VK_Q);
        editEnquote.setEnabled(false);
       
        cryptoMenu.add(cryptoDecrypt).setMnemonic(java.awt.event.KeyEvent.VK_D);
        cryptoDecrypt.setEnabled(false);
        cryptoMenu.add(cryptoDuress).setMnemonic(java.awt.event.KeyEvent.VK_U);
        cryptoDuress.setEnabled(false);
        cryptoMenu.addSeparator();
        cryptoMenu.add(cryptoEncrypt).setMnemonic(java.awt.event.KeyEvent.VK_E);
        cryptoEncrypt.setEnabled(false);
        cryptoMenu.add(cryptoClearsign).setMnemonic(java.awt.event.KeyEvent.VK_C);
        cryptoClearsign.setEnabled(false);
        cryptoMenu.add(cryptoSplitsign).setMnemonic(java.awt.event.KeyEvent.VK_P);
        cryptoSplitsign.setEnabled(false);
        cryptoMenu.add(cryptoSignonly).setMnemonic(java.awt.event.KeyEvent.VK_S);
        cryptoSignonly.setEnabled(false);
        
        keysMenu.add(keysGenerate).setMnemonic(java.awt.event.KeyEvent.VK_G);
        keysMenu.add(keysLock).setMnemonic(java.awt.event.KeyEvent.VK_A);
        
        keysMenu.addSeparator();
        
        keysMenu.add(keysExtract).setMnemonic(java.awt.event.KeyEvent.VK_X);
        keysExtract.setEnabled(false);
        keysMenu.add(keysSign).setMnemonic(java.awt.event.KeyEvent.VK_S);
        keysSign.setEnabled(false);
        keysMenu.add(keysAble).setMnemonic(java.awt.event.KeyEvent.VK_E);
        keysAble.setEnabled(false);
        keysMenu.add(keysDelete).setMnemonic(java.awt.event.KeyEvent.VK_D);
        keysDelete.setEnabled(false);
        
        keysMenu.addSeparator();
        
        keysMenu.add(keysRevoke).setMnemonic(java.awt.event.KeyEvent.VK_R);
        keysRevoke.setEnabled(false);
        keysMenu.add(keysLockThis).setMnemonic(java.awt.event.KeyEvent.VK_L);
        keysLockThis.setEnabled(false);
        keysMenu.add(keysUser).setMnemonic(java.awt.event.KeyEvent.VK_U);
        keysUser.setEnabled(false);
        keysMenu.add(keysPhrase).setMnemonic(java.awt.event.KeyEvent.VK_P);
        keysPhrase.setEnabled(false);
        
        helpMenu.add(helpAbout).setMnemonic(java.awt.event.KeyEvent.VK_A);
        
        
        shell.getContentPane().add(toolBar, BorderLayout.NORTH);
        toolBar.add(fileNew).setToolTipText(GlobalData.getResourceString("New"));
        toolBar.add(fileOpen).setToolTipText(GlobalData.getResourceString("Open_"));
        toolBar.add(fileSave).setToolTipText(GlobalData.getResourceString("Save_file"));
        toolBar.addSeparator();
        toolBar.add(filePOP3).setToolTipText(GlobalData.getResourceString("Read_POP3_mail_"));
        toolBar.add(fileSend).setToolTipText(GlobalData.getResourceString("SMTP_"));
        toolBar.addSeparator();
        toolBar.add(filePrint).setToolTipText(GlobalData.getResourceString("Print"));
        toolBar.addSeparator();
        toolBar.add(editCut).setToolTipText(GlobalData.getResourceString("Cut"));
        toolBar.add(editCopy).setToolTipText(GlobalData.getResourceString("Copy"));
        toolBar.add(editPaste).setToolTipText(GlobalData.getResourceString("Paste"));
        toolBar.addSeparator();
        toolBar.add(cryptoDecrypt).setToolTipText(GlobalData.getResourceString("Decrypt"));
        toolBar.add(cryptoEncrypt).setToolTipText(GlobalData.getResourceString("Encrypt"));
        toolBar.addSeparator();
        toolBar.add(filePref).setToolTipText(GlobalData.getResourceString("Save_Preferences"));
        toolBar.addSeparator();
        toolBar.add(helpAbout).setToolTipText(GlobalData.getResourceString("About_"));
        toolBar.addSeparator();
        toolBar.add(stopStop).setToolTipText(GlobalData.getResourceString("Break"));
                
        shell.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                Application.this.menuFileExit_actionPerformed();
            }
            public void windowIconified(java.awt.event.WindowEvent e)
            {
                //Application.this.hideFiles();
            }
            public void windowDeiconified(java.awt.event.WindowEvent e)
            {
                //Application.this.showFiles();
            }
        } );
        shell.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
    }

    //File | Exit action performed
    public void menuFileExit_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        java.lang.System.gc();
        if(closeWindow())
        {
            openDialog.dispose();
            openDialog = null;
            shell.dispose();
            PublicKeyRoot.instance().invalidateKeyrings();
            System.exit(0);
        }
    }

    public void break_actionPerformed()
    {
        GlobalData.userbreak = true;
    }

    //Help | About action performed
    public void menuHelpAbout_actionPerformed(    /*java.awt.event.ActionEvent e*/) {        
        LicenceDialog.showAll(shell);
    }


    void menuFileNew_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        Document window = new Document();
    }

    void menuFileOpen_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        openDialog.setVisible(true);
        if(null == openDialog.getFile()) return;

        Document window = new Document();
        window.loadFile(openDialog.getDirectory(),openDialog.getFile());
    }

    void menuFilePOP3_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        POP3Wizard pop3Dialog = new POP3Wizard(shell);
        
        if(pop3Dialog.show() != com.nexes.wizard.Wizard.FINISH_RETURN_CODE)
            return;
        Vector<POP3RetrievedMessage> mails = pop3Dialog.getMessage();
        if(null == mails) return;

        for(int i = 0; i<mails.size(); ++i)
        {
            Document window = new Document();
            window.load(mails.elementAt(i));
        }

    }

    void menuFileSavePref_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        Root.instance().saveConfig();
    }

    private boolean closeWindow()
    {
        Vector<Document> pending = new Vector<Document>();
        Vector<Document> safe = new Vector<Document>();
        
        Iterator<Document> keys = DocumentRoot.instance().iterator();
        while(keys.hasNext())
        {
            Document f = keys.next();
            if(f.isModified())
                safe.add(f);
            else
                pending.add(f);
        }
        
        int state = JOptionPane.OK_OPTION;
        if(!pending.isEmpty())
            state = doPending(pending);
        
        boolean result = (state != JOptionPane.CANCEL_OPTION);
        
        if(result)
        {
            keys = safe.iterator();
            while(keys.hasNext())
            {
                keys.next().closeWindow();
            }
        }
        
        return result;
    }

    private int doPending(Vector<Document> pending)
    {
        if(pending.isEmpty())
            return JOptionPane.OK_OPTION;
        
        JPanel p = new JPanel();
        JLabel warn = new JLabel(GlobalData.getResourceString("currently_unsaved"));
        p.setLayout(new GridLayout(pending.size()+1, 1));
        p.add(warn);
        
        Vector<JCheckBox> boxes = new Vector<JCheckBox>();
        for(int i=0; i<pending.size(); ++i)
        {
            JCheckBox box = new JCheckBox(pending.elementAt(i).getFileName());
            boxes.add(box);
            p.add(box);
        }
        
        int result = JOptionPane.showConfirmDialog
                (
                shell,
                p,
                GlobalData.getResourceString("Save_changes"),
                JOptionPane.OK_CANCEL_OPTION
                );
        if(JOptionPane.CANCEL_OPTION != result) {
            for(int i=0; i<pending.size(); ++i) {
                JCheckBox box = boxes.elementAt(i);
                result = pending.elementAt(i).close(box.isSelected());
                if(JOptionPane.CANCEL_OPTION == result)
                    break;
            }            
        }
        return result;
    }
    

    public void pull(Document f)
    {
        DocumentRoot.instance().remove(f);
        stack.remove(f.getShell());
        f = null;
        System.gc();
    }


    void menuKeysGen_actionPerformed(KeyGenerationWizard w)
    {        
        int keylen = w.getKeyLength();
        KeyConstants.PKA pkalg = w.getKeyType();
        KeyConstants.PRIME primegen = w.getPrimeGeneratorType();
        KeyConstants.MDA hash = w.getHash();
        KeyConstants.CEA kpalg = w.getKeyProtectionAlgorithm();
        byte[] phrase = w.getUTF8Passphrase();
        String userID = w.getUserID();

        // Spawn off a thread to do the hard work 
        Generator worker = new Generator(pkalg, keylen, primegen,
                            userID, hash, kpalg, phrase);
        worker.start();
    }

    private class Generator extends Thread
    {
        KeyConstants.PKA pkalg;
        int keylen;
        KeyConstants.PRIME primegen;
        String uid;
        KeyConstants.MDA hash;
        KeyConstants.CEA kpAlg;
        byte[] phrase;

        Generator(KeyConstants.PKA algorithm, int keylength,
            KeyConstants.PRIME primeMethod, String name, KeyConstants.MDA hash,
            KeyConstants.CEA kpAlg, byte[] passphrase)
        {
            this.pkalg = algorithm;
            this.keylen = keylength;
            this.primegen = primeMethod;
            this.uid = name;
            this.hash = hash;
            this.kpAlg = kpAlg;
            this.phrase = passphrase;
        }
        public void run()
        {
            try {
                Application.this.shell.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                GlobalData.startPulse();
                NativeSecretKey newKey = makePKEkey(pkalg.value(), keylen, 
                        primegen.value(), uid, hash.value(), kpAlg.value(), 
                        phrase);
                
                Arrays.fill(phrase, (byte)0);
                GlobalData.stopPulse();
                if(newKey.isNull()) return;

                // generate certificates
                boolean ok = newKey.revoke(false);
                if(!ok)
                {
                  String failure = GlobalData.getResourceString("noRevoke");
                JOptionPane.showMessageDialog(
                        shell,
                        failure,
                        "CTCJava",
                        JOptionPane.ERROR_MESSAGE);
                }
                ok = newKey.getPubkey().extract();
                if(!ok)
                {
                  String failure = GlobalData.getResourceString("noExtract");
                  JOptionPane.showMessageDialog(
                        shell,
                        failure,
                        "CTCJava",
                        JOptionPane.ERROR_MESSAGE);
                }
                // and save with resets.
                PublicKeyRoot.instance().invalidateKeyrings();
            } finally {
                Application.this.shell.setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    // lock all keys and force update
    void menuKeysLock_actionPerformed()
    {
        lock();
        shell.invalidate();
        shell.repaint();
    }
    
    // === current public key operations

    void extract_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof PublicKey)) return;
        boolean ok = ((PublicKey)sel).extract();
        if(!ok)
        {
                JOptionPane.showMessageDialog(
                        shell,
                        GlobalData.getResourceString("noExtract"),
                        GlobalData.getResourceString("Reading_public_keys"),
                        JOptionPane.ERROR_MESSAGE);
       }
    }

    void sign_actionPerformed()
    {
        
        javax.swing.tree.TreeNode sel = (javax.swing.tree.TreeNode)
            tree.getSelectionPath().getLastPathComponent();
        boolean isPubkey = (null != sel) && (sel instanceof PublicKey)
            && (sel.getParent() == PublicKeyRoot.instance());

        boolean isUsername = (null != sel) && (sel instanceof Username)
            && (sel.getParent().getParent() == PublicKeyRoot.instance());
        if(!isPubkey && !isUsername) return;
        

        SecretKeyRoot.instance().loadKeys();
        if(!SecretKeyRoot.instance().isValid())
        {
            JOptionPane.showMessageDialog(
                    shell,
                    GlobalData.getResourceString("noSecring"),
                    GlobalData.getResourceString("Reading_public_keys"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextArea jta = new JTextArea();
        JScrollPane js = new JScrollPane(jta);
        jta.setText(GlobalData.getResourceString("READ_CAREFULLY:_Based") );
        jta.setEditable(false);
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(js, BorderLayout.CENTER);
        JLabel msg = new JLabel(sel.toString());                                
        jp.add(msg, BorderLayout.NORTH);
        
        int result = JOptionPane.showOptionDialog(shell,
                jp, GlobalData.getResourceString("Key_signature"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null
                );
        
        
        if(result != JOptionPane.YES_OPTION) return;

        // find the signing key...        
        int outcome = EncryptionParameters.show(EncryptionParameters.Task.SIGN_ONLY);
        if( com.nexes.wizard.Wizard.FINISH_RETURN_CODE != outcome )
        {
            return;
        }
        
        SecretKey key = EncryptionParameters.getSelectedSignatory();
        if(null == key || key.isLocked())
        {
            return;
        }
        
        PublicKey pub = null;
        javax.swing.tree.TreePath path = tree.getSelectionPath();
        if(isPubkey) {
            pub = (PublicKey) sel;
            pub.sign(key);
            pub.reset();
            
        } else {
            Username name = (Username) sel;
            pub = (PublicKey) (sel.getParent());
            name.sign(key, pub);
            pub.reset();
            path = path.getParentPath();
        }
        
        pub.reset();
        tree.collapsePath(path);
    }

    void able_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof PublicKey)) return;
        ((PublicKey)sel).able();
    }

    void delete_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof PublicKey)) return;
        
        PublicKey key = (PublicKey) sel;
        if(key.getParent() != PublicKeyRoot.instance()) return;
        if(key.isLocalSecret()) return;
        
        key.delete();
        
        Object[] path = {Root.instance(), PublicKeyRoot.instance() };
        javax.swing.tree.TreePath fullPath = new 
                javax.swing.tree.TreePath(path);
        tree.collapsePath(fullPath);        
        PublicKeyRoot.instance().reset();                
    }

    
    // === Current Secret Key operations

    void revoke_actionPerformed()
    {

        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof SecretKey)) return;
        SecretKey key = (SecretKey) sel;

        Object[] args = { key.toString() };
        
        int i = JOptionPane.showOptionDialog(shell,
                                   java.text.MessageFormat.format(
                                   GlobalData.getResourceString("RevokeKey"), args),
                                   GlobalData.getResourceString("Revocation"),
                                   JOptionPane.YES_NO_CANCEL_OPTION,
                                   JOptionPane.QUESTION_MESSAGE,
                                   null,
                                   null,
                                   null);        
        boolean ok = true;
        switch(i)
        {
        case JOptionPane.YES_OPTION:
            if(key.unlock())
            {
                ok = key.revoke(true);
            }
            break;
        case JOptionPane.NO_OPTION:
            if(key.unlock())
            {
                ok = key.revoke(false);
            }
            break;
        default:
            break;
        }
        if(!ok)
        {            
                JOptionPane.showMessageDialog(
                        shell,
                        GlobalData.getResourceString("noRevoke"),
                        GlobalData.getResourceString("Secret_keys"),
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    void lock_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof SecretKey)) return;
        SecretKey key = (SecretKey) sel;
        key.lock();
        shell.invalidate();
        shell.repaint();
    }

    void user_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof SecretKey)) return;
        SecretKey key = (SecretKey) sel;

        // get a user ID and add it with signature
        
        JPanel message = new JPanel();
        JLabel text = new JLabel(GlobalData.getResourceString("UserID:"));
        JTextField name = new JTextField(20);
        message.add(text);
        message.add(name);
        
        int i = JOptionPane.showOptionDialog(shell,
                                   message,
                                   GlobalData.getResourceString("AddNewUser"),
                                   JOptionPane.	OK_CANCEL_OPTION,
                                   JOptionPane.PLAIN_MESSAGE,
                                   null,
                                   null,
                                   null);

        switch(i)
        {
        case JOptionPane.OK_OPTION:
            if(key.unlock() && name.getText() != null &&
                !name.getText().equals(""))
            {
                key.addUser(name.getText());
            }
            break;
        default:
            break;
        }
        shell.invalidate();
        shell.repaint();
    }

    void phrase_actionPerformed()
    {
        Object sel = tree.getSelectionPath().getLastPathComponent();
        if(null == sel || ! (sel instanceof SecretKey)) return;
        SecretKey key = (SecretKey) sel;

        if(!key.unlock()) return;
        
        KeyGenerationWizard w = new KeyGenerationWizard(shell);
        if(w.showKeyProtection() != com.nexes.wizard.Wizard.FINISH_RETURN_CODE)
            return;

        KeyConstants.MDA hash = w.getHash();
        KeyConstants.CEA kpalg = w.getKeyProtectionAlgorithm();
        byte[] phrase = w.getUTF8Passphrase();

        // get the new passphrase and re-lock the key
        key.changePassphrase(hash, kpalg, phrase);
        for(int kk=0; kk<phrase.length; ++kk) phrase[kk] = 0;
        shell.invalidate();
        shell.repaint();
    }        
    
    JFrame getFrame() {return shell;}
    
    /* Generate keypair according to instructions */
    private static native NativeSecretKey makePKEkey(byte algorithm, int keylength,
                int primeMethod, String name, byte selfSignAlg, 
                byte kpAlg, byte[] passphrase);

    /* externalise all secret keys */
    private static native void lock();
    
}
//==============================================================================

