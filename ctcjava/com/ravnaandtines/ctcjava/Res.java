package com.ravnaandtines.ctcjava;

import java.util.*;

public class Res extends java.util.ListResourceBundle
{
    static final Object[][] contents = {
        { "OK", "OK"},
        { "Algorithms", "Algorithms"},
        { "Modes", "Modes"},
        { "Save_text", "Save text..."},
        { "Save_file", "Save file..."},
        { "Save_changes", "Save changes?"},
        { "Print", "Print..."},
        { "Edit", "Edit"},
        { "Crypto", "Crypto"},
        { "Decrypt", "Decrypt/Validate"},
        { "Extract_session_key", "Extract session key"},
        { "Encrypt", "Encrypt/Sign"},
        { "Clearsign", "Clearsign"},
        { "Detached_signature", "Detached signature"},
        { "Sign_only", "Sign only"},
        { "Cut", "Cut"},
        { "Copy", "Copy"},
        { "Paste", "Paste"},
        { "File", "File"},
        { "Save", "Save"},
        { "Save_as", "Save as..."},
        { "Close_Window", "Close window"},
        { "ROT13", "ROT 13"},
        { "Quote_text", "Quote text"},
        { "WrapQuote", "Wrap quoted text"},
        { "Print_options", "Print options"},
        { "Print_Error", "CTC Print error"},
        { "Binary_file", "Binary file"},
        { "Text_file", "Text file"},
        { "File_from", "File from:"},
        { "with_good_signature", "with good signature made at time"},
        { "with_bad_signature", "with bad signature made at time"},
        { "This_file_has_been", "This file has been modified.  Save changes?"},
        { "ATines_pack_(after_V", "A Tines pack (by A. Taylor, after V. Vinge)"},
        { "CTC2_0_for_Java_Free", "{0}for Java - {1}"},
        { "Copyright_�_1998", "Free World Freeware - Copyright {0}"},
        { "Mr_Tines", "Mr. Tines <tines@windsong.demon.co.uk>"},
        { "&Ian_Miller", "& Ian Miller <ian_miller@bifroest.demon.co.uk>"},
        { "Uses_CTClib_2_x", "Uses CTClib crypto library version {0}"},
        { "Copyleft_actually_see", "Copyleft, actually - see Help>About"},
        { "IDEA_128bit", "IDEA 128bit (2.6)"},
        { "CAST_128bit", "CAST 128bit (5)"},
        { "Blowfish_128bit", "Blowfish 128bit (OpenPGP)"},
        { "Square_128bit", "Square 128bit"},
        { "SAFER_SK128", "Safer-SK128 (EBP)"},
        { "TEA_128bit", "TEA 128-bit"},
        { "TWAY_96bit", "3-Way 96-bit"},
        { "TripleDES", "TripleDES (5)"},
        { "AES128", "AES 128-bit (OpenPGP)"},
        { "AES256", "AES 256-bit (OpenPGP)"},
        { "FRF_Blowfish_40bit", "FRF Blowfish-40bit"},
        { "Triple_Blowfish_40bit", "Triple Blowfish-40bit"},
        { "CFB", "Cypher FeedBack (2.6)"},
        { "CBC", "Cypher Block Chaining"},
        { "No_Compression", "None (2.6)"},
        { "Deflate", "Deflate (2.6)"},
        { "Splay_tree", "Splay tree"},
        { "MD5_128bit", "MD5 128-bit (2.6)"},
        { "SHA1_160bit", "SHA-1 160-bit (5)"},
        { "RIPEM_160bit", "RIPEM 160-bit"},
        { "HAVEL_256bit", "Haval 256-bit (EBP)"},
        { "Unarmoured", "None (2.6)"},
        { "PGP_armour", "PGP armour (2.6)"},
        { "UUencode", "UUencode"},
        { "Conventional_Encryption", "Conventional Encryption"},
        { "Encryption_Mode", "Encryption Mode"},
        { "Message_Digest", "Message Digest"},
        { "Compression_Scheme", "Compression Scheme"},
        { "Armour_Style", "Armour Style"},
        { "Load_config_file", "Load config file"},
        { "Browse_", "Browse..."},
        { "Please_select_your", "Please select your configuration file."},
        { "If_the_file_does_not", "If the file does not exist, an empty one will be created."},
        { "This_program_expects", "This program expects a configuration file on the command line."},
        { "Syntax:_java_ctcjava", "Syntax: jre com.ravnaandtines.ctcjava.CTC <config file>"},
        { "Read_write_access", "Read/write access could not be gained to the supplied file:"},
        { "Filename:", "Filename: "},
        { "Select_recipients", "Select recipients"},
        { "Select_signing_key", "Select signing key"},
        { "If_none_selected", "If none selected, message will be conventially encrypted"},
        { "Public_Keyring_File", "Public Keyring File"},
        { "Secret_Keyring_File", "Secret Keyring File"},
        { "Loaded", "Loaded"},
        { "Open_keyring_", "Open keyring..."},
        { "Changes_to_keyrings", "Changes to keyrings were not saved.  Aborting"},
        { "CTC_Keyring_shutdown", "CTC Keyring shutdown warning"},
        { "Enter_passphrase", "Enter passphrase..."},
        { "CAPS", "CAPS"},
        { "Key:", "Key: "},
        { "Checksum:", "Checksum: "},
        { "<concealed>", "<concealed>"},
        { "No_conversion_to", "No conversion to String"},
        { "CTC_Java", "CTC Java configuration file"},
        { "CTC_bug_check", "CTC bug check"},
        { "No_session_data", "No session data available"},
        { "Incorrect_passphrase", "Incorrect passphrase"},
        { "Too_many_tries", "Too many tries"},
        { "This_message_was", "This message was marked \"Eyes Only\" when encrypted"},
        { "Could_not_backup_and", "Could not backup and update keyring"},
        { "What_s_up_Doc?", "What\'s up, Doc?"},
        { "CTC_information", "CTC information"},
        { "Continue?", "Continue?"},
        { "Line_limit_exhausted", "Line limit exhausted"},
        { "User_interruption", "User interruption taken"},
        { "File_I_O_error", "File I/O error"},
        { "CRC_check_in_armour", "CRC check in armour failed"},
        { "Format_error_in", "Format error in armoured block of type "},
        { "Unknown_format_type", "Unknown format type"},
        { "New_public_key_found", "New public key found "},
        { "Add_to_key_ring?", "Add to key ring?"},
        { "New_secret_key_found", "New secret key found "},
        { "Add_to_key_rings?", "Add to key rings?"},
        { "Searching_for", "Searching for armoured block to process"},
        { "Armouring_output", "Armouring output"},
        { "File_error_while", "File error while writing signature"},
        { "Decompressing", "Decompressing"},
        { "While_decompressing", "While decompressing"},
        { "Computing_message", "Computing message digest"},
        { "Encrypting_message", "Encrypting message body"},
        { "Decrypting_message", "Decrypting message body"},
        { "Encrypting_message_to", "Encrypting message to key "},
        { "Signing_from_key", "Signing from key "},
        { "No_armoured_blocks", "No armoured blocks found in text"},
        { "Unimplemented_message", "Unimplemented message digest algorithm expected"}, 
        { "Unimplemented", "Unimplemented compression algorithm expected"},
        { "Could_not_open", "Could not open temprary file for decompression"},
        { "Bad_session_key", "Bad session key"},
        { "File_input_error", "File input error"},
        { "File_output_error", "File output error"},
        { "Only_public_part_of", "Only public part of decryption key available"},
        { "Public_key", "Public key unavailable for verification"},
        { "Unknown_cypher_type", "Unknown cypher-type byte encountered"},
        { "No_memory_for_digest", "No memory for digest calculation"},
        { "PGP2_6_style_comment", "PGP2.6-style comment found : "},
        { "No_secret_key", "No secret key available for decryption"},
        { "Non_signature_data_in", "Non-signature data in clearsigned message signature"},
        { "Out_of_memory_while", "Out of memory while public key decrypting"},
        { "Out_of_memory_encrypt", "Out of memory while public key encrypting"},
        { "Out_of_memory_sign", "Out of memory while signing"},
        { "Out_of_memory_verify", "Out of memory while verifying signature"},
        { "Out_of_memory_keygen", "Out of memory while generating keypair"},
        { "User_interrupt_taken", "User interrupt taken while public key decrypting"},
        { "User_interrupt_pke", "User interrupt taken while public key encrypting"},
        { "User_interrupt_sign", "User interrupt taken while signing"},
        { "User_interrupt_verify", "User interrupt taken while verifying signature"},
        { "User_interrupt_keygen", "User interrupt taken while generating keypair"},
        { "File_I_O_error_while", "File I/O error while public key encrypting"},
        { "File_I_O_error_keygen", "File I/O error while generating keypair"},
        { "Check_data_not_found", "Check data not found in conventional key packet"},
        { "unrecognisedCKey", "unrecognised conventional key packet format"},
        { "unrecognised_message", "unrecognised message digest packet format"},
        { "message_digest_does", "message digest does not match check bytes"},
        { "Required_conventional", "Required conventional algorithm unavailable for decryption"},
        { "Required_mode_unavail", "Required conventional cypher mode unavailable for decryption"},
        { "Public_key_too_short", "Public key too short for required conventional key packet"},
        { "Fatal_error_in_public", "Fatal error in public key decryption"},
        { "Fatal_error_in_PKE", "Fatal error in public key encryption"},
        { "Fatal_error_in_sig", "Fatal error in signature"},
        { "Fatal_error_in_ver", "Fatal error in verification"},
        { "Fatal_error_in_keygen", "Fatal error in keypair generation"},
        { "Key_generation_begun", "Key generation begun"},
        { "Generating_first", "Generating first prime number"},
        { "Generating_second", "Generating second prime number"},
        { "Concluding_key", "Concluding key generation"},
        { "Required_public_key", "Required public key algorithm unavailable for decryption"},
        { "Required_pkalg_unavail", "Required public key algorithm unavailable for encryption"},
        { "Required_pksig_unavail", "Required public key algorithm unavailable for signature"},
        { "Required_pkver_unavail", "Required public key algorithm unavailable for verification"},
        { "Unexpected_duplicate", "Unexpected duplicate key IDs (DEADBEEF attack)"},
        { "Key_in_inconsistent", "Key in inconsistent state"},
        { "Could_not_read", "Could not read complete key from ring"},
        { "Public_key_found", "Public key found"},
        { "Secret_key_for", "Secret key for decryption found"},
        { "User_ID_for", "User ID for decryption found"},
        { "Key_signature_found", "Key signature found"},
        { "Key_without_userID", "Key without userID found"},
        { "Could_not_validate", "Could not validate key revocation (key dubious)"},
        { "Bad_key_revocation", "Bad key revocation (key dubious)"},
        { "Improperly_revoked", "Improperly revoked key found (wronge key used to revoke)"},
        { "Bad_key_signature", "Bad key signature found"},
        { "Key_signature_unknown", "Key signature found by unknown key"},
        { "Unsupported_version", "Unsupported version byte detected"},
        { "Unsupported_algorithm", "Unsupported algorithm byte detected"},
        { "Bad_length_value_in", "Bad length value in packet"},
        { "Unexpected_record", "Unexpected record type encountered"},
        { "File_does_not_appear", "File does not appear to be cyphertext"},
        { "Secret_key_not", "Secret key not allocated"},
        { "No_memory_for_I_O", "No memory for I/O"},
        { "STATUS:", "STATUS:"},
        { "INFO:", "INFO:"},
        { "CTC_CRASH", "CTC CRASH"},
        { "CTC_FATAL_ERROR", "CTC FATAL ERROR"},
        { "CTC_STATUS", "CTC STATUS"},
        { "Select_secret_key_for", "Select secret key for decryption..."},
        { "Select_algorithm", "Select algorithm"},
        { "Collecting_random", "Collecting random data"},
        { "User_ID", "User ID"},
        { "Key_algorithm_and", "Key algorithm and size"},
        { "Cancel", "Cancel"},
        { "DISABLED_KEY_", "DISABLED KEY "},
        { "RSA", "RSA "},
        { "Rabin", "Rabin "},
        { "Elliptic_curve", "Elliptic curve on GF(2^255)"},
        { "DSA", "DSA "},
        { "keyprint", " keyprint "},
        { "DH", "ElGamal/Diffie-Hellman "},
        { "Unknown_pkalg", "Unknown public key algorithm"},
        { "RSAencrypt", "RSA Encrypt Only"},
        { "RSAsign", "RSA Sign Only"},
        { "RSA_1024_bits_(2_6)", "RSA 1024 bits (2.6)"},
        { "RSA_2000_bits_(some_2", "RSA 2000 bits (some 2.6)"},
        { "RSA_2048_bits_(some_2", "RSA 2048 bits (some 2.6)"},
        { "RSA_4096_bits", "RSA 4096 bits"},
        { "Eliiptic_curve", "Eliiptic curve GF(2^255) "},
        { "Passphrase_and", "Passphrase and confirmation"},
        { "Protecting_encryption", "Protecting encryption"},
        { "Self_signature_hash", "Self-signature hash"},
        { "Show_passphrase", "Show passphrase checksums"},
        { "Quote_string", "Quote string"},
        { "Default_ID", "Default ID"},
        { "ShowChecksums", "ShowChecksums"},
        { "yes", "yes"},
        { "Public_keys", "Public keys"},
        { "Extract", "Extract"},
        { "Sign", "Sign"},
        { "En_Disable", "En/Disable"},
        { "Delete", "Delete"},
        { "Manage", "Manage"},
        { "Reading_public_keys", "Reading public keys"},
        { "Sorting_public_keys", "Sorting public keys"},
        { "Loading_public_keys", "Loading public keys"},
        { "Public_keys_loaded", "Public keys loaded"},
        { "Key_signature", "Key signature"},
        { "READ_CAREFULLY:_Based", "READ CAREFULLY:  Based on your own direct first-hand knowledge, are\n" +
          "you absolutely certain that you are prepared to solemnly certify " +
          "that\nhe above public key actually belongs to the user specified " +
          "by the\nabove user ID?"},
        { "Please_move_mouse", "Please move mouse.  Random bits required = {0}"},
        { "Enough_random_bits", "Enough random bits collected.  Thank you."},
        { "Collecting_mouse", "Collecting mouse events"},
        { "Key_type", "Key type"},
        { "Prime_generation", "Prime generation"},
        { "Encrypt_Sign_(2_6)", "Encrypt + Sign (2.6)"},
        { "Encrypt_Only_(4_0)", "Encrypt Only (4.0)"},
        { "Sign_Only_(4_0)", "Sign Only (4.0)"},
        { "Simple_scan_(faster)", "Simple scan (faster)"},
        { "Jump_scan", "Jump scan"},
        { "Sophie_Germain", "Sophie Germain (stronger)"},
        { "Revoke_", "Revoke..."},
        { "Lock", "Lock"},
        { "Secret_keys", "Secret keys"},
        { "Revocation", "Revocation certificate type"},
        { "RevokeKey", "Revoke {0} permanently?"},
        { "Open_", "Open..."},
        { "Configuration", "Configuration"},
        { "About", "About"},
        { "Settings", "Settings"},
        { "Keyrings", "Keyrings"},
        { "CTC_(IDEA_enabled)", "CTC (IDEA enabled)"},
        { "CTC_(IDEA_free)", "CTC (IDEA free)"},
        { "Exit", "Exit"},
        { "New", "New"},
        { "Save_Preferences", "Save Preferences"},
        { "Help", "Help"},
        { "About_", "About..."},
        { "Keys", "Keys"},
        { "Generate_", "Generate..."},
        { "Lock_All", "Lock All"},
        { "Create_new_file_", "Create new file..."},
        { "Open_file_", "Open file..."},
        { "Save_Configuration", "Save Configuration Preferences"},
        { "Help_About_", "Help About..."},
        { "IDEA_licence", "IDEA licence"},
        { "Break", "Break"},
        { "About_IDEA_", "About IDEA..."},
        { "About_CTC_(Freeware", "About CTC (Freeware Licence)"},
        { "IDEA_Licence", "IDEA Licence"},
        { "None", "None"},
        { "Select_key_type_", "Select key type..."},
        { "RSA_key_generation", "RSA key generation details"},
        { "Key_protection", "Key protection"},
        { "NOTE:_This_key_has", "NOTE: This key has been revoked"},
        { "Signature_by_unknown", "Signature by unknown key made at "},
        { "Good_signature_made", "Good signature made at "},
        { "Bad_signature_made_at", "Bad signature made at "},
        { "UserID:", "UserID: "},
        { "Read_POP3_mail_", "Read POP3 mail..."},
        { "Available_messages", "Available messages"},
        { "Connect_to_Server", "Connect to Server"},
        { "Retrieve_selection", "Retrieve selection"},
        { "POP3_retrieval", "POP3 retrieval"},
        { "POP3_user_name", "POP3 user name"},
        { "POP3_server_name", "POP3 server name"},
        { "POP3_password", "POP3 password"},
        { "Connection_failure:", "Connection failure: "},
        { "Logon_failure:", "Logon failure: "},
        { "Count_failure:", "Count failure: "},
        { "There_are_no_messages", "There are no messages waiting"},
        { "There_is_one_message", "There is one message waiting"},
        { "There_are_0_number", "There are {0,number,integer} messages waiting"},
        { "Retrieving_message_0", "Retrieving message {0} of {1}"},
        { "Problem_retrieving", "Problem retrieving message {0} of {1}"},
        { "Problem_deleting", "Problem deleting message {0} of {1}"},
        { "SMTP_", "Send via SMTP..."},
        { "Collect_message_0", "Collect message {0,number,integer}"},
        { "Delete_message_0", "Delete message {0,number,integer} after collecting"},
        { "Status", "Status"},
        { "Send", "Send"},
        { "Quit", "Quit"},
        { "SMTP_transmission", "SMTP transmission"},
        { "From:", "From:"},
        { "To:", "To:"},
        { "Subject:", "Subject:"},
        { "SMTP_server:", "SMTP server:"},
        { "Bad_recipient_email", "Bad recipient email address"},
        { "Bad_sender_email", "Bad sender email address"},
        { "Login_failure:", "Login failure: "},
        { "Bad_sender:", "Bad sender: "},
        { "Bad_recipient:", "Bad recipient: "},
        { "Transmission_error:", "Transmission error: "},
        { "No_response_code:", "No response code: "},
        { "Button_top_icons", "Non-Tines icons Copyright � 1998"},
        { "Dean_S_Jones", "Dean S. Jones <dean@gallant.com>"},
        { "www_gallant_com_icons", "www.javalobby.org/jfa/projects/icons.htm"},
        { "Other_Mail_Headers:", "Other Mail Headers:"},
        { "GWT", "GUI uses GWT Java Class Library"},
        { "GWT_CPR", "Copyright � 1997  DTAI, Inc."},
        { "GWT_URL", "http://www.dtai.com/solutions/gwt"},
        { "Use_secure_login", "Use secure login"},
        { "newUID", "Add userID"},
        { "newPphrase", "Passphrase" },
        { "AddNewUser", "New User ID"},
        { "Manual_random", "Manual random input"},
        { "MouseRandom", "MouseRandom"},
        { "key_revocation_found", "Key revocation certificate found"},
        { "noPubring", "Saved public key ring file not found."},
        { "noSecring", "Saved secret key ring file not found."},
        { "noExtract", "Public key could not be extracted."},
        { "noRevoke", "Key revocation could not be performed."},
        { "Armour_failure", "Armouring of file failed."},
        { "Decompressing_message", "Failure while decompressing message."} 
    };

    public Object[][] getContents()
    {
        return contents;
    }
}
