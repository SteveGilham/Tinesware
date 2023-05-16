/*
 * KeyGenerationWizard.java
 *
 * Created on 26 December 2005, 17:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;
import com.ravnaandtines.ctcjava.KeyConstants.PKA;
import com.ravnaandtines.ctcjava.KeyConstants.PRIME;
import javax.swing.*;
import com.nexes.wizard.*;
import java.awt.BorderLayout;

/**
 *
 * @author Steve
 */
public class KeyGenerationWizard {
    Wizard w = null;
    KeyIDStep k = null;
    RSAGenerationStep r = null;
    KeyProtectionStep p = null;
    
    /** Creates a new instance of KeyGenerationWizard */
    public KeyGenerationWizard(java.awt.Frame owner) {
        w = new Wizard(owner);
        w.setTitle(GlobalData.getResourceString("Key Generation Wizard"));
        
        k = new KeyIDStep();
        w.registerWizardPanel(k.getPanelDescriptorIdentifier(),k);
        w.setCurrentPanel(k.getPanelDescriptorIdentifier());        
        
        r = new RSAGenerationStep();
        w.registerWizardPanel(r.getPanelDescriptorIdentifier(),r);

        p = new KeyProtectionStep();
        w.registerWizardPanel(p.getPanelDescriptorIdentifier(),p);
    }
    
    int showKeyProtection()
    {
        w.setTitle(GlobalData.getResourceString("Key_protection"));
        w.setCurrentPanel(p.getPanelDescriptorIdentifier());
        p.preventBackup();
        return show();
    }
    
    
    public int show()
    {
        return w.showModalDialog();
    }
    
    public int getKeyLength()
    {
        return r.getKeyLength();
    }
    public KeyConstants.PKA getKeyType()
    {
        return r.getKeyType();
    }
    public KeyConstants.PRIME getPrimeGeneratorType()
    {
        return r.getPrimeGeneratorType();
    }
    public KeyConstants.MDA getHash()
    {
        return p.getHash();
    }
    public KeyConstants.CEA getKeyProtectionAlgorithm()
    {
        return p.getKeyProtectionAlgorithm();
    }
    public byte[] getUTF8Passphrase()
    {
        CryptoString p1 = p.getPassphrase();
        byte[] phrase = null;
        try {
            phrase = new byte[p1.utf8length()];
            p1.getUTF8(phrase);
        } finally {
            if(p1 != null)
                p1.wipe();
        }
        return phrase;
    }
    public String getUserID()
    {
        return k.getText();
    }    
    
    private class KeyIDStep extends CommonDescriptor 
    {
        private JTextField textField;
                
        KeyIDStep()
        {
            super();
            setPanelDescriptorIdentifier(KeyIDStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("User for key"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("Enter name"));
            top.add(title);
            top.add(rubric);

            JLabel key = new JLabel(GlobalData.getResourceString("User_ID"));
            textField = new JTextField(64);
            key.setLabelFor(textField);
            key.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_U);

            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            body.add(key, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            body.add(textField, gridBagConstraints);
        }
        
        String getText()
        {
            return textField.getText();
        }
        
        public String getNextPanelDescriptor() {
            return RSAGenerationStep.class.getName();
        }
        
    }

    private class RSAGenerationStep extends CommonDescriptor
    {
        JSlider keyBits;
        
        JRadioButton dualuse;
        JRadioButton encrypt;
        JRadioButton sign;
           
        JRadioButton simple;
        JRadioButton jump;
        JRadioButton sophie;        
        
