
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

// TODO: - fix the decrypt/read/dearmour action
// TODO: - refactor dtai.gwt.CJTextAreaGadget; in C++ too

package com.ravnaandtines.ctcjava;

import com.ravnaandtines.ctcjava.EncryptionParameters.Task;
import java.awt.*;

import java.awt.event.WindowEvent;
import java.io.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;

public class Document implements javax.swing.tree.TreeNode
{    
    private JPanel clientArea = new JPanel();
    
    private JTextArea fileText = new JTextArea(20,80); // called by cjcb_act.cpp getSplitPanel 

    private JPopupMenu popup = new JPopupMenu();

    private boolean isText = true; // called by cjcb_act.cpp cb_result_file
    private long modified = -2;

    private InMemoryPortIOFile binary = null;
    
    
    private static String hardCaption = null;
    private FileDialog saveDialog = null;

    // called by
    // - cjcb_act.cpp cb_result_file
    // - cjcb_act.cpp getSplitPanel    
    public Document() {
        this(null);
    }
        
    java.io.File realFile = null;
    String documentFileName = null;
    String documentDirectoryName = null;
    
    public String getFileName()
    {
        if(null == realFile)
            return documentFileName;
        String tmp = realFile.getPath();
        tmp.replace('\\','/');
        return tmp;
    }
    
    public Component getShell()
    {
        return clientArea;
    }
    
    
    public Document(java.io.File f)
    {
        synchronized(Document.class) {
            if(null == hardCaption)
                hardCaption = GlobalData.getResourceString("Save_changes");
            if(null == saveDialog)
                saveDialog = new FileDialog(Application.instance().getFrame(), //shell,
                        GlobalData.getResourceString("Save_text"), FileDialog.SAVE);
        }
        
        if(null == f)
        {
            documentFileName = GlobalData.getResourceString("Untitled")+hashCode();
            realFile = null;
        }
        else
        {
            realFile = f;  
            documentFileName = null;
        }
    
        doInit();
        
        modified = 0;
        Application.instance().register(this);
    }
        
