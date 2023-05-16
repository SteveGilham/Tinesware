
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;
import java.util.Vector;
import com.ravnaandtines.util.QSort;
import com.ravnaandtines.util.MessageBox;

public class CJSeckeyPanel extends PanelGadget implements TreeListener{
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    TreeViewGadget tree = new TreeViewGadget();
    BorderGadget stick = new BorderGadget();
    ButtonGadget revoke = new ButtonGadget(res.getString("Revoke_"));
    ButtonGadget lock = new ButtonGadget(res.getString("Lock"));
    ButtonGadget user = new ButtonGadget(res.getString("newUID"));
    ButtonGadget pphrase = new ButtonGadget(res.getString("newPphrase"));
    GadgetGridLayout col = new GadgetGridLayout(6,1,3,3);

    MenuBarGadget menuBar1 = new MenuBarGadget();
    MenuGadget menuManage = new MenuGadget();
    MenuItemGadget menuRevoke = new MenuItemGadget();
    MenuItemGadget menuLock = new MenuItemGadget();
    MenuItemGadget menuUser = new MenuItemGadget();
    MenuItemGadget menuPhrase = new MenuItemGadget();


    public CJSeckeyPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{
        tree.addTreeListener(this);

        menuRevoke.setLabel(res.getString("Revoke_"));
        menuRevoke.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                revoke_actionPerformed();
            }
        }
        );
        menuLock.setLabel(res.getString("Lock"));
        menuLock.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                lock_actionPerformed();
            }
        }
        );
        menuUser.setLabel(res.getString("newUID"));
        menuUser.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                user_actionPerformed();
            }
        }
        );
        menuPhrase.setLabel(res.getString("newPphrase"));
        menuPhrase.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                phrase_actionPerformed();
            }
        }
        );

        menuManage.setLabel(res.getString("Manage"));
        menuBar1.add(menuManage);
        menuManage.add(menuLock);
        menuManage.add(menuRevoke);
        menuManage.add(menuUser);
        menuManage.add(menuPhrase);
        this.setLayout(borderLayout1);
        this.add("Center", tree);
        stick.setBorderThickness(6);
        stick.setLayout(col);
        stick.add(lock);
        stick.add(revoke);
        stick.add(user);
        stick.add(pphrase);
        //    this.add("North", menuBar1);
        this.add("West", stick);

        revoke.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                revoke_actionPerformed();
            }
        }
        );
        lock.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                lock_actionPerformed();
            }
        }
        );
        user.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                user_actionPerformed();
            }
        }
        );
        pphrase.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                phrase_actionPerformed();
            }
        }
        );
        treeStateChanged(null);

    }
    public void treeStateChanged(TreeEvent e)
    {
        TreeItem sel = tree.getSelectedTreeItem();
        boolean isSeckey = (null != sel) && (sel instanceof SecKeyFolder);

        menuRevoke.setEnabled(isSeckey);
        menuLock.setEnabled(isSeckey);
        menuUser.setEnabled(isSeckey);
        menuPhrase.setEnabled(isSeckey);

        revoke.setEnabled(isSeckey);
        lock.setEnabled(isSeckey);
        user.setEnabled(isSeckey);
        pphrase.setEnabled(isSeckey);
    }

    public void treeNodeExpanded(TreeEvent e)
    {
    }

    public void treeNodeCondensed(TreeEvent e)
    {
    }

    void loadSeckeys()
    {
        CJGlobals.mainFrame.filesPanel.loadSeckeys();
        TreeFolder root = new TreeFolder(res.getString("Secret_keys"));
        tree.setRoot(root);

        if(!CJGlobals.mainFrame.filesPanel.secValid) return;
        if(!CJGlobals.mainFrame.filesPanel.secValid)
        {
            String failure = res.getString("noSecring");
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
            return;
        }
        if(CJGlobals.decodeContext != null)
        {
            CJSeckey key = CJGlobals.decodeContext.firstSecKey();
            Vector v = new Vector(20);
            while(key != null && !key.isNull())
            {
                SecKeyFolder kf = new SecKeyFolder(key);
                v.addElement(kf);
                key = key.nextSeckey();
            }

            QSort.sortables(v);
            for(int i=0; i<v.size(); ++i)
            {
                SecKeyFolder kf = (SecKeyFolder)(v.elementAt(i));
                root.add(kf);
                kf.pubkey();
            }
        }
    }

    void showLockAll()
    {
        TreeFolder root = (TreeFolder) tree.getRoot();
        if(null == root) return;
        TreeItem[] items = root.getItems();
        if(null == items) return;
        for(int i=0; i<items.length; ++i)
        {
            ((SecKeyFolder)items[i]).setImage();
        }
        invalidate();
        repaint();
    }

    void revoke_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof SecKeyFolder)) return;

        CJSeckey key = ((SecKeyFolder)sel).toKey();

        MessageBox check = new MessageBox();
        check.setType(MessageBox.YES_NO_CANCEL);
        check.setFrame(CJGlobals.mainFrame);
        check.setTitle(res.getString("Revocation"));
        Object[] args = { 
            key.getPubkey().toString()         };
        check.setMessage(java.text.MessageFormat.format(
          res.getString("RevokeKey"),
        args));
        check.show();
        int i = check.getResult();
        boolean ok = true;
        switch(i)
        {
        case MessageBox.YES:
            if(key.unlock())
            {
                ok = key.revoke(true);
            }
            break;
        case MessageBox.NO:
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
            String failure = res.getString("noRevoke");
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
        }
    }

    void lock_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof SecKeyFolder)) return;

        CJSeckey key = ((SecKeyFolder)sel).toKey();
        key.lock();
        ((SecKeyFolder)sel).setImage();
        invalidate();
        repaint();
    }

    void user_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof SecKeyFolder)) return;

        CJSeckey key = ((SecKeyFolder)sel).toKey();

        // get a user ID and add it with signature
        TextFieldGadget name = new TextFieldGadget(20);
        MessageBox check = new MessageBox();
        check.setType(MessageBox.OK_CANCEL);
        check.setFrame(CJGlobals.mainFrame);
        check.setTitle(res.getString("AddNewUser"));
        check.setMessage(res.getString("UserID:"));
        check.addInnerComponent(name);
        check.show();
        int i = check.getResult();
        switch(i)
        {
        case MessageBox.YES:
            if(key.unlock() && name.getText() != null &&
                !name.getText().equals(""))
            {
                key.addUser(name.getText());
            }
            break;
        default:
            break;
        }
        ((SecKeyFolder)sel).setImage();
        invalidate();
        repaint();
    }

    void phrase_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof SecKeyFolder)) return;

        CJSeckey key = ((SecKeyFolder)sel).toKey();

        if(!key.unlock()) return;

        boolean ok = true;
        byte hash=CTCIKeyConst.MDA_MD5, kpalg;
        byte[] phrase=null;
        do {
            CJKeyProtDlg phase3 = new CJKeyProtDlg(CJGlobals.mainFrame, res.getString("Key_protection"), true);
            phase3.showSign(false);
            phase3.show();
            if(!phase3.accept) return;

            hash = CTCIKeyConst.MDA_MD5;
            if(phase3.sha.getState()) hash = CTCIKeyConst.MDA_PGP5_SHA1;

            kpalg = CTCIKeyConst.CEA_CAST5;
            if(phase3.idea.getState()) kpalg = CTCIKeyConst.CEA_IDEA;
            else if(phase3.blow16.getState()) kpalg = CTCIKeyConst.CEA_GPG_BLOW16;
            else if(phase3.TDES.getState()) kpalg = CTCIKeyConst.CEA_3DES;
            else if(phase3.rijndael.getState()) kpalg = CTCIKeyConst.CEA_OPGP_AES_128;
            else if(phase3.TEA.getState()) kpalg = CTCIKeyConst.CEA_TEA;
            else if(phase3.tblow5.getState()) kpalg = CTCIKeyConst.CEA_BLOW5; //imply triple
            else if(phase3.tway.getState()) kpalg = CTCIKeyConst.CEA_3WAY;

            CryptoString p1 = phase3.pphrase1.phrase.getCryptoText();
            phase3.pphrase1.phrase.wipe();
            CryptoString p2 = phase3.pphrase2.phrase.getCryptoText();
            phase3.pphrase2.phrase.wipe();

            int l = p1.length();
            ok = (l == p2.length());
            for(int i=0; ok && i<l; ++i) ok = p1.charAt(i) == p2.charAt(i);

            // get byte array.
            if(ok)
            {
                phrase = new byte[p1.utf8length()];
                p1.getUTF8(phrase);
            }
            p1.wipe();
            p2.wipe();
        }
        while(!ok);

        // get the new passphrase and re-lock the key
        key.changePassphrase(hash, kpalg, phrase);
        for(int kk=0; kk<phrase.length; ++kk) phrase[kk] = 0;
        ((SecKeyFolder)sel).setImage();
        invalidate();
        repaint();
    }
}
