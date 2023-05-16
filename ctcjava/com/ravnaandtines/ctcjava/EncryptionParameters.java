
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.*;
import com.nexes.wizard.*;

public class EncryptionParameters {
    static Wizard w = null;
    static SecretKeyStep sks = null;
    static PassphraseStep pps = null;
    static RecipientsStep rs = null;
    static ConventionalStep cs = null;
    static CEAPassphraseStep cpps = null;
    
    
    static SecretKey signatory = null;
    
    private static class SecretKeyStep extends CommonDescriptor 
            implements javax.swing.event.ListSelectionListener
    {
        private boolean skipToRecipient = true;
        private JList nameList = null;
        private String nextPanel = RecipientsStep.class.getName();
        private JButton clear = new JButton(GlobalData.getResourceString("Unselect_all"));
        private Task task;
        private JCheckBox eyesonly = new JCheckBox(GlobalData.getResourceString("Set_eyes_only"));
        
        SecretKeyStep(Task t, boolean text)
        {
            super();
            task = t;
            setPanelDescriptorIdentifier(SecretKeyStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("Select_signing_key"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("Select_signing_key_stuff"));
            top.add(title);
            top.add(rubric);
            
            nameList = SecretKeyRoot.instance().getSignatoryList();
            if(null == nameList)
                return;
            nameList.addListSelectionListener(this);
            
            body.setLayout(new java.awt.BorderLayout());
            
            body.add(new JScrollPane(nameList), BorderLayout.CENTER);
            JPanel tmp = new JPanel();
            clear.addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SecretKeyStep.this.nameList.clearSelection();
                }
            });
            tmp.add(clear);
            body.add(tmp, BorderLayout.SOUTH);
            
            eyesonly.setSelected(false);
            if(text)
                body.add(eyesonly, BorderLayout.NORTH);
            