        RSAGenerationStep()
        {
            super();
            setPanelDescriptorIdentifier(RSAGenerationStep.class.getName());            
            JLabel title = new JLabel(GlobalData.getResourceString("RSAGen"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("RSAGenStuff"));
            top.add(title);
            top.add(rubric);
            
            JLabel key = new JLabel(GlobalData.getResourceString("Key_algorithm_and"));
            keyBits = new JSlider(1024, 4096, 2048);
            keyBits.createStandardLabels(1024, 1024);
            keyBits.setMajorTickSpacing(1024);
            keyBits.setMinorTickSpacing(256);
            keyBits.setPaintLabels(true);
            keyBits.setPaintTicks(true);
            
            key.setLabelFor(keyBits);
            key.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_K);

            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            body.add(key, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            body.add(keyBits, gridBagConstraints);
            
            JPanel type = new JPanel();
            type.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Key_type")));
            type.setLayout(new java.awt.GridLayout(3,1));
            ButtonGroup typeGroup = new ButtonGroup();
            
            JPanel generationAlgorithm = new JPanel();
            generationAlgorithm.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Prime_generation")));
            generationAlgorithm.setLayout(new java.awt.GridLayout(3,1));
            ButtonGroup genGroup = new ButtonGroup();
            
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
            body.add(type, gridBagConstraints);
            
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
            body.add(generationAlgorithm, gridBagConstraints);
            
            dualuse = new JRadioButton();
            typeGroup.add(dualuse); type.add(dualuse);
            encrypt = new JRadioButton();
            typeGroup.add(encrypt); type.add(encrypt);
            sign = new JRadioButton();
            typeGroup.add(sign); type.add(sign);
            
            simple = new JRadioButton();
            genGroup.add(simple); generationAlgorithm.add(simple);
            jump = new JRadioButton();
            genGroup.add(jump); generationAlgorithm.add(jump);
            sophie = new JRadioButton();
            genGroup.add(sophie); generationAlgorithm.add(sophie);
            
            dualuse.setSelected(true);
            dualuse.setText(GlobalData.getResourceString("Encrypt_Sign_(2_6)"));
            encrypt.setText(GlobalData.getResourceString("Encrypt_Only_(4_0)"));
            sign.setText(GlobalData.getResourceString("Sign_Only_(4_0)"));

            simple.setText(GlobalData.getResourceString("Simple_scan_(faster)"));
            jump.setText(GlobalData.getResourceString("Jump_scan"));
            sophie.setText(GlobalData.getResourceString("Sophie_Germain"));
            jump.setSelected(true);
            
        }
        public String getNextPanelDescriptor() {
            return KeyProtectionStep.class.getName();
        }
        public String getBackPanelDescriptor() {
            return KeyIDStep.class.getName();
        }
        public int getKeyLength() {
            return keyBits.getValue();
        }
        public KeyConstants.PKA getKeyType()
        {
            if(dualuse.isSelected()) return KeyConstants.PKA.RSA;
            else if(encrypt.isSelected()) return KeyConstants.PKA.RSA_ENCRYPT_ONLY;
            else return KeyConstants.PKA.RSA_SIGN_ONLY;            
        }
        public KeyConstants.PRIME getPrimeGeneratorType()
        {
            if(simple.isSelected()) return KeyConstants.PRIME.SIMPLE;
            else if (jump.isSelected()) return KeyConstants.PRIME.JUMP;
            else return KeyConstants.PRIME.SOPHIE_GERMAIN;            
        }
    }
    
    private class KeyProtectionStep extends CommonDescriptor
    {
        GetPassphraseUI passphrase;
        GetPassphraseUI confirm;
        
        JRadioButton idea;
        JRadioButton blow16;
        JRadioButton rijndael;
        JRadioButton cast5;
        
        JRadioButton md5;
        JRadioButton sha;
        boolean backup = true;
        
