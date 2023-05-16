//Title:        CTC2.0 for Java//Version:
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
import java.awt.datatransfer.*;
import com.ravnaandtines.util.MessageBox;

public class CJGlobals {

    private CJGlobals() {
    }

    static StatusBar theStatusBar = null;
    static ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    static String config = "";
    static CTCJMainFrame mainFrame = null;
    static boolean userbreak = false;
    static boolean manual = false;

    static Properties settings = null;
    static Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    static CJdecodeContext decodeContext = null;
    static boolean showChecksums = false;

    static String encoding = null;

    public static native String libVersion();

    public static boolean stubFile(String name)
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
        if(null != CJGlobals.encoding)
        {
            try {
                sbuf = new String(buf, 0, n, CJGlobals.encoding);
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

    public static void loadConfig()
    {
        try{
            FileInputStream f = new FileInputStream(config);
            Properties p = new Properties();
            p.load(f);
            settings = p;
        }
        catch(Exception e)
        {
        }
    }

    public static void saveConfig()
    {
        try{
            FileOutputStream f = new FileOutputStream(config);
            settings.save(f, res.getString("CTC_Java"));
        }
        catch(Exception e)
        {
        }
    }

    public static void stickUpandDie(String text)
    {
        MessageBox messageBox = new MessageBox();
        messageBox.setFrame(mainFrame);
        messageBox.setType(MessageBox.INFO);
        messageBox.setMessage(text);
        messageBox.setTitle(res.getString("CTC_bug_check"));
        messageBox.show();
        ((CTCJMainFrame)mainFrame).menuFileExit_actionPerformed(
        /*new java.awt.event.ActionEvent(text,0,text)*/);
    }
    public static final int BADPHRASE = 0;
    public static final int OUTOFTIME = 1;
    public static final int EYESONLY = 2;
    public static final int NOSESDATA = -3;
    public static final int KEYWRITEFAILED = 3;

    public static void stickUp(int key)
    {
        if(NOSESDATA == key) return;

        MessageBox messageBox = new MessageBox();
        messageBox.setFrame(mainFrame);
        messageBox.setType(MessageBox.INFO);
        switch(key)
        {
        case NOSESDATA:
            messageBox.setMessage(res.getString("No_session_data"));
            break;
        case BADPHRASE:
            messageBox.setMessage(res.getString("Incorrect_passphrase"));
            break;
        case OUTOFTIME:
            messageBox.setMessage(res.getString("Too_many_tries"));
            break;
        case EYESONLY:
            messageBox.setMessage(res.getString("This_message_was"));
            break;
        case KEYWRITEFAILED:
            messageBox.setMessage(res.getString("Could_not_backup_and"));
            break;
        default:
            messageBox.setMessage(res.getString("What_s_up_Doc?"));
            break;
        }
        messageBox.setTitle(res.getString("CTC_information"));
        messageBox.show();
    }

    public static boolean cbException(int code, String text, CJPubkey key)
    {
        // This routine is called only from ctc.c and is believed complete
        String kode = Integer.toHexString(code);
        String question = res.getString("Continue?");
        MessageBox messageBox = new MessageBox();
        messageBox.setFrame(mainFrame);
        messageBox.setType(MessageBox.YES_NO);

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

        messageBox.setMessage(""+kode+" "+text+" "+key);
        if(question != null)
        {
            dtai.gwt.LabelGadget q = new dtai.gwt.LabelGadget(question);
            messageBox.addInnerComponent(q);
        }
        messageBox.show();
        return messageBox.getResult()==0;
    }

    public static void cbInformation(int code, String text, CJPubkey key)
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
            theStatusBar.setText(res.getString("STATUS:")+kode+":"+text+":"+key);
            mainFrame.echo(res.getString("STATUS:")+kode+":"+text+":"+key);
            return;
        }
        else if(5 == severity && !stickup)
        {
            theStatusBar.setText(res.getString("INFO:")+kode+":"+text+":"+key);
            mainFrame.echo(res.getString("INFO:")+kode+":"+text+":"+key);
            return;
        }

        MessageBox messageBox = new MessageBox();
        messageBox.setFrame(mainFrame);
        kode = kode+":"+text+":"+key;
        switch(severity)
        {
        case 1:
            messageBox.setType(MessageBox.ERROR);
            messageBox.setTitle(res.getString("CTC_CRASH"));
            break;
        case 2:
            messageBox.setType(MessageBox.ERROR);
            messageBox.setTitle(res.getString("CTC_FATAL_ERROR"));
            break;
        case 3:
            messageBox.setType(MessageBox.ERROR);
            break;
        case 4:
            messageBox.setType(MessageBox.WARN);
            break;
        case 5:
            messageBox.setType(MessageBox.INFO);
            break;
        case 6:
            messageBox.setType(MessageBox.INFO);
            messageBox.setTitle(res.getString("CTC_STATUS"));
            break;
        }
        messageBox.setMessage(kode);
        messageBox.show();
    }

    public static int selectID(String [] names)
    {
        CJseckeySelector dlg = new CJseckeySelector(mainFrame,
        res.getString("Select_secret_key_for"), true);
        dlg.fill(names);
        dlg.pack();
        dlg.show();
        return dlg.selection;
    }

    public static int getPassphrase(String keyID, byte [] ans)
    {
        CJGet1Passphrase dlg = new CJGet1Passphrase(mainFrame, true);
        dlg.setKeyname(keyID);
        dlg.clearText();
        dlg.pack();
        dlg.show();
        int i = -1;
        if(dlg.result != null)
        {
            i = dlg.result.utf8length();
            dlg.result.getUTF8(ans);
            dlg.result.wipe();
        }
        return i;
    }

    public static CTCJMainFrame getFrame()
    {
        return (CTCJMainFrame) mainFrame;
    }

    static void notify(CJFileFrame window)
    {
        getFrame().notify(window);
    }

    static CJBytes getConvAlg()
    {
        CJConvKeyAlgDlg dlg = new CJConvKeyAlgDlg(mainFrame,
                   res.getString("Select_algorithm"), true);
        dlg.show();
        byte [] result = dlg.result;
        CJBytes r = null;
        if(result != null)
        {
          r = new CJBytes();
          r.data = result;
        }
        return r;
    }

    private static CJRanDlg random = null;
    private static byte[] rnd = null;
    private static int leadin = 128;
    private static int base = 0;

    static CJBytes getRawRandom(int length)
    {
        CJBytes result = new CJBytes(length);

        if(manual)
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
        if(manual)
        {
            if(null == random)
            {
                random = new CJRanDlg(mainFrame, res.getString("Collecting_random"), true);
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

    private static boolean pulsing = false;
    private static long pulsecount = 0;
    private static String handle;
    static void startPulse()
    {
        pulsing = true;
        pulsecount = 0;
        handle = theStatusBar.getText();
    }
    static void stopPulse()
    {
        pulsing = false;
    }
    static void pulse()
    {
        if(!pulsing) return;
        ++pulsecount;
        //theStatusBar.setText(handle+" "+pulsecount);
        //System.out.println(handle+" "+pulsecount);
    }


}// end of class

class CJBytes {
    byte [] data = null;
    public CJBytes()
    {
    }
    public CJBytes(int n)
    {
        data = new byte[n];
    }
}

