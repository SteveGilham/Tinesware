
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Cursor;


import java11.awt.event.*;
import java.awt.event.WindowEvent;
import java.io.*;
import dtai.gwt.*;
import java.text.*;
import java.util.Date;
import com.ravnaandtines.util.IconSelection;
import com.ravnaandtines.util.MessageBox;
import com.ravnaandtines.util.Sortable;

public class CJFileFrame extends Frame
{
    GadgetPanel content = new GadgetPanel();
    BorderLayout contentLayout = new BorderLayout();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    PanelGadget body = new PanelGadget();
    GadgetBorderLayout framing = new GadgetBorderLayout();

    CJTextAreaGadget fileText = new CJTextAreaGadget(20,80);
    TextAreaGadget sigText = null;
    GadgetBorderLayout bodyLayout = new GadgetBorderLayout();
    ButtonBar buttonBar = new ButtonBar();
    GadgetGridLayout barLayout = new GadgetGridLayout(2,1);
    LabelGadget trough = new LabelGadget();
    PanelGadget bar = new PanelGadget();
    MenuBarGadget editorMenuBar = new MenuBarGadget();
    MenuGadget menu1 = new MenuGadget();
    MenuItemGadget menuFileSave = new MenuItemGadget();
    MenuItemGadget menuFileSaveAs = new MenuItemGadget();
    MenuItemGadget menuFileSend = new MenuItemGadget();
    MenuItemGadget menuFilePrint = new MenuItemGadget();
    MenuItemGadget menuFileClose = new MenuItemGadget();
    MenuGadget menu2 = new MenuGadget();
    MenuItemGadget menuEditCut = new MenuItemGadget();
    MenuItemGadget menuEditCopy = new MenuItemGadget();
    MenuItemGadget menuEditPaste = new MenuItemGadget();

    MenuItemGadget menuROT13 = new MenuItemGadget();
    MenuItemGadget menuEnquote = new MenuItemGadget();
    MenuItemGadget menuWrapQ = new MenuItemGadget();

    PopupMenuGadget popup = new PopupMenuGadget();
    MenuItemGadget popupROT13 = new MenuItemGadget();
    MenuItemGadget popupEnquote = new MenuItemGadget();
    MenuItemGadget popupWrapQ = new MenuItemGadget();

    FileDialog saveDialog = null;
    boolean isText = true;
    long modified = -2;
    String fn = null, dr = null;
    CTCJMainFrame owner;
    //  String bigString = null;
    MenuGadget menu3 = new MenuGadget();
    MenuItemGadget menuCryptoDecrypt = new MenuItemGadget();
    MenuItemGadget menuCryptoDuress = new MenuItemGadget();
    MenuItemGadget menuCryptoEncrypt = new MenuItemGadget();
    MenuItemGadget menuCryptoClearsign = new MenuItemGadget();
    MenuItemGadget menuCryptoSplitsign = new MenuItemGadget();
    MenuItemGadget menuCryptoSignonly = new MenuItemGadget();

    CJTempfile binary = null;
    String hardCaption = null;

    static final int YES = 0;
    static final int NO = 1;
    static final int CANCEL = -1;