        KeyProtectionStep()
        {
            super();
            setPanelDescriptorIdentifier(KeyProtectionStep.class.getName());            
            JLabel title = new JLabel(GlobalData.getResourceString("KeyProt"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("KeyProtStuff"));
            top.add(title);
            top.add(rubric);
            
            JLabel key = new JLabel(GlobalData.getResourceString("Passphrase"));
            passphrase = new GetPassphraseUI();
            
            passphrase.applyLabel(key);
            key.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_P);
            passphrase.addDocumentListener(new javax.swing.event.DocumentListener ()
            {
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
            });

            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
            body.add(key, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = 3;
            body.add(passphrase.getShell(), gridBagConstraints);
            
            key = new JLabel(GlobalData.getResourceString("Confirmation"));
            confirm = new GetPassphraseUI();
            
            confirm.applyLabel(key);
            key.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_C);
            confirm.addDocumentListener(new javax.swing.event.DocumentListener ()
            {
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    KeyProtectionStep.this.phrase_textValueChanged();
                }
            });

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
            body.add(key, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridwidth = 3;
            body.add(confirm.getShell(), gridBagConstraints);
            
            JPanel cea = new JPanel();
            cea.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Protecting_encryption")));
            cea.setLayout(new java.awt.GridLayout(4,1));
            ButtonGroup encryptGroup = new ButtonGroup();
            
            JPanel mda = new JPanel();
            mda.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Self_signature_hash")));
            mda.setLayout(new java.awt.GridLayout(4,1));
            ButtonGroup hashGroup = new ButtonGroup();
            
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
            gridBagConstraints.gridwidth = 2;
            body.add(cea, gridBagConstraints);
            
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
            gridBagConstraints.gridwidth = 2;
            body.add(mda, gridBagConstraints);
            
            idea     = new JRadioButton();
            encryptGroup.add(idea); cea.add(idea);
            blow16   = new JRadioButton();
            encryptGroup.add(blow16); cea.add(blow16);
            rijndael = new JRadioButton();
            encryptGroup.add(rijndael); cea.add(rijndael);
            cast5    = new JRadioButton();
            encryptGroup.add(cast5); cea.add(cast5);
                        
            md5     = new JRadioButton();
            hashGroup.add(md5); mda.add(md5);
            sha     = new JRadioButton();
            hashGroup.add(sha); mda.add(sha);
            
            idea.setText(GlobalData.getResourceString("IDEA_128bit"));
            blow16.setText(GlobalData.getResourceString("Blowfish_128bit"));

            rijndael.setText(GlobalData.getResourceString("AES128"));
            cast5.setText(GlobalData.getResourceString("CAST_128bit"));

            md5.setText(GlobalData.getResourceString("MD5_128bit"));
            sha.setText(GlobalData.getResourceString("SHA1_160bit"));
            
            md5.setSelected(true);
            if(!KeyConstants.isIDEAenabled()) {
                idea.setEnabled(false);
                cast5.setSelected(true);
            } else
                idea.setSelected(true);
        }
        public void displayingPanel() {
            passphrase.phrase.setRequestFocusEnabled(true);
            passphrase.phrase.requestFocusInWindow();
        }        
        public void aboutToDisplayPanel() {
            getWizardModel().setNextFinishButtonEnabled(Boolean.FALSE);
            if(!backup)
                getWizardModel().setBackButtonEnabled(Boolean.FALSE);                
        }     
        void preventBackup()
        {
            backup = false;
        }
        public String getBackPanelDescriptor() {
            return RSAGenerationStep.class.getName();
        }
        public String getNextPanelDescriptor() {
            return WizardPanelDescriptor.FinishIdentifier;
        } 
        public KeyConstants.MDA getHash()
        {
            if(sha.isSelected())
                return KeyConstants.MDA.PGP5_SHA1;
            else return KeyConstants.MDA.MD5;
        }
        public KeyConstants.CEA getKeyProtectionAlgorithm()
        {
            if(idea.isSelected()) return KeyConstants.CEA.IDEA;
            else if(blow16.isSelected()) return KeyConstants.CEA.GPG_BLOW16;
            else if(rijndael.isSelected()) return KeyConstants.CEA.OPGP_AES_128;
            else return KeyConstants.CEA.CAST5;
        }
        public CryptoString getPassphrase()
        {
            return passphrase.getResult();
        }
        
        public void phrase_textValueChanged() {
            if(null == getWizardModel())
                return;
            CryptoString t1 = null;
            CryptoString t2 = null;
            try {
                t1 = passphrase.getResult();
                t2 = confirm.getResult();
                if(t1.length() < 10 || t2.length() < 10
                        || t1.length() != t2.length()) {
                    getWizardModel().setNextFinishButtonEnabled(Boolean.FALSE);
                    return;
                }
                int n = t1.length();
                for(int i=0; i<n; ++i) {
                    if(t1.charAt(i) != t2.charAt(i)) {
                        getWizardModel().setNextFinishButtonEnabled(Boolean.FALSE);
                        return;
                    }
                    getWizardModel().setNextFinishButtonEnabled(Boolean.TRUE);
                }
            } finally {
                if(t1 != null) t1.wipe();
                if(t2 != null) t2.wipe();
            }
        }
    }
    
}

    class CommonDescriptor extends WizardPanelDescriptor 
    {
        protected JPanel top;
        protected JPanel body;
        
        CommonDescriptor()
        {
            super();
            JPanel panel = (JPanel)getPanelComponent();
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(new java.awt.Dimension(480, 300));
            
            top = new JPanel();
            top.setBackground(java.awt.Color.WHITE);
            panel.add(top, BorderLayout.NORTH);
            top.setPreferredSize(new java.awt.Dimension(480, 72));
            top.setLayout(new java.awt.GridLayout(2,1));
            top.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 10, 5, 10)));  
            
            body = new JPanel();
            body.setLayout(new java.awt.GridBagLayout());
            body.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 10, 5, 10)));  

            panel.add(body, BorderLayout.CENTER);
        }
    }