    private void doInit()
    {        
        clientArea.setLayout(new BorderLayout());
        
        popup.add(Application.instance().editROT13).setMnemonic(java.awt.event.KeyEvent.VK_R);
        popup.add(Application.instance().editEnquote).setMnemonic(java.awt.event.KeyEvent.VK_Q);
        popup.setBackground(SystemColor.control);
        popup.setFont(new Font("Dialog", 0, 12));

        clientArea.add(new JScrollPane(fileText), BorderLayout.CENTER);
        
        // TODO: needs ^Z undo
        fileText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fileText.setColumns(73);
        fileText.setWrapStyleWord(true);
        
        fileText.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e)
            {
                fileText_textValueChanged();
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)
            {
                fileText_textValueChanged();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e)
            {
                fileText_textValueChanged();
            }        
        });
        
        fileText.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent e)
            {
                if (e.isMetaDown() && null == binary)
                {
                    popup.show((Component)e.getSource(),
                            e.getX(),e.getY());
                }
            }
        }
        );
          
    }

    void commonSave() {
        try {
        if(isText) {
            try{
                FileOutputStream f = new FileOutputStream(realFile);
                Writer w = null;
                if(null != GlobalData.encoding) try {
                    w =  new OutputStreamWriter(f, GlobalData.encoding);
                } catch ( UnsupportedEncodingException ex ) {
                }
                if(null == w) w =  new OutputStreamWriter(f);
                StringReader r = new StringReader(
                        fileText.getText());
                BufferedReader get = new BufferedReader(r);
                String line = null;
                // write each line with local line separator
                for(line = get.readLine(); line != null; line = get.readLine()) {
                    w.write(line+System.getProperty("line.separator"));
                }
                w.flush();
                w.close();
            } catch(Exception ex) {
                return;
            }
        } else {
            try{
                FileOutputStream f = new FileOutputStream(realFile);
                // push binary out
                binary.saveToStream(f);
                f.flush();
                f.close();
            } catch(Exception ex) {
                return;
            }
        }
        modified = 0;
        } finally {
            Application.instance().updateTitle(this, true);
        }
    }

    void menuFileSave_actionPerformed()
    {
        System.gc();
        
        if(null == realFile)
        {
            menuFileSaveAs_actionPerformed();
            return;
        }        
        if(modified <= 0) return;
        commonSave();
    }

    void menuFileSaveAs_actionPerformed()
    {
        System.gc();
        if(realFile == null)
            saveDialog.setFile(documentFileName);
        else
        {
            java.io.File parent = realFile.getParentFile();
            if(parent.exists() && parent.isDirectory())
                saveDialog.setDirectory(parent.getAbsolutePath());
            saveDialog.setFile(realFile.getName());
        }
        
        saveDialog.setVisible(true);
        
        String fn = saveDialog.getFile();
        String dr = saveDialog.getDirectory();
        if(null == fn || null == dr) // cancelled
        {
            return;
        }
        realFile = new java.io.File(dr+fn);
        documentFileName = null;
        commonSave();
    }

    void menuFileSend_actionPerformed()
    {
        SMTPTransmitter send = new SMTPTransmitter(Application.instance().getFrame(), //shell,
                fileText.getText());
        send.show();
    }


    void menuFilePrint_actionPerformed()
    {     
        java.awt.PrintJob pj = null;
        try {
            pj = Toolkit.getDefaultToolkit().getPrintJob(Application.instance().getFrame(), //shell,
                    "CTCjava", null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    Application.instance().getFrame(), //shell,
                    ex.getMessage(),
                    GlobalData.getResourceString("Print_Error"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(null != pj) {
            try {
                
                fileText.print(pj.getGraphics());
            } catch (Exception pex) {
            }
            pj.end();
        }
    }

    void menuFileClose_actionPerformed()
    {
        closeWindow();
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
        String s = fileText.getSelectedText();
        if(null == s || s.length() == 0)
            return;
        
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<s.length();++i)
        {
            char c = s.charAt(i);
            if(c >= 'a' && c <= 'z')
            {
                int num = ((c-'a')+13)%26;
                buf.append((char)('a'+num));
            }
            else if(c >= 'A' && c <= 'Z')
            {
                int num = ((c-'A')+13)%26;
                buf.append((char)('A'+num));
            }
            else buf.append(c);
        }
        
        fileText.replaceSelection(buf.toString());
        ++modified;
        System.gc();
    }

    void menuEditEnquote_actionPerformed()
    {
        String s = fileText.getSelectedText();
        if(null == s || s.length() == 0)
            return;
        
        String token = Root.instance().getQuoteString();
        
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<s.length();++i)
        {
            char c = s.charAt(i);
            buf.append(c);
            if(c == '\n')
            buf.append(token);
        }
        
        fileText.replaceSelection(buf.toString());
        ++modified;
        System.gc();
    }


    public boolean closeWindow()
    {
        if(modified <= 0) {
            Application.instance().pull(this);
            return true;
        }
        
        // need to block
        int result = JOptionPane.showOptionDialog(
                Application.instance().getFrame(), // argument used here
                GlobalData.getResourceString("This_file_has_been"),
                getFileName()+" "+hardCaption,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null
                );
        switch(result)
                {
            case JOptionPane.YES_OPTION:
            case JOptionPane.NO_OPTION:
                result = close(result == JOptionPane.YES_OPTION);
        }
        return result != JOptionPane.CANCEL_OPTION;
    }
    
    public int close(boolean b) {
        if(b) {
            menuFileSave_actionPerformed();
            if(modified > 0) return JOptionPane.CANCEL_OPTION;
        }
        Application.instance().pull(this);
        saveDialog.dispose();
        saveDialog = null;
        //shell.dispose();
        System.gc();
        return JOptionPane.OK_OPTION;
    }
    
    public boolean isModified()
    {
        return modified <= 0;
    }

    public void load(POP3RetrievedMessage mail)
    {
        setASCII(mail.getBuffer());
        modified = 1;
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
            } catch(IOException ioe){
                break;
            }
            if(bucket != null) {
                transit.append(bucket+System.getProperty("line.separator"));
            }
        }
        while (bucket != null);
        setASCII(transit);
        modified = 0;
    }

    // called by
    // - cjcb_act.cpp cb_result_file
    void setASCII(StringBuffer text)
    {
        fileText.setText(text.toString());
        int shown = fileText.getText().length();
        fileText.setCaretPosition(0);
        modified = 1;
        setText(true);
    }

    void setMessage(InMemoryPortIOFile tmp)
    {
        Reader loader = tmp.getReader();
        load(loader);
        if(fileText.getText() != null &&
            fileText.getText().length() > 0) modified = 1;
    }

    public void loadFile(String directory, String filename)
    {
        Application.instance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            realFile = new java.io.File(directory+filename);
            Application.instance().updateTitle(this,true);
            saveDialog.setFile(filename);
            saveDialog.setDirectory(directory);
            if(isText = GlobalData.isTextFile(directory+filename)) {
                FileInputStream f;
                try {
                    f = new FileInputStream(directory+filename);
                } catch(FileNotFoundException fnfe){
                    return;
                }
                Reader loader = null;
                if(null != GlobalData.encoding) try {
                    loader =  new InputStreamReader(f, GlobalData.encoding);
                } catch ( UnsupportedEncodingException ex ) {
                }
                if(null == loader) loader =  new InputStreamReader(f);
                load(loader);
            } else {
                InputStream loader;
                try {
                    loader = new FileInputStream(directory+filename);
                } catch(FileNotFoundException fnfe){
                    return;
                }
                setBinary( new InMemoryPortIOFile(loader) );
                modified = 0;
            }
        } finally{
            Application.instance().updateTitle(this, true);
            Application.instance().getFrame().setCursor(Cursor.getDefaultCursor());
            System.gc();
        }
    }

    // called by
    // - cjcb_act.cpp cb_result_file
    void setBinary(InMemoryPortIOFile bin)
    {
        binary = bin;
        fileText.setText(GlobalData.getResourceString("Binary_file"));
        fileText.setEditable(false);
        modified = 1;
        setText(false);
    }

    void fileText_textValueChanged()
    {
        ++modified;
    }

    private InMemoryPortIOFile getFile()
    {
        if(isText)
        {
            return new InMemoryPortIOFile(fileText.getText());
        }
        else
        {
            return binary;
        }
    }

    void doDecryption(boolean split)
    {
        if(!PublicKeyRoot.instance().isValid())
            PublicKeyRoot.instance().loadKeys();
        if(!SecretKeyRoot.instance().isValid())
            SecretKeyRoot.instance().loadKeys();
        
        GlobalData.userbreak = false;
        SecretKeyRoot.instance().loadKeys();
        
        String failure = null;
        if(!PublicKeyRoot.instance().isValid())
            failure = GlobalData.getResourceString("noPubring");
        else if(!SecretKeyRoot.instance().isValid())
            failure = GlobalData.getResourceString("noSecring");
        
        if(failure != null) {
            JOptionPane.showMessageDialog(
                    Application.instance().getFrame(),
                    failure,
                    "CTCJava",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Examiner t = new Examiner(getFile(), split, isText);
        t.start();
    }

    private class Examiner extends Thread {
        InMemoryPortIOFile f;
        boolean split;
        boolean isText;
        
        public Examiner(InMemoryPortIOFile f, boolean split, boolean isText) {
            this.f = f;
            this.split = split;
            this.isText = isText;
        }
        
        public void run() {
            try {
                Application.instance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int state = isText?
                    examine_text(f, split):
                    examine(f, split);
            } finally {
                Application.instance().getFrame().setCursor(Cursor.getDefaultCursor());
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

    private boolean prepareFile(EncryptionParameters.Task task) {
        if(!PublicKeyRoot.instance().isValid())
            PublicKeyRoot.instance().loadKeys();
        if(!SecretKeyRoot.instance().isValid())
            SecretKeyRoot.instance().loadKeys();
        
        String failure = null;
        if(!PublicKeyRoot.instance().isValid())
            failure = GlobalData.getResourceString("noPubring");
        else if(!SecretKeyRoot.instance().isValid()) {
            failure = GlobalData.getResourceString("noSecring");
        }
        
        if(failure != null) {
            JOptionPane.showMessageDialog(
                    Application.instance().getFrame(),
                    failure,
                    "CTCJava",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        EncryptionParameters.reset();
        Root.instance().setAlgs();
        
        int outcome = EncryptionParameters.show(task, null==binary);
        
        EncryptionParameters.setFile(
                EncryptionParameters.isEyesOnly() ?
                    "_CONSOLE" : getFileName(), 
                (null == binary) ? 't': 'b');
        
        return (com.nexes.wizard.Wizard.FINISH_RETURN_CODE == outcome);
    }

    private void spawn(InMemoryPortIOFile load, boolean clearsigned) {
        // create and register a new window
        Document result = new Document();
        
        if(clearsigned || Root.instance().isArmoured()) {
            result.load(load.getReader());
            result.isText = true;
            
            if(realFile == null) {
                result.documentFileName =
                        documentFileName +".asc";
            } else {
                result.realFile = new java.io.File(
                        realFile.getPath()+".asc");
            }
        } else {
            result.setBinary(load);
            result.isText = false;
            
            if(realFile == null) {
                result.documentFileName =
                        documentFileName +".pgp";
            } else {
                result.realFile = new java.io.File(
                        realFile.getPath()+".pgp");
            }
        }
        Application.instance().updateTitle(result, true);
        result.modified = 10;
    }

    void menuCryptoEncrypt_actionPerformed()
    {
        if(!prepareFile(EncryptionParameters.Task.ENCRYPTION))
            return;
        Encrypter t = new Encrypter(getFile());
        t.start();
    }

    private class Encrypter extends Thread {
        InMemoryPortIOFile f;
        
        public Encrypter(InMemoryPortIOFile f) {
            this.f = f;
        }
        
        public void run() {
            try {
                Application.instance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                InMemoryPortIOFile output = new InMemoryPortIOFile();
                boolean ok = encrypt(f, output);
                if(ok) {
                    spawn(output, false);
                }
            } finally {
                Application.instance().getFrame().setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    void menuCryptoClearsign_actionPerformed() {
        if(!prepareFile(EncryptionParameters.Task.SIGN_ONLY))
            return;
        
        Root.instance().setAlgs(Root.Operation.CLEARSIGN);
        
        Signer t = new Signer(getFile());
        t.start();
    }

    private class Signer extends Thread {
        InMemoryPortIOFile f;
        
        public Signer(InMemoryPortIOFile f) {
            this.f = f;
        }
        
        public void run() {
            try {
                Application.instance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                InMemoryPortIOFile output = new InMemoryPortIOFile();
                boolean ok = signOnly(f, output);
                if(ok) {
                    spawn(output, false);
                }
            } finally {
                Application.instance().getFrame().setCursor(Cursor.getDefaultCursor());
                System.gc();
            }
        }
    }

    void menuCryptoSplitsign_actionPerformed()
    {
        if(!prepareFile(EncryptionParameters.Task.SIGN_ONLY))
            return;
        Root.instance().setAlgs(Root.Operation.SPLITSIGN);
        
        Signer t = new Signer(getFile());
        t.start();
    }

    void menuCryptoSignonly_actionPerformed()
    {
        if(!prepareFile(EncryptionParameters.Task.SIGN_ONLY))
            return;
        
        Root.instance().setAlgs(Root.Operation.SIGNONLY);
        
        Encrypter t = new Encrypter(getFile());
        t.start();
    }

    
    JPanel notificationArea = null;
    
    // called by
    // - cjcb_act.cpp cb_result_file
    void setText(boolean text)
    {
        isText = text;
        if(isText)
            binary = null;
        if(null == notificationArea)
            return;

        JLabel type = new JLabel();
        
        if(text)
        {
            type.setIcon(new ImageIcon(IconSelection.TEXT.getImage()));
        }    
        else
        {
            type.setIcon(new ImageIcon(IconSelection.BINARY.getImage()));            
        }
        type.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        notificationArea.add(type, BorderLayout.EAST);
        clientArea.validate();
        clientArea.invalidate();
    }    
    
    // called by
    // - cjcb_act.cpp cb_result_file
    public void displaySignatureState(String name, int time, boolean ok)
    {
        if(null == notificationArea)
        {
            notificationArea = new JPanel();
            notificationArea.setLayout(new BorderLayout());
            clientArea.add(notificationArea, BorderLayout.NORTH);
            notificationArea.setBackground(SystemColor.info);
            notificationArea.setForeground(SystemColor.infoText);            
        }
        
        
        String statusText = 
                (ok ? GlobalData.getResourceString("with_good_signature") :
                    GlobalData.getResourceString("with_bad_signature") );
                
        long t = ((long)time)&0x00000000FFFFFFFFL;
        Date when = new Date(1000L*t);
        
        String format = GlobalData.getResourceString("Signed_by");
        
        String headline = MessageFormat.format(format, name, when);
        
        
        JLabel status = new JLabel(
                "<html>"+statusText+"<br>"+headline+"</html>"                
                    ,
                (ok ? new ImageIcon(IconSelection.TICK.getImage()) :
                    new ImageIcon(IconSelection.CROSS.getImage()) )
                    ,
                JLabel.LEFT
                );
        
        status.setHorizontalTextPosition(JLabel.RIGHT);
        status.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
        
        notificationArea.add(status, BorderLayout.CENTER);

        clientArea.validate();
        clientArea.invalidate();
    }
    
    // called by
    // - cjcb_act.cpp cb_result_file
    void setPath(String path)
    {
        if(path != null && path.length() > 0)
        {
            realFile = new java.io.File(path);
        }
        Application.instance().updateTitle(this,true);
    }

    
    public boolean isTextDocument()
    {
        return isText;
    }
        
    public void repaint(long tm) 
    {
        clientArea.repaint(tm);
    }
    
    // TreeNode
    public java.util.Enumeration children()
    {
        return null;
    }
    public boolean getAllowsChildren()
    {
        return false;
    }
    public javax.swing.tree.TreeNode getChildAt(int childIndex)
    {
        return null;
    }
    public int getChildCount()
    {
        return 0;
    }
    public int getIndex(javax.swing.tree.TreeNode node)
    {
        return -1;
    }
    public javax.swing.tree.TreeNode getParent()
    {
        return DocumentRoot.instance();
    }
    public boolean isLeaf()
    {
        return true;
    }    
    
    // ctclib entrypoints
    /* Examine and decypher a binary input file */
    private static native int examine(InMemoryPortIOFile input, boolean split);

    /* Examine and decypher an armoured text input file */
    private static native int examine_text(InMemoryPortIOFile input, boolean split);

    /* Encrypt file according to instructions (closes source file) */
    private static native boolean encrypt(InMemoryPortIOFile source, InMemoryPortIOFile output);

    /* Sign file according to instructions (closes source file) */
    private static native boolean signOnly(InMemoryPortIOFile source, InMemoryPortIOFile output);

}