    public CJFileFrame(CTCJMainFrame o) {
        saveDialog = new FileDialog(this,res.getString("Save_text"), FileDialog.SAVE);
        hardCaption = res.getString("Save_changes");
        try {
            jbInit();
            pack();
            owner = o;
            modified = 0;
            Object cb = CJGlobals.clip.getContents(this);
            menuEditPaste.setEnabled(cb != null);

            Point parentAt = owner.getLocation();
            Dimension d = owner.getSize();
            Dimension d2 = getSize();

            // our centre over parent centre
            Point to = new Point(
            parentAt.x+(d.width-d2.width)/2,
            parentAt.y+(d.height-d2.height)/2);

            //random displacement nearby
            to.x +=(int)((d.width+d2.width)*(Math.random()-0.5));
            to.y +=(int)((d.height+d2.height)*(Math.random()-0.5));

            // ensure on screen
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            if (to.x + d2.width > screen.width)
            {
                to.x -= screen.width-d2.width;
            }
            if(to.x < 0) to.x = 0;
            if (to.y + d2.height > screen.height)
            {
                to.y -= screen.height-d2.height;
            }
            if(to.y < 0) to.y = 0;
            setLocation(to.x, to.y);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception{
        this.setBackground(SystemColor.control);

        this.setLayout(contentLayout);
        this.add("Center", content);
        this.setBackground(SystemColor.control);

        content.setLayout(framing);
        content.add("Center", body);

        Image [] active = {
            IconSelection.getIcon(IconSelection.FILESAVE),
            IconSelection.getIcon(IconSelection.MAILOUT),null,
            IconSelection.getIcon(IconSelection.PRINT), null,
            IconSelection.getIcon(IconSelection.CUT),
            IconSelection.getIcon(IconSelection.COPY),
            IconSelection.getIcon(IconSelection.PASTE), null,
            IconSelection.getIcon(IconSelection.UNLOCK),
            IconSelection.getIcon(IconSelection.LOCK)
            };

        String [] tips = {
            res.getString("Save_file"), res.getString("SMTP_"),null,
            res.getString("Print"), null,
            res.getString("Cut"), res.getString("Copy"),
            res.getString("Paste"), null,
            res.getString("Decrypt"),res.getString("Encrypt")        };
        java11.awt.event.ActionListener [] targets = {
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFileSave_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFileSend_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuFilePrint_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuEditCut_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuEditCopy_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuEditPaste_actionPerformed();
                }
            }
            ,
            null,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuCryptoDecrypt_actionPerformed();
                }
            }
            ,
            new java11.awt.event.ActionListener()
            {
                public void actionPerformed(java11.awt.event.ActionEvent e)
                {
                    menuCryptoEncrypt_actionPerformed();
                }
            }
        };

        buttonBar.addButtons(active, null, tips, targets);

        fileText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fileText.setColumns(73);
        fileText.addTextListener(new java11.awt.event.TextListener()
        {
            public void textValueChanged(java11.awt.event.TextEvent e)
            {
                fileText_textValueChanged();
            }
        }
        );
        menu1.setLabel(res.getString("File"));
        menuFileSave.setLabel(res.getString("Save"));
        menuFileSave.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileSave_actionPerformed();
            }
        }
        );
        menuFileSaveAs.setLabel(res.getString("Save_as"));
        menuFileSaveAs.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileSaveAs_actionPerformed();
            }
        }
        );
        menuFileSend.setLabel(res.getString("SMTP_"));
        menuFileSend.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileSend_actionPerformed();
            }
        }
        );
        menuFilePrint.setLabel(res.getString("Print"));
        menuFilePrint.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFilePrint_actionPerformed();
            }
        }
        );
        menuFileClose.setLabel(res.getString("Close_Window"));
        menuFileClose.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuFileClose_actionPerformed();
            }
        }
        );

        menu2.setLabel(res.getString("Edit"));
        menuEditCut.setLabel(res.getString("Cut"));
        menuEditCut.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditCut_actionPerformed();
            }
        }
        );
        menuEditCopy.setLabel(res.getString("Copy"));
        menuEditCopy.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditCopy_actionPerformed();
            }
        }
        );
        menuEditPaste.setLabel(res.getString("Paste"));
        menuEditPaste.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditPaste_actionPerformed();
            }
        }
        );

        menu3.setLabel(res.getString("Crypto"));
        menuCryptoDecrypt.setLabel(res.getString("Decrypt"));
        menuCryptoDecrypt.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoDecrypt_actionPerformed();
            }
        }
        );
        menuCryptoDuress.setLabel(res.getString("Extract_session_key"));
        menuCryptoDuress.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoDuress_actionPerformed();
            }
        }
        );
        menuCryptoEncrypt.setLabel(res.getString("Encrypt"));
        menuCryptoEncrypt.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoEncrypt_actionPerformed();
            }
        }
        );
        menuCryptoClearsign.setLabel(res.getString("Clearsign"));
        menuCryptoClearsign.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoClearsign_actionPerformed();
            }
        }
        );
        menuCryptoSplitsign.setLabel(res.getString("Detached_signature"));
        menuCryptoSplitsign.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoSplitsign_actionPerformed();
            }
        }
        );
        menuCryptoSignonly.setLabel(res.getString("Sign_only"));
        menuCryptoSignonly.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuCryptoSignonly_actionPerformed();
            }
        }
        );


        menuROT13.setLabel(res.getString("ROT13"));
        menuROT13.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditROT13_actionPerformed();
            }
        }
        );
        menuEnquote.setLabel(res.getString("Quote_text"));
        menuEnquote.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditEnquote_actionPerformed();
            }
        }
        );
        menuWrapQ.setLabel(res.getString("WrapQuote"));
        menuWrapQ.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditWrapQ_actionPerformed();
            }
        }
        );

        popupROT13.setLabel(res.getString("ROT13"));
        popupROT13.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditROT13_actionPerformed();
            }
        }
        );
        popupEnquote.setLabel(res.getString("Quote_text"));
        popupEnquote.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditEnquote_actionPerformed();
            }
        }
        );
        popupWrapQ.setLabel(res.getString("WrapQuote"));
        popupWrapQ.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                menuEditWrapQ_actionPerformed();
            }
        }
        );

        bar.setLayout(barLayout);
        bar.add(buttonBar);
        bar.add(trough);
        body.setLayout(bodyLayout);
        fileText.setWordWrap(true);
        body.add("Center", fileText);
        body.add("North", bar);
        editorMenuBar.add(menu1);
        editorMenuBar.add(menu2);
        editorMenuBar.add(menu3);
        menu1.add(menuFileSave);
        menu1.add(menuFileSaveAs);
        menu1.addSeparator();
        menu1.add(menuFileSend);
        menu1.add(menuFilePrint);
        menu1.addSeparator();
        menu1.add(menuFileClose);

        menu2.add(menuEditCut);
        menu2.add(menuEditCopy);
        menu2.add(menuEditPaste);
        menu2.addSeparator();
        menu2.add(menuROT13);
        menu2.add(menuEnquote);
        menu2.add(menuWrapQ);

        menu3.add(menuCryptoDecrypt);
        menu3.add(menuCryptoDuress);
        menu3.addSeparator();
        menu3.add(menuCryptoEncrypt);
        menu3.add(menuCryptoClearsign);
        menu3.add(menuCryptoSplitsign);
        menu3.add(menuCryptoSignonly);

        popup.add(popupROT13);
        popup.add(popupEnquote);
        popup.add(popupWrapQ);

        this.setIconImage(IconSelection.getIcon(IconSelection.ICON));
        content.add("North", editorMenuBar);
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        this.enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);

        fileText.addMouseListener(new java11.awt.event.MouseAdapter()
        {
            public void mousePressed(java11.awt.event.MouseEvent e)
            {
                if (e.isMetaDown() && null == binary)
                {
                    popup.setBackground(SystemColor.control);
                    popup.setFont(new Font("Dialog", 0, 12));
                    popup.showMenu(e.getX(),e.getY(),(Gadget)e.getSource());
                }
            }
        }
        );
    }

    void commonSave()
    {
        if(isText)
        {
            try{
                FileOutputStream f = new FileOutputStream(dr+fn);
                Writer w = null;
                if(null != CJGlobals.encoding) try {
                    w =  new OutputStreamWriter(f, CJGlobals.encoding);
                } 
                catch ( UnsupportedEncodingException ex ) {
                }
                if(null == w) w =  new OutputStreamWriter(f);
                StringReader r = new StringReader(                /*(bigString == null) ?*/
                fileText.getText()                /*: bigString*/);
                BufferedReader get = new BufferedReader(r);
                String line = null;
                // write each line with local line separator
                for(line = get.readLine(); line != null; line = get.readLine())
                {
                    w.write(line+System.getProperty("line.separator"));
                }
                w.flush();
                w.close();
            } 
            catch(Exception ex) {
                return;
            }
        }
        else
        {
            try{
                FileOutputStream f = new FileOutputStream(dr+fn);
                // push binary out
                binary.saveToStream(f);
                f.flush();
                f.close();
            } 
            catch(Exception ex) {
                return;
            }
        }
        modified = 0;
    }

    void menuFileSave_actionPerformed()
    {
        System.gc();
        if(modified <= 0) return;
        else if(null == fn || null == dr ||
            0==fn.length() || 0==dr.length())
        {
            menuFileSaveAs_actionPerformed();
            return;
        }
        commonSave();
    }

    void menuFileSaveAs_actionPerformed()
    {
        System.gc();
        saveDialog.show();
        fn = saveDialog.getFile();
        dr = saveDialog.getDirectory();
        if(null == fn)
        {
            return;
        }
        setTitle(dr+fn);
        commonSave();
    }

    void menuFileSend_actionPerformed()
    {
        SMTPDialog send = new SMTPDialog(this);
        send.setText(fileText.getText());
        send.show();
    }


    void menuFilePrint_actionPerformed()
    {
        Frame f = new Frame(res.getString("Print_options"));
        java.awt.PrintJob pj = null;
        try {
            pj = Toolkit.getDefaultToolkit().getPrintJob(f,
            "CTCjava", CJGlobals.settings);
        }
        catch (Exception ex) {
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(this);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setTitle(res.getString("Print_Error"));
            messageBox.setMessage(ex.getMessage());
            messageBox.show();
            return;
        }
        if(null != pj)
        {
            try {
                fileText.print(pj);
            }
            catch (Exception pex) {
            }
            pj.end();
        }
    }

    void menuFileClose_actionPerformed()
    {
        closeWindow(this);
        System.gc();
    }

    void menuEditCut_actionPerformed()
    {
        fileText.cut();
        ++modified;
        System.gc();
    }

    void menuEditCopy_actionPerformed()
    {
        fileText.copy();
        System.gc();
    }

    void menuEditPaste_actionPerformed()
    {
        fileText.paste();
        ++modified;
        System.gc();
    }

    void menuEditROT13_actionPerformed() // hopelessly 7-bit ASCII
    {
        fileText.rot13();
        ++modified;
        System.gc();
    }

    void menuEditEnquote_actionPerformed()
    {
        fileText.enquote(CJGlobals.mainFrame.miscPanel.quoteField.getText());
        ++modified;
        System.gc();
    }

    void menuEditWrapQ_actionPerformed()
    {
        fileText.wrapQ();
        ++modified;
        System.gc();
    }

    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
        switch(e.getID())
        {
        case java.awt.event.WindowEvent.WINDOW_CLOSING:
            {
                closeWindow(this);
                return;
            }
        }
        super.processWindowEvent(e);
    }

    public boolean closeWindow(Frame f)
    {
        if(modified <= 0)
        {
            owner.pull(this);
            return true;
        }
        MessageBox messageBox = new MessageBox();

        messageBox.setTitle(getTitle()+" "+hardCaption);
        messageBox.setMessage(res.getString("This_file_has_been"));
        messageBox.setFrame(f);
        messageBox.setType(MessageBox.YES_NO_CANCEL);
        messageBox.show();

        // need to block
        switch(messageBox.getResult())
        {
        case YES:
            menuFileSave_actionPerformed();
            if(modified > 0) return false;
            //drop through
        case NO:
            owner.pull(this);
            saveDialog.dispose();
            saveDialog = null;
            dispose();
            System.gc();
            return true;
            // don't need to catch cancel
        }
        return false;
    }

    public void load(Message mail)
    {
        setASCII(mail.getBuffer());
        modified = 0;
    }

    private void load(Reader loader)
    {
        StringBuffer transit= new StringBuffer(1024);
        BufferedReader lines = new BufferedReader(loader);
        String bucket = null;
        do
        {
            bucket = null;
            try{
                bucket = lines.readLine();
            }
            catch(IOException ioe){
                break;
            }
            if(bucket != null)
            {
                transit.append(bucket+System.getProperty("line.separator"));
            }
        } 
        while (bucket != null);
        setASCII(transit);
        modified = 0;
    }

    void setASCII(StringBuffer text)
    {
        fileText.setText(text.toString());
        int shown = fileText.getText().length();
        //    bigString = null;
        modified = 1;
    }

    void setMessage(CJTempfile tmp)
    {
        Reader loader = tmp.getReader();
        load(loader);
        if(fileText.getText() != null &&
            fileText.getText().length() > 0) modified = 1;
    }

    public void loadFile(String directory, String filename)
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            dr = directory;
            fn = filename;
            setTitle(dr+fn);
            saveDialog.setFile(filename);
            saveDialog.setDirectory(directory);
            if(isText = CJGlobals.isTextFile(directory+filename))
            {
                FileInputStream f;
                try {
                    f = new FileInputStream(directory+filename);
                }
                catch(FileNotFoundException fnfe){
                    return;
                }
                Reader loader = null;
                if(null != CJGlobals.encoding) try {
                    loader =  new InputStreamReader(f, CJGlobals.encoding);
                } 
                catch ( UnsupportedEncodingException ex ) {
                }
                if(null == loader) loader =  new InputStreamReader(f);
                load(loader);
            }
            else
            {
                InputStream loader;
                try {
                    loader = new FileInputStream(directory+filename);
                }
                catch(FileNotFoundException fnfe){
                    return;
                }
                setBinary ( new CJTempfile(loader) );
                modified = 0;
            }
        }
        finally{
            this.setCursor(Cursor.getDefaultCursor());
            System.gc();
        }
    }

    void setBinary(CJTempfile bin)
    {
        binary = bin;
        fileText.setText(res.getString("Binary_file"));
        fileText.setEditable(false);
        modified = 1;

        // no clearsigning and no mailing for binary
        menuCryptoClearsign.setEnabled(false);
        menuFileSend.setEnabled(false);
        menuFilePrint.setEnabled(false);
        buttonBar.setVisible(1,false);
        buttonBar.setVisible(3,false);
        buttonBar.setVisible(5,false);
        buttonBar.setVisible(6,false);
        buttonBar.setVisible(7,false);
        menu2.setVisible(false);
    }



    void fileText_textValueChanged()
    {
        //Debug System.out.println("text event!");
        ++modified;
    }

    private CJTempfile getFile()
    {
        if(isText)
        {
            {
                return new CJTempfile(fileText.getText());
            }
        }
        else
        {
            return binary;
        }
    }

    void doDecryption(boolean split)
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        CJGlobals.userbreak = false;
        owner.filesPanel.loadSeckeys();

        String failure = null;
        if(!owner.filesPanel.pubValid)
            failure = res.getString("noPubring");
        else if(!owner.filesPanel.secValid)
            failure = res.getString("noSecring");

        if(failure != null)
        {
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(this);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
        }

        Examiner t = new Examiner(getFile(), split, isText);
        t.start();
    }

    private class Examiner extends Thread
    {
        CJTempfile f;
        boolean split;
        boolean isText;

        public Examiner(CJTempfile f, boolean split, boolean isText)
        {
            this.f = f;
            this.split = split;
            this.isText = isText;
        }

        public void run()
        {
            try {
                int state = isText?
                CJctclib.examine_text(f, split):
                CJctclib.examine(f, split);
            }
            finally {
                CJFileFrame.this.setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    void menuCryptoDecrypt_actionPerformed()
    {
        doDecryption(false);
    }

    void menuCryptoDuress_actionPerformed()
    {
        doDecryption(true);
    }

    private void prepareFile()
    {
        owner.filesPanel.loadSeckeys();

        String failure = null;
        boolean canSign = true;
        if(!owner.filesPanel.pubValid)
            failure = res.getString("noPubring");
        else if(!owner.filesPanel.secValid)
        {
            failure = res.getString("noSecring");
            canSign = false;
        }

        if(failure != null)
        {
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(this);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(failure);
            messageBox.show();
        }

        CJencryptInsts.reset();
        CJGlobals.mainFrame.algPanel.setAlgs();
        CJencryptInsts.setFile(getTitle(), (null == binary) ? 't': 'b');


        SecKeyFolder s = null;
        if(canSign)
        {
          s = CJencryptInsts.getSignatory();
          if(CJencryptInsts.isCancelled())
          {
            return;
          }
        }
        if(null != s) CJencryptInsts.setSignatory(s.toKey().cHandle());
    }

    private void spawn(CJTempfile load, boolean clearsigned)
    {
        // create and register a new window
        CJFileFrame result = new CJFileFrame(CJGlobals.mainFrame);
        CJGlobals.notify(result);
        result.dr = dr;

        if(clearsigned || CJGlobals.mainFrame.algPanel.ARMbox.getSelectedIndex() > 0)
        {
            result.load(load.getReader());
            result.fn = fn+".asc";
            result.isText = true;
        }
        else
        {
            result.setBinary(load);
            result.fn = fn+".pgp";
            result.isText = false;
        }
        result.setTitle(result.dr+result.fn);
        result.modified = 10;
    }

    void menuCryptoEncrypt_actionPerformed()
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        prepareFile();
        if(CJencryptInsts.isCancelled())
        {
            return;
        }
        Sortable[] s = CJencryptInsts.getRecipients();
        if(CJencryptInsts.isCancelled())
        {
            return;
        }
        for(int i=0; s != null && i<s.length; ++i)
        {
            CJencryptInsts.addRecipient(((PubKeyFolder)s[i]).toKey().cHandle());
        }

        Encrypter t = new Encrypter(getFile());
        t.start();
    }

    private class Encrypter extends Thread
    {
        CJTempfile f;

        public Encrypter(CJTempfile f)
        {
            this.f = f;
        }

        public void run()
        {
            try {
                CJTempfile output = new CJTempfile();
                boolean ok = CJctclib.encrypt(f, output);
                if(ok)
                {
                    spawn(output, false);
                }
            }
            finally {
                CJFileFrame.this.setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    void menuCryptoClearsign_actionPerformed()
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        prepareFile();
        if(CJencryptInsts.isCancelled())
        {
            return;
        }
        CJAlgPanel x=CJGlobals.mainFrame.algPanel;
        CJencryptInsts.setAlgs(0, 0, x.mdan[x.MDAbox.getSelectedIndex()],
                                  0, CTCIKeyConst.ARM_PGP_PLAIN);

        Signer t = new Signer(getFile());
        t.start();
    }

    private class Signer extends Thread
    {
        CJTempfile f;

        public Signer(CJTempfile f)
        {
            this.f = f;
        }

        public void run()
        {
            try {
                CJTempfile output = new CJTempfile();
                boolean ok = CJctclib.signOnly(f, output);
                if(ok)
                {
                    spawn(output, false);
                }
            }
            finally {
                CJFileFrame.this.setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    void menuCryptoSplitsign_actionPerformed()
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        prepareFile();
        if(CJencryptInsts.isCancelled())
        {
            return;
        }
        CJAlgPanel x=CJGlobals.mainFrame.algPanel;
        CJencryptInsts.setAlgs(0, 0, x.mdan[x.MDAbox.getSelectedIndex()],
                                  0, x.armn[x.ARMbox.getSelectedIndex()]);

        Signer t = new Signer(getFile());
        t.start();
    }

    void menuCryptoSignonly_actionPerformed()
    {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        prepareFile();
        if(CJencryptInsts.isCancelled())
        {
            return;
        }
        CJAlgPanel x=CJGlobals.mainFrame.algPanel;
        CJencryptInsts.setAlgs(CTCIKeyConst.CEA_NONE, 0,
            x.mdan[x.MDAbox.getSelectedIndex()],
            x.cpan[x.CPAbox.getSelectedIndex()],
            x.armn[x.ARMbox.getSelectedIndex()]);

        Encrypter t = new Encrypter(getFile());
        t.start();
    }

    private void setSigTextArea()
    {
        if(null == sigText)
        {
            sigText = new TextAreaGadget();
            //sigTextShell.add(sigText);
            body.add("South", sigText            /*Shell*/);
            sigText.setEditable(false);
        }
    }

    void showSig(String name, int time, boolean ok)
    {
        long t = ((long)time)&0x00000000FFFFFFFFL;
        Date when = new Date(1000L*t);
        DateFormat fmt = DateFormat.getDateTimeInstance();
        setSigTextArea();
        sigText.append(
        "File from: "+name+"\n" +
            (ok ? res.getString("with_good_signature") :
        res.getString("with_bad_signature") )
            +fmt.format(when)+"\n");
        pack();
    }
    void setPath(String dir, String file)
    {
        dr = dir;
        fn = file;
        setTitle(dr+fn);
    }

    void setText(boolean text)
    {
        setSigTextArea();
        if(text) sigText.append(res.getString("Text_file"));
        else     sigText.append(res.getString("Binary_file"));
        pack();
    }
}

