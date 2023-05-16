
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java.awt.Cursor;
import java11.awt.event.*;
import java.util.*;
import com.ravnaandtines.util.QSort;
import com.ravnaandtines.util.MessageBox;

public class CJPubkeyPanel extends PanelGadget implements TreeListener{
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    TreeViewGadget tree = new TreeViewGadget();
    TreeFolder root = new TreeFolder(res.getString("Public_keys"));

    BorderGadget stick = new BorderGadget();
    ButtonGadget extract = new ButtonGadget(res.getString("Extract"));
    ButtonGadget sign = new ButtonGadget(res.getString("Sign"));
    ButtonGadget able = new ButtonGadget(res.getString("En_Disable"));
    ButtonGadget delete = new ButtonGadget(res.getString("Delete"));
    GadgetGridLayout col = new GadgetGridLayout(6,1,3,3);

    MenuBarGadget menuBar1 = new MenuBarGadget();
    MenuGadget menuManage = new MenuGadget();
    MenuItemGadget menuExtract = new MenuItemGadget();
    MenuItemGadget menuSign = new MenuItemGadget();
    MenuItemGadget menuAble = new MenuItemGadget();
    MenuItemGadget menuDelete = new MenuItemGadget();

    public CJPubkeyPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{
        /* records a double-click
         *     tree.treePanel.addActionListener(new java11.awt.event.ActionListener()
         *         {
         *             public void actionPerformed(java11.awt.event.ActionEvent e)
         *             {
         *                 System.out.println("click!");;
         *             }
         *         });
         */

        tree.addTreeListener(this);

        menuExtract.setLabel(res.getString("Extract"));
        menuExtract.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                extract_actionPerformed();
            }
        }
        );
        menuSign.setLabel(res.getString("Sign"));
        menuSign.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                sign_actionPerformed();
            }
        }
        );
        menuAble.setLabel(res.getString("En_Disable"));
        menuAble.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                able_actionPerformed();
            }
        }
        );
        menuDelete.setLabel(res.getString("Delete"));
        menuDelete.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                delete_actionPerformed();
            }
        }
        );
        menuManage.setLabel(res.getString("Manage"));
        menuBar1.add(menuManage);
        menuManage.add(menuExtract);
        menuManage.add(menuSign);
        menuManage.add(menuAble);
        menuManage.add(menuDelete);

        this.setLayout(borderLayout1);
        this.add("Center", tree);
        stick.setBorderThickness(6);
        stick.setLayout(col);
        stick.add(extract);
        stick.add(sign);
        stick.add(able);
        stick.add(delete);
        this.add("North", menuBar1);
        extract.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                extract_actionPerformed();
            }
        }
        );
        sign.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                sign_actionPerformed();
            }
        }
        );
        able.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                able_actionPerformed();
            }
        }
        );
        delete.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                delete_actionPerformed();
            }
        }
        );


        treeStateChanged(null);
    }

    public void treeStateChanged(TreeEvent e)
    {
        TreeItem sel = tree.getSelectedTreeItem();

        boolean isPubkey = (null != sel) && (sel instanceof PubKeyFolder)
            && (sel.getParent() == sel.getRoot());

        boolean isSeckey = isPubkey &&
            !((PubKeyFolder)sel).toKey().getSeckey().isNull();

        boolean isUsername = (null != sel) && (sel instanceof UsernameFolder)
            && (sel.getParent().getParent() == sel.getRoot());

        menuExtract.setEnabled(isPubkey);
        menuSign.setEnabled(isPubkey||isUsername);
        menuAble.setEnabled(isPubkey);
        menuDelete.setEnabled(isPubkey && !isSeckey);

        extract.setEnabled(isPubkey);
        sign.setEnabled(isPubkey||isUsername);
        able.setEnabled(isPubkey);
        delete.setEnabled(isPubkey && !isSeckey);
    }

    public void treeNodeExpanded(TreeEvent e)
    {
    }

    public void treeNodeCondensed(TreeEvent e)
    {
    }

    void loadPubkeys()
    {
        CJGlobals.mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            CJGlobals.theStatusBar.setText(res.getString("Reading_public_keys"));
            CJGlobals.mainFrame.filesPanel.loadPubkeys();
            tree.setRoot(root);
            if(!CJGlobals.mainFrame.filesPanel.pubValid)
            {
                String failure = res.getString("noPubring");
                MessageBox messageBox = new MessageBox();
                messageBox.setFrame(CJGlobals.mainFrame);
                messageBox.setType(MessageBox.ERROR);
                messageBox.setMessage(failure);
                messageBox.show();
                return;
            }

            if(CJGlobals.decodeContext != null)
            {
                CJPubkey key = CJGlobals.decodeContext.firstPubKey();
                Vector v = new Vector(20);
                while(key != null && !key.isNull())
                {
                    PubKeyFolder kf = new PubKeyFolder(key);
                    v.addElement(kf);
                    key = key.nextPubkey();
                }

                CJGlobals.theStatusBar.setText(res.getString("Sorting_public_keys"));
                QSort.sortables(v);
                CJGlobals.theStatusBar.setText(res.getString("Loading_public_keys"));
                for(int i=0; i<v.size(); ++i)
                {
                    PubKeyFolder kf = (PubKeyFolder)(v.elementAt(i));
                    root.add(kf);
                }
            }
        }
        finally {
            CJGlobals.theStatusBar.setText(res.getString("Public_keys_loaded"));
            CJGlobals.mainFrame.setCursor(Cursor.getDefaultCursor());
            System.gc();
        }
    }

    void extract_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof PubKeyFolder)) return;
        boolean ok = ((PubKeyFolder)sel).toKey().extract();
        if(!ok)
        {
            String failure = res.getString("noExtract");
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
        }
    }

    void sign_actionPerformed()
    {
        CJGlobals.mainFrame.filesPanel.loadSeckeys();
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

        TreeItem sel = tree.getSelectedTreeItem();

        boolean isPubkey = (null != sel) && (sel instanceof PubKeyFolder)
            && (sel.getParent() == sel.getRoot());

        boolean isUsername = (null != sel) && (sel instanceof UsernameFolder)
            && (sel.getParent().getParent() == sel.getRoot());

        if(!isPubkey && !isUsername) return;

        MessageBox check = new MessageBox();
        check.setFrame(CJGlobals.mainFrame);
        check.setTitle(res.getString("Key_signature"));
        check.setMessage(sel.toString());
        check.setType(MessageBox.YES_NO);
        TextAreaGadget ta = new TextAreaGadget(
        res.getString("READ_CAREFULLY:_Based") );
        ta.setEditable(false);
        check.addInnerComponent(ta);
        check.show();
        if(check.getResult() != MessageBox.YES) return;

        // find the signing key...
        SecKeyFolder skf = CJencryptInsts.getSignatory();
        if(null == skf) return;
        if(CJencryptInsts.isCancelled())
        {
            return;
        }

        CJSeckey key = skf.toKey();
        if(key.unlock())
        {
            if(isPubkey)
            {
                ((PubKeyFolder)sel).toKey().sign(key);
                ((PubKeyFolder)sel).reset();
            }
            else
            {
                ((UsernameFolder)sel).toName().sign(key,
                ((PubKeyFolder)(sel.getParent())).toKey() );
                ((PubKeyFolder)(sel.getParent())).reset();
            }
        }
    }

    void able_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof PubKeyFolder)) return;
        ((PubKeyFolder)sel).toKey().able();
        ((PubKeyFolder)sel).able();
    }

    void delete_actionPerformed()
    {
        TreeItem sel = tree.getSelectedTreeItem();
        if(null == sel) return;
        if(!(sel instanceof PubKeyFolder)) return;
        ((PubKeyFolder)sel).toKey().delete();
        root.remove(((PubKeyFolder)sel));
    }


}
