
//Title:      CTC2.0 for Java
//Version:    
//Copyright:  Copyright (c) 1997
//Author:     Mr. TInes
//Company:    Ravna & Tines
//Description:Free World Freeware 
//Public key encryption
package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.SystemColor;
import java11.awt.event.*;
import java.awt.event.*;
import com.ravnaandtines.util.IconSelection;
import com.ravnaandtines.util.MessageBox;

public class CTCJMainFrame extends Frame {
    GadgetPanel content = new GadgetPanel();
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    BorderLayout contentLayout = new BorderLayout();

    PanelGadget body = new PanelGadget();
    GadgetBorderLayout framing = new GadgetBorderLayout();
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    BorderGadget bevelPanel1 = new BorderGadget();
    MenuBarGadget menuBar1 = new MenuBarGadget();
    MenuGadget menuFile = new MenuGadget();
    MenuItemGadget menuFileExit = new MenuItemGadget();
    MenuGadget menuHelp = new MenuGadget();
    MenuItemGadget menuHelpAbout = new MenuItemGadget();
    MenuItemGadget menuHelpAboutIDEA = new MenuItemGadget();
    ButtonBar buttonBar = new ButtonBar();
    TabPaneGadget CJMainTabset = new TabPaneGadget();
    GadgetBorderLayout borderLayout2 = new GadgetBorderLayout();
    StatusBar statusBar1 = new StatusBar();
    CJAboutPanel aboutPanel = new CJAboutPanel();
    CJConfig configPanel = new CJConfig(this);
    CJFilesPanel filesPanel = new CJFilesPanel(this);
    CJAlgPanel algPanel = new CJAlgPanel();
    CJMiscPanel miscPanel = new CJMiscPanel();
    CJPubkeyPanel pubkeyPanel = new CJPubkeyPanel();
    CJSeckeyPanel seckeyPanel = new CJSeckeyPanel();
    boolean configNeeded = false;
    MenuItemGadget menuFileNew = new MenuItemGadget();
    MenuItemGadget menuFileOpen = new MenuItemGadget();
    MenuItemGadget menuFilePOP3 = new MenuItemGadget();
    MenuItemGadget menuFileSavePref = new MenuItemGadget();
    Hashtable openFileList = new Hashtable();
    FileDialog openDialog = new FileDialog(this, res.getString("Open_"), FileDialog.LOAD);
    MenuGadget menuKeys = new MenuGadget();
    MenuItemGadget menuKeysGen = new MenuItemGadget();
    MenuItemGadget menuKeysLock = new MenuItemGadget();

