//Title:        CTC2.0 for Java
//Version:
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware
//The compression signatures bit derives from PGP2.6ui's code
//as does the test for text file.  The latter is not very Kanji
//friendly, to say the least.  A far-eastern version awaits a
//person with the kit to do the port on.

package com.ravnaandtines.ctcjava;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class GlobalData {

    private GlobalData() {
    }

    static Document createDocument()
    {
        Document doc = new Document();
        return doc;
    }
    
    private static ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    static String getResourceString(String s) {return res.getString(s);}

    public static void initialiseApplication()
    {
        Root root = Root.instance();
        if(!root.loadConfig())
            return;
                
        Application mainFrame = Application.instance();
        java.awt.Frame frame = mainFrame.getFrame();
        //Pack frames that have useful preferred size info, e.g. from their layout
        //Validate frames that have preset sizes
        frame.validate();
        //Center the window
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        try {Thread.sleep(1500); } catch (InterruptedException e) {}  
        frame.setVisible(true);
        frame.toFront();        
    }
    
    
    static boolean userbreak = false; // application and document and native

    static String encoding = null; // not implemented; widely used

    public static native String libVersion();

    public static boolean ensureWriteableFileExists(String name)
    {
        if(null == name) return false;
        if(name.length() == 0) return false;

        File wantFile = new File(name);
        if(wantFile.exists())
        {
            if(!wantFile.isFile()) return false;
            return (wantFile.canWrite() && wantFile.canRead());
        }
        try {
            FileOutputStream stubber = new FileOutputStream(wantFile);
            byte [] dummy = new byte[1];
            stubber.write(dummy,0,0);
            stubber.close();
            wantFile = new File(name);
            return (wantFile.canWrite() && wantFile.canRead());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    } // Stub file

    
    static String getInputFile(int purpose)
    {
        String caption = GlobalData.getResourceString("Open_file_");
        if(0 == purpose)
            caption = GlobalData.getResourceString("Open_signed_file_");
        else if (2 == purpose)
            caption = GlobalData.getResourceString("Open_encrypted_file_");
        
        
        java.awt.FileDialog f = new java.awt.FileDialog(
                Application.instance().getFrame(),
                caption,
                java.awt.FileDialog.LOAD);
        f.setVisible(true);
        String file = f.getFile();
        if(null == file)
            return null;
        String dir = f.getDirectory();
        if(null == dir)
            return null;
        return dir+file;
    }
    
    

    static String compressSig[] =  {
        "PK\03\04", "ZOO ", "GIF8", "\352\140",
        "HPAK", "\037\213", "\037\235", "\032\013", "\032HP%"
        /* lharc is special, must be last */         };
    //static char *compressName[] = { "PKZIP",   "Zoo",  "GIF",  "Arj",
    // "Hpack", "gzip", "compressed", "PAK", "Hyper",
    // "LHarc" };
    //static char *compressExt[] =  { ".zip",  ".zoo",  ".gif",  ".arj",
    // ".hpk", ".z", ".Z", ".pak", ".hyp",
    // ".lzh" };
    /* "\032\0??", "ARC", ".arc" */

    /*---------------------------------------------------------------------------
    * Routine isTextFile()
    * Args
    *         const char* filename  file to read from
    * Return
    *         true if looks like
    * Comment
    *         NOT rewritten for port_io
    *         Return TRUE if file filename looks like a pure text file
    */
    // called by
    // - cjcb_act.cpp getInputFile    
    public static boolean isTextFile (String filename)
    {
        FileInputStream f = null;
        try{
            f = new FileInputStream(filename);
        }
        catch(FileNotFoundException fnfe) {
            return false;
        }
        byte[] buf = new byte[512];
        int n;
        // wipe more than the compress header length
        for(n=0; n<20; ++n) buf[n] = 0;
        try{
            n = f.read(buf);
        }
        catch (IOException ioe) {
            return false;
        }
        // this works on the raw byte stream,
        if(compressSignature(buf) >= 0) return false;

        // convert to string; assume text if there aren't
        // any suspicious control characters
        String sbuf = null;
        if(null != GlobalData.encoding)
        {
            try {
                sbuf = new String(buf, 0, n, GlobalData.encoding);
            }
            catch ( UnsupportedEncodingException ex ) {
            }
        }
        if(null == sbuf) sbuf = new String(buf, 0, n);
        if(null == sbuf)
        {
            //System.out.println(res.getString("No_conversion_to"));
            return false;
        }
        int bit8 = 0;
        for(int i=0; i<sbuf.length()-2; ++i) // allow for some broken stuff at the end
        {
            char c = sbuf.charAt(i);
            if(!Character.isDefined(c)) return false;
            if(Character.isISOControl(c))
            {
                /* allow BEL BS HT LF VT FF CR EOF control characters */
                /* this traps 8-bit control characters too */
                if (c < 7 || (c > '\r' && c < ' ' && c != 26)) return false;
            }
            if((c < 0x100) && ( (c & 0x80) != 0) ) ++bit8; // byte with high bit set
        }
        // assume binary if more than 1/4 bytes have 8th bit set
        // as these are accented letters, which are usually less dense than that
        // in languages using ISO-Latin - frequent accent users like Scandinavia
        // place those as 7-bit characters which will map into values > 0xFF here
        return(bit8 < n / 4);
    }    /* isTextFile */
    /*===========================================
     * Routine compressSignature()
     * Args
     *         const byte *       header        starting bytes of a file
     * Return
     *       	 Returns file signature type from a number of
     *            popular compression formats or -1 if no match
     * Comment
     *         Used to determine if file should be compressed again.
     */
    public static int compressSignature(byte[] header)
    {
        int i;
        for (i=0; i<compressSig.length; i++)
        {
            boolean match = true;
            for(int j=0; match && j < compressSig[i].length(); ++i)
            {
                // compare byte values against low-byte-only char values
                match = compressSig[i].charAt(j)==header[j];
            }
            if (match) return(i);
        }
        /* Special check for lharc files */
        if (header[2]=='-' && header[3]=='l' && (header[4]=='z'||header[4]=='h') &&
            header[6]=='-')  return(i);
        return(-1);
    }    /* compressSignature() */




    // consumed by native code only
    public static void stickUpandDie(String text)
    {
        JOptionPane.showMessageDialog(Application.instance().getFrame(), text, 
                res.getString("CTC_bug_check"), JOptionPane.ERROR_MESSAGE);
        Application.instance().menuFileExit_actionPerformed();
    }
    public static final int BADPHRASE = 0;
    public static final int OUTOFTIME = 1;
    public static final int EYESONLY = 2;
    public static final int NOSESDATA = -3;
    public static final int KEYWRITEFAILED = 3;

    // There has to be a better way that this...
    
    // Called by
    //  - cjcb_act.cpp method stickUp
    public static void stickUp(int key)
    {
        if(NOSESDATA == key) return;
        
        String message = null;
        switch(key)
        {
        case NOSESDATA:
            message = res.getString("No_session_data");
            break;
        case BADPHRASE:
            message = res.getString("Incorrect_passphrase");
            break;
        case OUTOFTIME:
            message = res.getString("Too_many_tries");
            break;
        case EYESONLY:
            message = res.getString("This_message_was");
            break;
        case KEYWRITEFAILED:
            message = res.getString("Could_not_backup_and");
            break;
        default:
            message = res.getString("What_s_up_Doc?");
            break;
        }
        JOptionPane.showMessageDialog(Application.instance().getFrame(), message, 
                res.getString("CTC_information"), JOptionPane.INFORMATION_MESSAGE);
    }

    // There has to be a better way that this...
    public static boolean cbException(int code, String text, NativePublicKey key)
    {
        // This routine is called only from ctc.c and is believed complete
        String kode = Integer.toHexString(code);
        String question = res.getString("Continue?");

        // interpret the code into sensible text
        switch (code)
        {
            // Armouring
        case 0x03020101:
            kode = res.getString("Line_limit_exhausted");
            break;
        case 0x03020201:
            kode = res.getString("User_interruption");
            break;
        case 0x03020301:
            kode = res.getString("File_I_O_error");
            break;
        case 0x03020401:
            kode = res.getString("CRC_check_in_armour");
            break;
        case 0x03020501:
            kode = res.getString("Format_error_in");
            break;
        case 0x03020601:
            kode = res.getString("Unknown_format_type");
            break;

            // Key management
        case 0x05070501:
            if(key.isRevoked())
                kode = res.getString("key_revocation_found");
            else
                kode = res.getString("New_public_key_found");
            question = res.getString("Add_to_key_ring?");
            break;
        case 0x05070601:
            kode = res.getString("New_secret_key_found");
            question = res.getString("Add_to_key_rings?");
            break;
        }
        
        int result = JOptionPane.showOptionDialog(Application.instance().getFrame(), ""+kode+" "+text+" "+key, 
                res.getString("CTC_information"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        return result == JOptionPane.YES_OPTION;
    }

    // There has to be a better way that this...
    public static void cbInformation(int code, String text, NativePublicKey key)
    {
        int severity = (code>>24) & 0xFF;
        String kode = Integer.toHexString(code);
        boolean stickup = false;

        // interpret the code into sensible text
        switch (code & 0xFFFFFF)
        {
            // CTC engine
        case 0x010101:
            kode = res.getString("Searching_for");
            break;

        case 0x010302:
            kode = res.getString("Armouring_output");
            break;

        case 0x010303:
            kode = res.getString("File_error_while");
            break;

        case 0x010500:
            kode = res.getString("Decompressing");
            if(severity < 3) kode = res.getString("While_decompressing");
            break;

        case 0x010501:
            kode = res.getString("Decompressing_message");
            break;

        case 0x010600:
        case 0x010601:
        case 0x010602:
            kode = res.getString("Computing_message");
            break;

        case 0x010702:
            kode = res.getString("Encrypting_message");
            break;

        case 0x010801:
            kode = res.getString("Decrypting_message");
            break;

        case 0x010902:
            kode = res.getString("Encrypting_message_to");
            break;

        case 0x010b02:
            kode = res.getString("Signing_from_key");
            break;

        case 0x010d01:
            kode = res.getString("No_armoured_blocks");
            break;

        case 0x010e00:
        case 0x010e01:
        case 0x010e02:
            kode = res.getString("Unimplemented_message");
            break;

        case 0x010f00:
        case 0x010f01:
        case 0x010f02:
            kode = res.getString("Unimplemented");
            break;

        case 0x011301:
        case 0x011302:
            kode = res.getString("Could_not_open");
            break;

        case 0x011601:
            kode = res.getString("Bad_session_key");
            break;

        case 0x011700:
        case 0x011701:
        case 0x011702:
            kode = res.getString("File_input_error");
            break;

        case 0x011800:
        case 0x011801:
        case 0x011802:
            kode = res.getString("File_output_error");
            break;

        case 0x011901:
            kode = res.getString("Only_public_part_of");
            stickup = true;
            break;

        case 0x011A04:
            kode = res.getString("Public_key");
            break;

        case 0x011B01:
            kode = res.getString("Unknown_cypher_type");
            break;

        case 0x011f00:
        case 0x011f01:
        case 0x011f02:
            kode = res.getString("No_memory_for_digest");
            break;

        case 0x012001:
            kode = res.getString("PGP2_6_style_comment");
            break;

        case 0x012101:
            kode = res.getString("No_secret_key");
            break;

        case 0x012304:
            kode = res.getString("Non_signature_data_in");
            break;

        case 0x020601:
        case 0x020602:
        case 0x020603:
        case 0x020604:
        case 0x020605:
        case 0x020606:
            kode = res.getString("Armour_failure");
            break;

            // Public key operations
        case 0x060101:
            kode = res.getString("Out_of_memory_while");
            break;

        case 0x060102:
            kode = res.getString("Out_of_memory_encrypt");
            break;

        case 0x060103:
            kode = res.getString("Out_of_memory_sign");
            break;

        case 0x060104:
            kode = res.getString("Out_of_memory_verify");
            break;

        case 0x060105:
            kode = res.getString("Out_of_memory_keygen");
            break;

        case 0x060201:
            kode = res.getString("User_interrupt_taken");
            break;

        case 0x060202:
            kode = res.getString("User_interrupt_pke");
            break;

        case 0x060203:
            kode = res.getString("User_interrupt_sign");
            break;

        case 0x060204:
            kode = res.getString("User_interrupt_verify");
            break;

        case 0x060205:
            kode = res.getString("User_interrupt_keygen");
            break;

        case 0x060302:
            kode = res.getString("File_I_O_error_while");
            break;

        case 0x060305:
            kode = res.getString("File_I_O_error_keygen");
            break;

        case 0x060401:
            kode = res.getString("Check_data_not_found");
            break;

        case 0x060402:
            kode = res.getString("unrecognisedCKey");
            break;

        case 0x060403:
        case 0x060804:
            kode = res.getString("unrecognised_message");
            break;

        case 0x060404:
            kode = res.getString("message_digest_does");
            break;

        case 0x060601:
            kode = res.getString("Required_conventional");
            break;

        case 0x060701:
            kode = res.getString("Required_mode_unavail");
            break;

        case 0x060902:
        case 0x060904:
            kode = res.getString("Public_key_too_short");
            break;

        case 0x060a01:
            kode = res.getString("Fatal_error_in_public");
            break;

        case 0x060a02:
            kode = res.getString("Fatal_error_in_PKE");
            break;

        case 0x060a03:
            kode = res.getString("Fatal_error_in_sig");
            break;

        case 0x060a04:
            kode = res.getString("Fatal_error_in_ver");
            break;

        case 0x060a05:
            kode = res.getString("Fatal_error_in_keygen");
            break;

        case 0x060b05:
            kode = res.getString("Key_generation_begun");
            break;

        case 0x060c05:
            kode = res.getString("Generating_first");
            break;

        case 0x060d05:
            kode = res.getString("Generating_second");
            break;

        case 0x060e05:
            kode = res.getString("Concluding_key");
            break;

        case 0x060f01:
            kode = res.getString("Required_public_key");
            break;

        case 0x060f02:
            kode = res.getString("Required_pkalg_unavail");
            break;

        case 0x060f03:
            kode = res.getString("Required_pksig_unavail");
            break;

        case 0x060f04:
            kode = res.getString("Required_pkver_unavail");
            break;

            // Key manager
        case 0x070208:
            kode = res.getString("Unexpected_duplicate");
            break;

        case 0x070308:
            kode = res.getString("Key_in_inconsistent");
            break;

        case 0x070408:
            kode = res.getString("Could_not_read");
            break;

        case 0x070701:
            kode = res.getString("Public_key_found");
            break;

        case 0x070801:
            kode = res.getString("Secret_key_for");
            break;

        case 0x070901:
            kode = res.getString("User_ID_for");
            break;

        case 0x070A01:
            kode = res.getString("Key_signature_found");
            break;

        case 0x070b01:
            kode = res.getString("Key_without_userID");
            break;

        case 0x070d01:
            kode = res.getString("Could_not_validate");
            break;

        case 0x070e01:
            kode = res.getString("Bad_key_revocation");
            break;

        case 0x070f01:
            kode = res.getString("Improperly_revoked");
            break;

        case 0x071001:
            kode = res.getString("Bad_key_signature");
            break;

        case 0x071101:
            kode = res.getString("Key_signature_unknown");
            break;

            // I/O processing
        case 0x080101:
        case 0x080107:
            kode = res.getString("Unsupported_version");
            break;

        case 0x080201:
        case 0x080207:
            kode = res.getString("Unsupported_algorithm");
            break;

        case 0x080301:
        case 0x080307:
            kode = res.getString("File_I_O_error");
            break;

        case 0x080401:
        case 0x080407:
            kode = res.getString("Bad_length_value_in");
            break;

        case 0x080501:
        case 0x080507:
            kode = res.getString("Unexpected_record");
            break;

        case 0x080601:
        case 0x080607:
            kode = res.getString("File_does_not_appear");
            break;

        case 0x080701:
        case 0x080707:
            kode = res.getString("Secret_key_not");
            break;

        case 0x080801:
        case 0x080807:
            kode = res.getString("No_memory_for_I_O");
            break;

        default:
            stickup = true;
        }


        if(6 == severity && !stickup)
        {
            Application.instance().setStatusText(res.getString("STATUS:")+kode+":"+text+":"+key);
            return;
        }
        else if(5 == severity && !stickup)
        {
            Application.instance().setStatusText(res.getString("INFO:")+kode+":"+text+":"+key);
            return;
        }

        kode = kode+":"+text+":"+key;
        String title = res.getString("CTC_information");
        int type = JOptionPane.INFORMATION_MESSAGE;
        switch(severity)
        {
        case 1:
            type = JOptionPane.ERROR_MESSAGE;
            title = res.getString("CTC_CRASH");
            break;
        case 2:
            type = JOptionPane.ERROR_MESSAGE;
            title = res.getString("CTC_FATAL_ERROR");
            break;
        case 3:
            type = JOptionPane.ERROR_MESSAGE;
            break;
        case 4:
            type = JOptionPane.WARNING_MESSAGE;
            break;
        case 5:
            break;
        case 6:
            title = res.getString("CTC_STATUS");
            break;
        }
        
        JOptionPane.showMessageDialog(Application.instance().getFrame(), 
                kode, title, type);
        
        
    }

    // called by native code only
    // - cjcb_act.cpp selectID
    public static int selectID(String [] names)
    {
        JList nameList = new JList(names);
        nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        int mode = JOptionPane.showOptionDialog(
                Application.instance().getFrame(),
                new JScrollPane(nameList),
                res.getString("Select_secret_key_for"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null
                );
        if(JOptionPane.CANCEL_OPTION == mode)
            return -1;
        
        return nameList.getSelectedIndex();
    }

    // called by native code only
    // - cjcb_act.cpp getPassphrase
    public static int getPassphrase(String keyID, byte [] ans)
    {
        final GetPassphraseUI core = new GetPassphraseUI();
        JPanel message = new JPanel();
        message.setLayout(new BorderLayout());
        message.add(core.getShell(), BorderLayout.CENTER);
        message.add(new JLabel(res.getString("Key:")+keyID), BorderLayout.NORTH);
        JLabel caps = new JLabel(/*res.getString("CAPS")*/ "");
        message.add(caps, BorderLayout.SOUTH);
        
        JOptionPane pane = new JOptionPane(message);
        JDialog dialog = pane.createDialog(Application.instance().getFrame(),
                res.getString("Enter_passphrase"));
        pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        pane.setIcon(null);
        
        dialog.addWindowListener(
                new java.awt.event.WindowListener() {
            public void windowActivated(WindowEvent e) {
                core.phrase.setRequestFocusEnabled(true);
                core.phrase.requestFocusInWindow();
            }
            public void windowClosed(WindowEvent e) {
            }
            public void windowClosing(WindowEvent e) {
            }
            public void windowDeactivated(WindowEvent e) {
            }
            public void windowDeiconified(WindowEvent e) {
            }
            public void windowIconified(WindowEvent e) {
            }
            public void windowOpened(WindowEvent e) {
                core.phrase.setRequestFocusEnabled(true);
                core.phrase.requestFocusInWindow();
            }
        }
        );
        dialog.setVisible(true);
        int mode = JOptionPane.CANCEL_OPTION;
        Object selectedValue = pane.getValue();
        if(selectedValue instanceof Integer)
            mode = ((Integer)selectedValue).intValue();
                
        /*
        int mode = JOptionPane.showOptionDialog(
                Application.instance().getFrame(),
                message,
                res.getString("Enter_passphrase"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null
                );*/
        
        if(JOptionPane.CANCEL_OPTION == mode)
            return -1;
        
        CryptoString result = core.getResult();
        if(null == result)
            return -1;
        
        int i = result.utf8length();
        result.getUTF8(ans);
        result.wipe();

        return i;
    }

    // Random number seeding - consumed only by native code
    private static StrongRandomSeedCollector random = null;
    private static byte[] rnd = null;
    private static int leadin = 128;
    private static int base = 0;

    static ByteArrayWrapper getRawRandom(int length)
    {
        ByteArrayWrapper result = new ByteArrayWrapper(length);

        if(Root.instance().isManualRandom())
        {
            if(null == random) ensure(length);
            random.getRaw(result.data, length);
        }
        else
        {
            ensure(length);
            System.arraycopy(rnd, base, result.data, 0, length);
            base += length;
        }
        return result;
    }

    static boolean ensure(int bytes)
    {
        if(Root.instance().isManualRandom())
        {
            if(null == random)
            {
                random = new StrongRandomSeedCollector(Application.instance().getFrame());
            }
            random.ensure(bytes*8);
        }
        else if(null == rnd || ((rnd.length - base) < bytes))
        {
          rnd = new byte[bytes];
          try {
            String dr = "/dev/random";
            File f = new File(dr);
            if(dr.equals(f.getCanonicalPath()) && f.exists())
            {
               FileInputStream fi = new FileInputStream(f);
               for(int i=0; i<bytes; ++i)
               {
                 rnd[i] =  (byte)fi.read();
               }
               return true;
            }
          }
          catch(IOException ignored) {}

          // This is good random, at least on NT4.0, after 1k bits discard
          // to remove transients - 0.9 bits/byte or more
          byte[] lrnd = java.security.SecureRandom.getSeed(leadin+bytes);
          System.arraycopy(lrnd, leadin, rnd, 0, bytes);
          base = 0;
        }
        return true;
    }

    // TO FIX - interrupt polling for key generation
    private static boolean pulsing = false;
    private static long pulsecount = 0;
    private static String handle;
    static void startPulse()
    {
        pulsing = true;
        pulsecount = 0;
        handle = "";//theStatusBar.getText();
    }
    static void stopPulse()
    {
        pulsing = false;
    }
    static void pulse()
    {
        if(!pulsing) return;
        ++pulsecount;
    }


}// end of class

class ByteArrayWrapper {
    // called by
    // - cjcb_act.cpp cb_convKey    
    byte [] data = null;
    public ByteArrayWrapper()
    {
    }
    public ByteArrayWrapper(int n)
    {
        data = new byte[n];
    }
    public void wipe()
    {
        java.util.Arrays.fill(data, (byte)0);
    }
}

