## bare-bones makefile for jar file; assumes JDK 1.3 in the parameters used

OBJDIR = ../../cbout/classes
OUTDIR = ../../cbout
JAVAHOME = /opt/jbuilder4/jdk1.3
JAVABIN = $(JAVAHOME)/bin

SRCFILES =  ./com/ravnaandtines/ctcjava/CTC.java \
    ./com/ravnaandtines/ctcjava/CJAlgPanel.java \
    ./com/ravnaandtines/ctcjava/CJseckeySelector.java \
    ./dtai/gwt/AppletFrame.java \
    ./dtai/gwt/AppletManager.java \
    ./dtai/gwt/AppletWrapper.java \
    ./dtai/gwt/ArrowButtonGadget.java \
    ./dtai/gwt/BorderGadget.java \
    ./dtai/gwt/ButtonGadget.java \
    ./dtai/gwt/CanvasGadget.java \
    ./dtai/gwt/CellEvent.java \
    ./dtai/gwt/CellListener.java \
    ./com/ravnaandtines/ctcjava/CJMiscPanel.java \
    ./dtai/gwt/CellSelectable.java \
    ./dtai/gwt/ChoiceGadget.java \
    ./dtai/gwt/ClickableGadget.java \
    ./dtai/gwt/ComboBoxGadget.java \
    ./dtai/gwt/ComponentShell.java \
    ./dtai/gwt/ContainerGadget.java \
    ./dtai/gwt/DisplayGadget.java \
    ./dtai/gwt/DisplayImageFilter.java \
    ./dtai/gwt/Gadget.java \
    ./dtai/gwt/GadgetBorderLayout.java \
    ./com/ravnaandtines/ctcjava/CJFileFrame.java \
    ./dtai/gwt/GadgetCardLayout.java \
    ./dtai/gwt/GadgetCell.java \
    ./dtai/gwt/GadgetClipboard.java \
    ./dtai/gwt/GadgetCursor.java \
    ./dtai/gwt/GadgetDialog.java \
    ./dtai/gwt/GadgetFlowLayout.java \
    ./dtai/gwt/GadgetGraphics.java \
    ./dtai/gwt/GadgetHelp.java \
    ./dtai/gwt/GadgetLayoutManager.java \
    ./dtai/gwt/GadgetManager.java \
    ./com/ravnaandtines/ctcjava/CJTempfile.java \
    ./dtai/gwt/GadgetPanel.java \
    ./dtai/gwt/GadgetShell.java \
    ./dtai/gwt/GadgetTimer.java \
    ./dtai/gwt/GWTEventMulticaster.java \
    ./dtai/gwt/ImageGadget.java \
    ./dtai/gwt/ImagePanelGadget.java \
    ./dtai/gwt/LabelGadget.java \
    ./dtai/gwt/LinesPanel.java \
    ./dtai/gwt/ListGadget.java \
    ./dtai/gwt/ListGridGadget.java \
    ./com/ravnaandtines/ctcjava/CJPubkeyPanel.java \
    ./dtai/gwt/ListGridItem.java \
    ./dtai/gwt/ListManager.java \
    ./dtai/gwt/Lock.java \
    ./dtai/gwt/MenuBarGadget.java \
    ./dtai/gwt/MenuGadget.java \
    ./dtai/gwt/MenuItemGadget.java \
    ./dtai/gwt/MultiCaster.java \
    ./dtai/gwt/OverlayPanelGadget.java \
    ./dtai/gwt/PanelGadget.java \
    ./dtai/gwt/PopupMenuGadget.java \
    ./com/ravnaandtines/ctcjava/CJPubkey.java \
    ./dtai/gwt/ScrollbarGadget.java \
    ./dtai/gwt/ScrollGadgetGadget.java \
    ./dtai/gwt/ScrollListGadget.java \
    ./dtai/gwt/ScrollPaneGadget.java \
    ./dtai/gwt/SeparatorGadget.java \
    ./dtai/gwt/ShellPaintEvent.java \
    ./dtai/gwt/ShellPaintListener.java \
    ./dtai/gwt/ShellPaintMultiCaster.java \
    ./dtai/gwt/SpecialFont.java \
    ./dtai/gwt/SpecialFontMetrics.java \
    ./com/ravnaandtines/ctcjava/CJdecodeContext.java \
    ./dtai/gwt/Sprite.java \
    ./dtai/gwt/TextAreaGadget.java \
    ./dtai/gwt/TextFieldGadget.java \
    ./dtai/gwt/TextGadget.java \
    ./dtai/gwt/TextLine.java \
    ./dtai/gwt/TipGadget.java \
    ./dtai/gwt/TreeEvent.java \
    ./dtai/gwt/TreeItem.java \
    ./dtai/gwt/TreeListener.java \
    ./dtai/gwt/TreeNode.java \
    ./com/ravnaandtines/ctcjava/CJctclib.java \
    ./dtai/gwt/TreePanel.java \
    ./dtai/gwt/TreeSelectable.java \
    ./dtai/util/Debug.java \
    ./dtai/util/ShowUser.java \
    ./dtai/util/ShowUserHandler.java \
    ./dtai/util/StatusHandler.java \
    ./java11/awt/event/ActionEvent.java \
    ./java11/awt/event/ActionListener.java \
    ./java11/awt/event/AdjustmentEvent.java \
    ./java11/awt/event/AdjustmentListener.java \
    ./com/ravnaandtines/ctcjava/CJencryptInsts.java \
    ./java11/awt/event/AWTEvent.java \
    ./java11/awt/event/ComponentEvent.java \
    ./java11/awt/event/ComponentListener.java \
    ./java11/awt/event/FocusEvent.java \
    ./java11/awt/event/FocusListener.java \
    ./java11/awt/event/InputEvent.java \
    ./java11/awt/event/ItemEvent.java \
    ./java11/awt/event/ItemListener.java \
    ./java11/awt/event/KeyEvent.java \
    ./java11/awt/event/KeyListener.java \
    ./com/ravnaandtines/ctcjava/CJSeckey.java \
    ./java11/awt/event/MouseEvent.java \
    ./java11/awt/event/MouseListener.java \
    ./java11/awt/event/MouseMotionListener.java \
    ./java11/awt/event/TextAdapter.java \
    ./java11/awt/event/TextEvent.java \
    ./java11/awt/event/TextListener.java \
    ./java11/awt/event/WindowEvent.java \
    ./java11/awt/event/WindowListener.java \
    ./java11/awt/ItemSelectable.java \
    ./java11/awt/Adjustable.java \
    ./com/ravnaandtines/ctcjava/CJSeckeyPanel.java \
    ./java11/util/EventListener.java \
    ./java11/util/EventObject.java \
    ./java11/awt/event/MouseAdapter.java \
    ./dtai/gwt/GadgetGridLayout.java \
    ./dtai/gwt/TreeFolder.java \
    ./dtai/gwt/TabPaneGadget.java \
    ./dtai/gwt/CheckboxGadget.java \
    ./dtai/gwt/TreeViewGadget.java \
    ./dtai/gwt/GadgetGridBagLayout.java \
    ./dtai/gwt/CheckboxIndicatorGadget.java \
    ./dtai/gwt/GadgetGridBagConstraints.java \
    ./dtai/gwt/CheckboxGadgetGroup.java \
    ./dtai/gwt/RadioGadget.java \
    ./dtai/gwt/CheckGadget.java \
    ./dtai/gwt/FolderItem.java \
    ./com/ravnaandtines/ctcjava/CJGetPassphrase.java \
    ./com/ravnaandtines/ctcjava/CJGet1Passphrase.java \
    ./com/ravnaandtines/ctcjava/StatusBar.java \
    ./com/ravnaandtines/ctcjava/ButtonBar.java \
    ./com/ravnaandtines/ctcjava/CryptoTextBuffer.java \
    ./com/ravnaandtines/ctcjava/CTCJMainFrame.java \
    ./com/ravnaandtines/ctcjava/CryptoTextTokenizer.java \
    ./dtai/gwt/CryptoTextLine.java \
    ./com/ravnaandtines/ctcjava/CryptoString.java \
    ./dtai/gwt/CryptoTextFieldGadget.java \
    ./dtai/gwt/CJTextAreaGadget.java \
    ./dtai/gwt/CJLinesPanel.java \
    ./com/ravnaandtines/ctcjava/GroupBox.java \
    ./com/ravnaandtines/ctcjava/CTCIKeyConst.java \
    ./com/ravnaandtines/ctcjava/CJConvKeyAlgDlg.java \
    ./com/ravnaandtines/ctcjava/CJKeyGenDlg.java \
    ./com/ravnaandtines/ctcjava/CJRSAGenDlg.java \
    ./com/ravnaandtines/ctcjava/CJKeyProtDlg.java \
    ./com/ravnaandtines/ctcjava/CJRanDlg.java \
    ./com/ravnaandtines/util/StickUp.java \
    ./com/ravnaandtines/util/Logo.java \
    ./com/ravnaandtines/util/MessageBox.java \
    ./com/ravnaandtines/util/QSort.java \
    ./com/ravnaandtines/util/IconSelection.java \
    ./com/ravnaandtines/util/Sortable.java \
    ./com/ravnaandtines/ctcjava/PubKeyFolder.java \
    ./com/ravnaandtines/ctcjava/SecKeyFolder.java \
    ./com/ravnaandtines/ctcjava/Username.java \
    ./com/ravnaandtines/ctcjava/UsernameFolder.java \
    ./com/ravnaandtines/ctcjava/Signature.java \
    ./com/ravnaandtines/ctcjava/SignatureFolder.java \
    ./com/ravnaandtines/ctcjava/CJGlobals.java \
    ./com/ravnaandtines/ctcjava/Res.java \
    ./com/ravnaandtines/util/Res.java \
    ./com/ravnaandtines/ctcjava/CJConfig.java \
    ./com/ravnaandtines/ctcjava/CTCJLicenceDialog.java \
    ./com/ravnaandtines/ctcjava/CJFilesPanel.java \
    ./com/ravnaandtines/ctcjava/CJAboutPanel.java \
    ./com/ravnaandtines/ctcjava/POP3Dialog.java \
    ./com/ravnaandtines/ctcjava/Message.java \
    ./com/ravnaandtines/ctcjava/SMTPDialog.java


JAVAC = $(JAVABIN)/javac -classpath $(OBJDIR) -d $(OBJDIR) -sourcepath .
JAVACs = $(JAVAC) $<

OBJFILES = $(OBJDIR)/com/ravnaandtines/ctcjava/CTC.class \
           $(OBJDIR)/com/ravnaandtines/ctcjava/Res.class \


$(OUTDIR)/ctcjava.jar: $(OBJFILES)
	cd $(OBJDIR)
	jar -cvf $(OUTDIR)/ctcjava.jar *

$(OBJDIR)/com/ravnaandtines/ctcjava/CTC.class: $(SRCFILES)
	$(JAVACs)

$(OBJDIR)/com/ravnaandtines/ctcjava/Res.class:  ./com/ravnaandtines/ctcjava/Res.java
	$(JAVACs)

$(OBJDIR)/java11/awt/event/TextAdapter.class:  ./ava11/awt/event/TextAdapter.java
	$(JAVACs)


.PHONY : clean

clean :
	rm -rf $(OBJDIR)/*
	rm -f $(OUTDIR)/ctcjava.jar