            skipToRecipient = false;
            valueChanged(null);
        }
        public boolean isEyesOnly()
        {
            return eyesonly.isSelected();
        }
        public String getNextPanelDescriptor() {
            return nextPanel;
        }
        public void aboutToDisplayPanel() {
            valueChanged(null);
        }
        public void valueChanged(javax.swing.event.ListSelectionEvent e)
        {
            if(null != e && e.getValueIsAdjusting())
                return;
            if(null == nameList)
                return;
            
            EncryptionParameters.restartPassphraseCount();
            
            SecretKey key = (SecretKey) nameList.getSelectedValue();
            clear.setEnabled(key != null);

            if((null==key) || !key.isLocked())
            {
                if(Task.ENCRYPTION == task)
                    nextPanel = RecipientsStep.class.getName();
                else
                    nextPanel = WizardPanelDescriptor.FinishIdentifier;
            }
            else
                nextPanel = PassphraseStep.class.getName();  
            
            if(getWizard() != null)
                getWizard().resetButtonsToPanelRules();            
        }
        public boolean haveNoSecretKeys()
        {
            return skipToRecipient;
        }
        SecretKey getSelectedSignatory()
        {
            if(null == nameList)
                return null;
            return (SecretKey) nameList.getSelectedValue();
        }
    }
    
    private static class PassphraseStep extends CommonDescriptor 
    {
        private int tries = 0;
        private int most = 3;
        
        private JLabel title = new JLabel("");
        private JLabel rubric = new JLabel("");
        private GetPassphraseUI ui = new GetPassphraseUI();
        
        String nextPanel = PassphraseStep.class.getName();
        Task task;
        
        PassphraseStep(Task t)
        {
            super();
            task = t;
            setPanelDescriptorIdentifier(PassphraseStep.class.getName());
            top.add(title);
            top.add(rubric);
            
            
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 5;
            gridBagConstraints.ipady = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
            body.add(new JLabel(GlobalData.getResourceString("Enter_passphrase")), gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            
            body.add(ui.getShell(), gridBagConstraints);
            
         
        }

        public void restart()
        {
            tries = 0;
        }
        public void displayingPanel() {
            ui.phrase.setRequestFocusEnabled(true);
            ui.phrase.requestFocusInWindow();
        }
        public void aboutToDisplayPanel() {
            ++tries;
            SecretKey who = EncryptionParameters.getSelectedSignatory();
            if(null == who)
                EncryptionParameters.forceCancel();
            
            title.setText(GlobalData.getResourceString("Key:")+who.toString());
            Object[] args = { new Integer(tries), new Integer(most) };
            rubric.setText(java.text.MessageFormat.format(
                    GlobalData.getResourceString("This_is_attempt"), args));
        }
        public void aboutToHidePanel() {
            ui.clearText();
        }
        public void onNextButtonPressed() {
            CryptoString suggested = null;
            
            try {
                suggested = ui.getResult();
                ui.clearText();
                SecretKey who = EncryptionParameters.getSelectedSignatory();
                if(null == who)
                    EncryptionParameters.forceCancel();
                if(!who.isLocked()) {
                    if(Task.ENCRYPTION == task)
                        nextPanel = RecipientsStep.class.getName();
                    else
                        nextPanel = WizardPanelDescriptor.FinishIdentifier;
                    getWizard().resetButtonsToPanelRules();
                    return;
                }
                if(who.unlock(suggested)) {
                    if(Task.ENCRYPTION == task)
                        nextPanel = RecipientsStep.class.getName();
                    else
                        nextPanel = WizardPanelDescriptor.FinishIdentifier;
                    getWizard().resetButtonsToPanelRules();
                    return;
                }
            } finally {
                suggested.wipe();
            }
            if(tries >= most)
            {
                EncryptionParameters.forceCancel();
                GlobalData.stickUp(GlobalData.OUTOFTIME);
            }
            
            nextPanel = PassphraseStep.class.getName();
            getWizard().resetButtonsToPanelRules();
        }
        public String getNextPanelDescriptor() {
            return nextPanel;
        }
        public String getBackPanelDescriptor() {
            return SecretKeyStep.class.getName();
        }
    }
    
    private static class RecipientsStep extends CommonDescriptor 
        implements javax.swing.event.ListSelectionListener
    {
        JList nameList = null;
        String nextPanel = ConventionalStep.class.getName();  
        JButton clear = new JButton(GlobalData.getResourceString("Unselect_all"));
        
        RecipientsStep()
        {
            super();
            setPanelDescriptorIdentifier(RecipientsStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("Select_recipients"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("If_none_selected"));
            top.add(title);
            top.add(rubric);
            
            nameList = PublicKeyRoot.instance().getRecipientList();
            if(null == nameList)
                return;
            nameList.addListSelectionListener(this);
            
            body.setLayout(new java.awt.BorderLayout());
            
            body.add(new JScrollPane(nameList), BorderLayout.CENTER);
            JPanel tmp = new JPanel();
            clear.addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    RecipientsStep.this.nameList.clearSelection();
                }
            });
            tmp.add(clear);
            body.add(tmp, BorderLayout.SOUTH);
            valueChanged(null);
        }
        public String getNextPanelDescriptor() {
            return nextPanel;
        }
        public String getBackPanelDescriptor() {
            return SecretKeyStep.class.getName();
        }
        public void valueChanged(javax.swing.event.ListSelectionEvent e)
        {
            if(null != e && e.getValueIsAdjusting())
                return;
            if((null == nameList) || (nameList.getSelectedIndex() >= 0))
            {
                clear.setEnabled(true);
                nextPanel = WizardPanelDescriptor.FinishIdentifier;
                getWizard().resetButtonsToPanelRules();
                return;
            }
            clear.setEnabled(false);
            nextPanel = ConventionalStep.class.getName();
            if(getWizard() != null)
                getWizard().resetButtonsToPanelRules();
        }
        public Object[] getSelectedRecipients()
        {
            if(null == nameList)
                return null;
            return nameList.getSelectedValues();
        }
    }

    private static class ConventionalStep extends CommonDescriptor 
    {
        JRadioButton idea = new JRadioButton(GlobalData.getResourceString("IDEA_128bit"),true);
        JRadioButton frfb = new JRadioButton(GlobalData.getResourceString("FRF_Blowfish_40bit"),false);
        JRadioButton b128 = new JRadioButton(GlobalData.getResourceString("Blowfish_128bit"),false);
        JRadioButton teax = new JRadioButton(GlobalData.getResourceString("TEA_128bit"),false);
        JRadioButton CAST = new JRadioButton(GlobalData.getResourceString("CAST_128bit"),false);
        JRadioButton b403 = new JRadioButton(GlobalData.getResourceString("Triple_Blowfish_40bit"),false);
        JRadioButton des3 = new JRadioButton(GlobalData.getResourceString("TripleDES"),false);
        
        JRadioButton cfb = new JRadioButton(GlobalData.getResourceString("CFB"),true);
        JRadioButton cbc = new JRadioButton(GlobalData.getResourceString("CBC"),false);
        
        ConventionalStep()
        {
            super();
            setPanelDescriptorIdentifier(ConventionalStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("Select_algorithm"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("Select_algorithm_stuff"));
            top.add(title);
            top.add(rubric);
            
            JPanel baseplane = new JPanel();
            ButtonGroup algorithms = new ButtonGroup();
            ButtonGroup modes = new ButtonGroup();
            baseplane.setLayout(new java.awt.GridLayout(1,2));
            
            JPanel algpanel = new JPanel();
            algpanel.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Algorithms")));
            algpanel.setLayout(new java.awt.GridLayout(7,1));
            baseplane.add(algpanel);
            
            JPanel modepanel = new JPanel();
            modepanel.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Modes")));
            modepanel.setLayout(new java.awt.GridLayout(7,1));
            baseplane.add(modepanel);
            
            algpanel.add(idea); algorithms.add(idea);
            algpanel.add(frfb); algorithms.add(frfb);
            algpanel.add(b128); algorithms.add(b128);
            algpanel.add(teax); algorithms.add(teax);
            algpanel.add(CAST); algorithms.add(CAST);
            algpanel.add(b403); algorithms.add(b403);
            algpanel.add(des3); algorithms.add(des3);
            
            if(!KeyConstants.isIDEAenabled()) {
                idea.setEnabled(false);
                CAST.setSelected(true);
            }
            
            
            modepanel.add(cfb); modes.add(cfb);
            modepanel.add(cbc); modes.add(cbc);
            body.setLayout(new java.awt.BorderLayout());
            body.add(baseplane, BorderLayout.SOUTH);
        }
        public String getNextPanelDescriptor() {
            return CEAPassphraseStep.class.getName();
        }
        public String getBackPanelDescriptor() {
            return RecipientsStep.class.getName();
        }
        
        public ByteArrayWrapper getCEAParameters() {
            byte[] result = new byte[8];
            result[0] = result[1] =
                    result[2] = result[3] =
                    result[4] = result[5] =
                    result[6] = result[7] = 0;
            
            if(idea.isSelected()) {
                result[0] = KeyConstants.CEA.IDEAFLEX.value();
            } else if(b128.isSelected()) {
                result[0] = KeyConstants.CEA.BLOW16.value();
            } else if(b403.isSelected()) {
                result[0] = KeyConstants.CEA.BLOW5.value();
                result[1] = KeyConstants.CEM.TRIPLE_FLAG.value();
            } else if(frfb.isSelected()) {
                result[0] = (byte)(KeyConstants.CEA.BLOW5.value() | KeyConstants.CEA.MORE_FLAG.value());
                result[2] = result[0];
                result[4] = KeyConstants.CEA.BLOW5.value();
                result[3] = KeyConstants.CEM.REVERSE_FLAG.value();
            } else if(teax.isSelected()) {
                result[0] = KeyConstants.CEA.TEA.value();
            } else if(CAST.isSelected()) {
                result[0] = KeyConstants.CEA.CAST5FLEX.value();
            } else {
                result[0] = KeyConstants.CEA.TRIPLEDESFLEX.value();
            }
            
            int i;
            if(cfb.isSelected()) {
                for(i=0; result[2*i]!=0; ++i) result[1+2*i] |= KeyConstants.CEM.CFB.value();
            } else {
                for(i=0; result[2*i]!=0; ++i) result[1+2*i] |= KeyConstants.CEM.CBC.value();
            }
            
            ByteArrayWrapper r = new ByteArrayWrapper();
            r.data = result;
            return r;
        }
    }
    
    private static class CEAPassphraseStep extends CommonDescriptor 
    {
        GetPassphraseUI passphrase;
        GetPassphraseUI confirm;
        
        CEAPassphraseStep()
        {
            super();
            setPanelDescriptorIdentifier(CEAPassphraseStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("Enter_and_confirm"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("CEAPassphraseStuff"));
            top.add(title);
            top.add(rubric);
            
            JLabel key = new JLabel(GlobalData.getResourceString("Passphrase"));
            passphrase = new GetPassphraseUI();
            
            passphrase.applyLabel(key);
            key.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_P);
            passphrase.addDocumentListener(new javax.swing.event.DocumentListener ()
            {
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    CEAPassphraseStep.this.phrase_textValueChanged();
                }
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    CEAPassphraseStep.this.phrase_textValueChanged();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    CEAPassphraseStep.this.phrase_textValueChanged();
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
                    CEAPassphraseStep.this.phrase_textValueChanged();
                }
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    CEAPassphraseStep.this.phrase_textValueChanged();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    CEAPassphraseStep.this.phrase_textValueChanged();
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
        }
        
        public String getNextPanelDescriptor() {
            return WizardPanelDescriptor.FinishIdentifier;
        }
        public String getBackPanelDescriptor() {
            return ConventionalStep.class.getName();
        }
        public void displayingPanel() {
            passphrase.phrase.setRequestFocusEnabled(true);
            passphrase.phrase.requestFocusInWindow();
        }
        public void aboutToDisplayPanel() {
            getWizardModel().setNextFinishButtonEnabled(Boolean.FALSE);
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
        public CryptoString getCEAPassphrase()
        {
            if(null == passphrase)
                return null;
            CryptoString result = passphrase.getResult();
            passphrase.clearText();
            confirm.clearText();
            return result;
        }
        
    }
    
    public enum Task {
        ENCRYPTION,
        SIGN_ONLY
    }
    
    
    private static void initialiseWizard(Task task, boolean text)
    {
        Wizard wiz = new Wizard(Application.instance().getFrame());
        wiz.setTitle(GlobalData.getResourceString("Encrypt"));
        
        sks = new SecretKeyStep(task, text);
        wiz.registerWizardPanel(sks.getPanelDescriptorIdentifier(),sks);
        wiz.setCurrentPanel(sks.getPanelDescriptorIdentifier());        
        
        pps = new PassphraseStep(task);
        wiz.registerWizardPanel(pps.getPanelDescriptorIdentifier(),pps);
        
        rs = new RecipientsStep();
        wiz.registerWizardPanel(rs.getPanelDescriptorIdentifier(),rs);
        
        cs = new ConventionalStep();
        wiz.registerWizardPanel(cs.getPanelDescriptorIdentifier(),cs);
        
        cpps = new CEAPassphraseStep();
        wiz.registerWizardPanel(cpps.getPanelDescriptorIdentifier(),cpps);
        
        w = wiz;
    }
    
    public static int show(Task task, boolean text)
    {
        ceaParams = null;
        if(ceaPhrase != null)
            ceaPhrase.wipe();
        ceaPhrase = null;
        initialiseWizard(task, text);
        int outcome = w.showModalDialog();
        if(outcome != Wizard.FINISH_RETURN_CODE)
            return outcome;
       
        SecretKey s = getSelectedSignatory();
        if(null != s) setSignatory(s.cHandle());
        
        if(task != Task.SIGN_ONLY)
        {
            Object[] p = EncryptionParameters.getSelectedRecipients();
            for(int i=0; p != null && i<p.length; ++i) {
                PublicKey key = (PublicKey)p[i];
                EncryptionParameters.addRecipient(key.cHandle());
            }
            // conventional key stuff
            if(null == p || 0 == p.length)
            {
                if(cs != null)
                {
                    ceaParams = cs.getCEAParameters();
                    ceaPhrase = cpps.getCEAPassphrase();
                }
            }
            
        }
        return outcome;
    }
    
    public static boolean isEyesOnly()
    {
        if(null == sks)
            return false;
        return sks.isEyesOnly();
    }
    
    private static ByteArrayWrapper ceaParams = null;
    public static ByteArrayWrapper getCEAParameters()
    {
        if(null == ceaParams && null != ceaPhrase)
        {
            ceaPhrase.wipe();
            ceaPhrase = null;
        }   
        return ceaParams;
    }
    
    private static CryptoString ceaPhrase = null;
    public static ByteArrayWrapper getCEAPassphrase()
    {
        ByteArrayWrapper result = new ByteArrayWrapper();
        if(null == ceaPhrase)
        {
            result.data = new byte[0];
        } else {
            int i = ceaPhrase.utf8length();
            result.data = new byte[i];
            ceaPhrase.getUTF8(result.data);
            ceaPhrase.wipe();
            ceaPhrase = null;
        }
        return result;
    }
    
    
    static SecretKey getSelectedSignatory()
    {
        if(null == sks)
            return null;
        return sks.getSelectedSignatory();
    }
    private static Object[] getSelectedRecipients()
    {
        if(null == rs)
            return null;
        return rs.getSelectedRecipients();
    }
    
    static void forceCancel()
    {
        if(w != null)
            w.forceCancel();
    }

    static void restartPassphraseCount()
    {
        if(pps != null)
            pps.restart();
    }
    
    
    private EncryptionParameters() {
    }

    // direct poking to the structure
    public static native void reset();
    public static native void setFile(String name, char type);
    public static native void setVersion(int v);
    public static native boolean addRecipient(long to);
    private static native void setSignatory(long from);
    
    public static void setAlgs(KeyConstants.CEA cea, KeyConstants.CEM cem, 
            KeyConstants.MDA mda, KeyConstants.CPA cpa, KeyConstants.ARM arm)
    {
        setAlgs(cea.value(), cem.value(), mda.value(), cpa.value(), arm.value());        
    }
    private static native void 
            setAlgs(int cea, int cem, int mda, int cpa, int arm);

    static boolean cancelled;

    public static boolean isCancelled()
    {
        return cancelled;
    }
    
    
    // helpful enquiries

    private static  PublicKey[] getRecipients()
    {
        cancelled = false;
        JList nameList = PublicKeyRoot.instance().getRecipientList();
        if(null == nameList)
            return null;
        
        
        nameList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel sub = new JPanel();
        sub.setLayout(new BorderLayout());
        sub.add(new JScrollPane(nameList), BorderLayout.CENTER);
        sub.add(new JLabel(GlobalData.getResourceString("If_none_selected")), BorderLayout.SOUTH);
        
        int mode = JOptionPane.showOptionDialog(
                Application.instance().getFrame(),
                sub,
                GlobalData.getResourceString("Select_recipients"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null
                );
        cancelled = (JOptionPane.CANCEL_OPTION == mode);
        if(cancelled || nameList.getSelectedValues().length == 0)
            return null;
        PublicKey[] result = new PublicKey[nameList.getSelectedValues().length];
        for(int i=0; i<result.length; ++i) {
            result[i] = (PublicKey) nameList.getSelectedValues()[i];
        }
        return result;
  }

    private static SecretKey getSignatory()
    {        
        cancelled = false;
        JList nameList = SecretKeyRoot.instance().getSignatoryList();
        if(null == nameList)
            return null;
        
        int mode = JOptionPane.showOptionDialog(
                Application.instance().getFrame(),
                new JScrollPane(nameList),
                GlobalData.getResourceString("Select_signing_key"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, null, null
                );
        
        cancelled = (JOptionPane.CANCEL_OPTION == mode);
        Object o = nameList.getSelectedValue();
        if(null == o || cancelled)
            return null;
        return (SecretKey) o;
        
    }
}