    //Construct the frame
    public CTCJMainFrame() {
        CJGlobals.mainFrame = this;
        try {
            jbInit();
            CJGlobals.theStatusBar = statusBar1;
            checkConfiguration();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkConfiguration()
    {
        if(!CJGlobals.stubFile(CJGlobals.config))
        {
            configNeeded = true;
            CJMainTabset.add(res.getString("Configuration"), configPanel);
        }
        else doUsualTabs();
    }

    void doUsualTabs()
    {
        try {
            if(configNeeded) CJMainTabset.removeItem(0);
            CJGlobals.loadConfig();
            CJMainTabset.add(res.getString("About"), aboutPanel);
            CJMainTabset.add(res.getString("Public_keys"), pubkeyPanel);
            CJMainTabset.add(res.getString("Secret_keys"), seckeyPanel);
            miscPanel.loadConfig();
            CJMainTabset.add(res.getString("Settings"), miscPanel);
            filesPanel.loadConfig();
            CJMainTabset.add(res.getString("Keyrings"), filesPanel);
            algPanel.loadConfig();
            CJMainTabset.add(res.getString("Algorithms"), algPanel);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit() throws Exception{
        this.setSize(new Dimension(400, 300));

        if(CTCIKeyConst.isIDEAenabled())
        {
            this.setTitle(res.getString("CTC_(IDEA_enabled)"));
        }
        else
        {
            this.setTitle(res.getString("CTC_(IDEA_free)"));
        }

        this.setLayout(contentLayout);
        this.add("Center", content);
        this.setBackground(SystemColor.control);

        content.setLayout(framing);
        content.add("Center", body);

        body.setLayout(borderLayout1);
        menuFile.setLabel(res.getString("File"));

        menuFileExit.setLabel(res.getString("Exit"));
        menuFileExit.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileExit_actionPerformed();
            }
        }
        );
        menuFileNew.setLabel(res.getString("New"));
        menuFileNew.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileNew_actionPerformed();
            }
        }
        );
        menuFileOpen.setLabel(res.getString("Open_"));
        menuFileOpen.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileOpen_actionPerformed();
            }
        }
        );
        menuFilePOP3.setLabel(res.getString("Read_POP3_mail_"));
        menuFilePOP3.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFilePOP3_actionPerformed();
            }
        }
        );
        menuFileSavePref.setLabel(res.getString("Save_Preferences"));
        menuFileSavePref.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileSavePref_actionPerformed();
            }
        }
        );

        menuHelp.setLabel(res.getString("Help"));
        menuHelpAbout.setLabel(res.getString("About_"));
        menuHelpAbout.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuHelpAbout_actionPerformed();
            }
        }
        );
        menuKeys.setLabel(res.getString("Keys"));
        menuKeysGen.setLabel(res.getString("Generate_"));
        menuKeysGen.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuKeysGen_actionPerformed();
            }
        }
        );
        menuKeysLock.setLabel(res.getString("Lock_All"));
        menuKeysLock.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuKeysLock_actionPerformed();
            }
        }
        );

        Image ideabutton = CTCIKeyConst.isIDEAenabled() ?
        IconSelection.getIcon(IconSelection.IDEA) : null;

        Image [] active = {
            IconSelection.getIcon(IconSelection.FILENEW),
            IconSelection.getIcon(IconSelection.FILEOPEN),
            IconSelection.getIcon(IconSelection.MAILIN),null,
            IconSelection.getIcon(IconSelection.FILESAVE), null,
            IconSelection.getIcon(IconSelection.HELP),
            ideabutton, null, IconSelection.getIcon(IconSelection.STOP)        };
        String [] tips = {
            res.getString("Create_new_file_"), res.getString("Open_file_"),
            res.getString("Read_POP3_mail_"),null,
            res.getString("Save_Configuration"), null, res.getString("Help_About_"),
            res.getString("IDEA_licence"),null, res.getString("Break")        };

        java11.awt.event.ActionListener [] targets = {
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFileNew_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFileOpen_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFilePOP3_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFileSavePref_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuHelpAbout_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuHelpAboutIDEA_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    break_actionPerformed();
                }
            }
        };
        buttonBar.addButtons(active, null, tips, targets);

        CJMainTabset.addItemListener(new java11.awt.event.ItemListener()
        {
            public void itemStateChanged(java11.awt.event.ItemEvent e)
            {
                CJMainTabset_itemChanged();
            }
        }
        );
        bevelPanel1.setBorderType(BorderGadget.THREED_OUT);
        bevelPanel1.setLayout(borderLayout2);
        menuFile.add(menuFileNew);
        menuFile.add(menuFileOpen);
        menuFile.add(menuFilePOP3);
        menuFile.addSeparator();
        menuFile.add(menuFileSavePref);
        menuFile.addSeparator();
        menuFile.add(menuFileExit);
        menuHelp.add(menuHelpAbout);
        menuKeys.add(menuKeysGen);
        menuKeys.add(menuKeysLock);

        if(CTCIKeyConst.isIDEAenabled())
        {
            menuHelp.addSeparator();
            menuHelpAboutIDEA.setLabel(res.getString("About_IDEA_"));
            menuHelp.add(menuHelpAboutIDEA);
            menuHelpAboutIDEA.addActionListener(new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuHelpAboutIDEA_actionPerformed();
                }
            }
            );
        }

        menuBar1.add(menuFile);
        menuBar1.add(menuKeys);
        menuBar1.add(menuHelp);
        this.setIconImage(IconSelection.getIcon(IconSelection.ICON));
        content.add("North", menuBar1);
        this.setResizable(false);
        body.add("North", buttonBar);
        body.add("Center", bevelPanel1);
        bevelPanel1.add("Center", CJMainTabset);
        body.add("South", statusBar1);
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
    }

    //File | Exit action performed
    public void menuFileExit_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        java.lang.System.gc();
        if(closeWindow())
        {
            openDialog.dispose();
            openDialog = null;
            dispose();
            filesPanel.invalidatePubkeys();

            // Windows leaves a lot of threads lying about,
            // alas, so we need to cull them.
            Thread here = Thread.currentThread();
            Thread[] all = new Thread[Thread.activeCount()];
            Thread.enumerate(all);
            for(int i=0; i<all.length; ++i)
            {
                if(all[i] != here) all[i].stop();
            }
            java.lang.System.runFinalization();
            System.exit(0);
        }
    }

    public void break_actionPerformed()
    {
        CJGlobals.userbreak = true;
    }

    //Help | About action performed
    public void menuHelpAbout_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        CTCJLicenceDialog dlg = new CTCJLicenceDialog(this, res.getString("About_CTC_(Freeware"));
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }

    public void menuHelpAboutIDEA_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        CTCJLicenceDialog dlg = new CTCJLicenceDialog(this, res.getString("IDEA_Licence"));
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setIDEA();
        dlg.setModal(true);
        dlg.show();
    }

    void menuFileNew_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        CJFileFrame window = new CJFileFrame(this);
        openFileList.put(window,window);
        window.setTitle(res.getString("None"));
        window.show();
    }

    void menuFileOpen_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        openDialog.show();
        if(null == openDialog.getFile()) return;

        CJFileFrame window = new CJFileFrame(this);
        openFileList.put(window,window);
        window.show();
        window.loadFile(openDialog.getDirectory(),openDialog.getFile());
    }

    void menuFilePOP3_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        POP3Dialog pop3Dialog = new POP3Dialog(this);
        pop3Dialog.show();
        Vector mails = pop3Dialog.getMessage();
        if(null == mails) return;

        for(int i = 0; i<mails.size(); ++i)
        {
            CJFileFrame window = new CJFileFrame(this);
            openFileList.put(window,window);
            window.show();
            window.load((Message)mails.elementAt(i));
        }

    }

    void notify(CJFileFrame window)
    {
        openFileList.put(window,window);
        window.show();
    }

    void menuFileSavePref_actionPerformed(    /*java.awt.event.ActionEvent e*/) {
        CJGlobals.settings.put("DefaultSigner", miscPanel.idField.getText());
        CJGlobals.settings.put("QuoteString", miscPanel.quoteField.getText());
        CJGlobals.saveConfig();
    }

    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
        switch(e.getID())
        {
        case java.awt.event.WindowEvent.WINDOW_CLOSING:
            {
                menuFileExit_actionPerformed();
            }
        case java.awt.event.WindowEvent.WINDOW_ICONIFIED:
            {
                hideFiles();
                break;
            }
        case java.awt.event.WindowEvent.WINDOW_DEICONIFIED:
            {
                showFiles();
                break;
            }
        }
        super.processWindowEvent(e);
    }

    private void hideFiles()
    {
        if(null == openFileList) return;
        Enumeration keys = openFileList.keys();
        while(keys.hasMoreElements())
        {
            Frame f = (Frame) keys.nextElement();
            f.setVisible(false);
        }
    }

    private void showFiles()
    {
        if(null == openFileList) return;
        Enumeration keys = openFileList.keys();
        while(keys.hasMoreElements())
        {
            Frame f = (Frame) keys.nextElement();
            f.setVisible(true);
        }
    }

    private boolean closeWindow()
    {
        if(null == openFileList) return true;
        do {
            Enumeration keys = openFileList.keys();
            if(!keys.hasMoreElements()) break;
            CJFileFrame f = (CJFileFrame) keys.nextElement();
            if(!f.closeWindow(this)) return false;
        } 
        while(true);
        openFileList = null;
        System.gc();
        return true;
    }

    public void echo(String s)
    {
        if(null == openFileList) return;
        Enumeration keys = openFileList.keys();
        while(keys.hasMoreElements())
        {
            ((CJFileFrame)keys.nextElement()).trough.setLabel(s);
        }
    }

    public void pull(CJFileFrame f)
    {
        openFileList.remove(f);
        f.dispose();
        f = null;
        System.gc();
    }


    void CJMainTabset_itemChanged()
    {
        if(pubkeyPanel.isShowing())
        {
            filesPanel.loadPubkeys();
            if(!filesPanel.pubValid)
            {
                String failure = res.getString("noPubring");
                com.ravnaandtines.util.MessageBox messageBox =
                    new com.ravnaandtines.util.MessageBox();
                messageBox.setFrame(this);
                messageBox.setType(com.ravnaandtines.util.MessageBox.ERROR);
                messageBox.setMessage(failure);
                messageBox.show();
            }
            else pubkeyPanel.loadPubkeys();
        }
        if(seckeyPanel.isShowing())
        {
            filesPanel.loadSeckeys();
            if(!filesPanel.secValid)
            {
                String failure = res.getString("noSecring");
                com.ravnaandtines.util.MessageBox messageBox =
                    new com.ravnaandtines.util.MessageBox();
                messageBox.setFrame(this);
                messageBox.setType(com.ravnaandtines.util.MessageBox.ERROR);
                messageBox.setMessage(failure);
                messageBox.show();
            }
            else seckeyPanel.loadSeckeys();
        }
        /*
         *        if(aboutPanel.isShowing()) System.out.println("about showing");
         *        if(filesPanel.isShowing()) System.out.println("files showing");
         *        if(algPanel.isShowing()) System.out.println("alg showing");
         *        if(miscPanel.isShowing()) System.out.println("misc showing");
         */
    }

    void menuKeysGen_actionPerformed()
    {
        // prepare keyrings!
        filesPanel.loadPubkeys();
        if(!filesPanel.pubValid)
        {
            String failure = res.getString("noPubring");
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
            return;
        }
        filesPanel.loadSeckeys();
        if(!filesPanel.secValid)
        {
            String failure = res.getString("noSecring");
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
            return;
        }

        CJKeyGenDlg phase1 = new CJKeyGenDlg(this, res.getString("Select_key_type_"), true);
        phase1.show();
        if(!phase1.accept) return;
        byte pkalg = CTCIKeyConst.PKA_GF2255;
        int keylen = 0;
        int primegen = 0;
        if(!phase1.pegwit8.getState())
        {
            if(phase1.rsa1024.getState()) keylen = 1024;
            else if (phase1.rsa2000.getState()) keylen = 2000;
            else if (phase1.rsa2048.getState()) keylen = 2048;
            else keylen = 4096;

            CJRSAGenDlg phase2 = new CJRSAGenDlg(this, res.getString("RSA_key_generation"), true);
            phase2.show();
            if(!phase2.accept) return;

            if(phase2.dualuse.getState()) pkalg = CTCIKeyConst.PKA_RSA;
            else if(phase2.encrypt.getState()) pkalg = CTCIKeyConst.PKA_RSA_ENCRYPT_ONLY;
            else pkalg = CTCIKeyConst.PKA_RSA_SIGN_ONLY;

            if(phase2.simple.getState()) primegen = 1;
            else if (phase2.jump.getState()) primegen = 2;
            else primegen = 3;
        }

        boolean ok = true;
        byte hash, kpalg;
        byte[] phrase = null;
        do {
            CJKeyProtDlg phase3 = new CJKeyProtDlg(this, res.getString("Key_protection"), true);
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
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Spawn off a thread to do the hard work 
        Generator worker = new Generator(pkalg, keylen, primegen,
        phase1.uid.getText(), hash, kpalg, phrase);
        worker.start();
    }

    private class Generator extends Thread
    {
        byte pkalg;
        int keylen;
        int primegen;
        String uid;
        byte hash;
        byte kpAlg;
        byte[] phrase;

        Generator(byte algorithm, int keylength,
        int primeMethod, String name, byte selfSignAlg,
        byte kpAlg, byte[] passphrase)
        {
            this.pkalg = algorithm;
            this.keylen = keylength;
            this.primegen = primeMethod;
            this.uid = name;
            this.hash = selfSignAlg;
            this.kpAlg = kpAlg;
            this.phrase = passphrase;
        }
        public void run()
        {
            try {
                CJGlobals.startPulse();
                CJSeckey newKey = CJctclib.makePKEkey(pkalg, keylen, primegen,
                    uid, hash, kpAlg, phrase);
                for(int kk=0; kk<phrase.length; ++kk) phrase[kk] = 0;
                CJGlobals.stopPulse();
                if(newKey.isNull()) return;

                // generate certificates
                boolean ok = newKey.revoke(false);
                if(!ok)
                {
                  String failure = res.getString("noRevoke");
                  MessageBox messageBox = new MessageBox();
                  messageBox.setFrame(CJGlobals.mainFrame);
                  messageBox.setType(MessageBox.ERROR);
                  messageBox.setMessage(failure);
                  messageBox.show();
                }
                ok = newKey.getPubkey().extract();
                if(!ok)
                {
                  String failure = res.getString("noExtract");
                  MessageBox messageBox = new MessageBox();
                  messageBox.setFrame(CJGlobals.mainFrame);
                  messageBox.setType(MessageBox.ERROR);
                  messageBox.setMessage(failure);
                  messageBox.show();
                }
                // and save with resets.
                CTCJMainFrame.this.filesPanel.invalidatePubkeys();
            } finally {
                CTCJMainFrame.this.setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }



    void menuKeysLock_actionPerformed()
    {
        CJctclib.lock();
        seckeyPanel.showLockAll();
    }
}
//==============================================================================

