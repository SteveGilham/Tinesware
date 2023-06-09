/*
 * FadeToBlack.java
 *
 * Created on 03 July 2004, 07:36
 */

package com.ravnaandtines;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.Serializable;

/**
 *
 * @author  Steve
 */
public class FadeToBlack extends javax.swing.JFrame {
    
    private java.util.Hashtable table = new java.util.Hashtable();
    private Object selected = null;
    
    /** Creates new form FadeToBlack */
    public FadeToBlack() {
        initComponents();
        setSize(new Dimension(800, 600));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
        frameSize.width = screenSize.width;
        setLocation((screenSize.width - frameSize.width) / 2, 
            (screenSize.height - frameSize.height) / 2);
        setTitle("Fade to black");
        editMenu.setVisible(false);
        contentMenuItem.setVisible(false);
        jButton2.setText("          ");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup1 = new javax.swing.ButtonGroup();
        desktopPane = new javax.swing.JDesktopPane();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        upRadio = new javax.swing.JRadioButton();
        leftRadio = new javax.swing.JRadioButton();
        rightRadio = new javax.swing.JRadioButton();
        downRadio = new javax.swing.JRadioButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

        jToolBar1.setOrientation(1);
        jLabel1.setDisplayedMnemonic('F');
        jLabel1.setLabelFor(jButton2);
        jLabel1.setText("Fade Colour");
        jToolBar1.add(jLabel1);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jToolBar1.add(jButton2);

        upRadio.setText("up");
        buttonGroup1.add(upRadio);
        upRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                upRadioStateChanged(evt);
            }
        });

        jToolBar1.add(upRadio);

        leftRadio.setText("left");
        buttonGroup1.add(leftRadio);
        leftRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                leftRadioStateChanged(evt);
            }
        });

        jToolBar1.add(leftRadio);

        rightRadio.setSelected(true);
        rightRadio.setText("right");
        buttonGroup1.add(rightRadio);
        rightRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rightRadioStateChanged(evt);
            }
        });

        jToolBar1.add(rightRadio);

        downRadio.setText("down");
        buttonGroup1.add(downRadio);
        downRadio.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                downRadioStateChanged(evt);
            }
        });

        jToolBar1.add(downRadio);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.WEST);

        fileMenu.setText("File");
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(openMenuItem);

        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setText("Help");
        contentMenuItem.setText("Contents");
        helpMenu.add(contentMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        FadeToBlack_AboutBox dlg = new FadeToBlack_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        JFileChooser opener = new JFileChooser();
        javax.swing.filechooser.FileFilter[] def = opener.getChoosableFileFilters();
        for(int f=0;f<def.length; ++f) opener.removeChoosableFileFilter(def[f]);
        javax.swing.filechooser.FileFilter images = 
            new ExtensionFilter("JPEG image",
            new String[]{".jpg",".jpeg"});
        opener.addChoosableFileFilter(images);
        javax.swing.filechooser.FileFilter images2 = 
            new ExtensionFilter("PNG image",
            new String[]{".png"});
        opener.addChoosableFileFilter(images2);
        opener.setFileFilter(images);
        opener.setSelectedFile(r.file);
        int status = opener.showSaveDialog(this);
        if(JFileChooser.APPROVE_OPTION == status)
        {
            r.file = opener.getSelectedFile();
            saveImage(r);
        }

    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        saveImage(r);
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void downRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_downRadioStateChanged
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        if(null == r) return;
        r.direction = 3;
        r.h.setVisible(false);
        r.v.setVisible(true);
        r.icon.setIcon(genImageIcon(r));
        r.echo.setText(""+r.icon.getIcon().getIconWidth()+", "
                                +r.icon.getIcon().getIconHeight());

    }//GEN-LAST:event_downRadioStateChanged

    private void rightRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rightRadioStateChanged
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        if(null == r) return;
        r.direction = 2;
        r.h.setVisible(true);
        r.v.setVisible(false);
        r.icon.setIcon(genImageIcon(r));
        r.echo.setText(""+r.icon.getIcon().getIconWidth()+", "
                                +r.icon.getIcon().getIconHeight());

        
    }//GEN-LAST:event_rightRadioStateChanged

    private void leftRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_leftRadioStateChanged
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        if(null == r) return;
        r.direction = 1;
        r.h.setVisible(true);
        r.v.setVisible(false);
        r.icon.setIcon(genImageIcon(r));
        r.echo.setText(""+r.icon.getIcon().getIconWidth()+", "
                                +r.icon.getIcon().getIconHeight());


    }//GEN-LAST:event_leftRadioStateChanged

    private void upRadioStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_upRadioStateChanged
        if(null == selected) return;
        ImageRecord r = (ImageRecord)table.get(selected);
        if(null == r) return;
        r.direction = 0;
        r.h.setVisible(false);
        r.v.setVisible(true);
        r.icon.setIcon(genImageIcon(r));
        r.echo.setText(""+r.icon.getIcon().getIconWidth()+", "
                                +r.icon.getIcon().getIconHeight());


    }//GEN-LAST:event_upRadioStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JColorChooser chooser = new JColorChooser(jButton2.getBackground());
        chooser.addChooserPanel(new com.ravnaandtines.util.swing.ColourWheel());
        chooser.addChooserPanel(new com.ravnaandtines.util.swing.ColourSwatch());
        ColorTracker ok = new ColorTracker(chooser);
        ColorChooserDialog dialog = new ColorChooserDialog(this, 
            "Fade Colour", true, chooser, ok, null);
        dialog.addWindowListener(new ColorChooserDialog.Closer());
        dialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());

        dialog.show(); // blocks until user brings dialog down...
        
        if(!dialog.cancelled)
        {
            jButton2.setBackground(ok.getColor());
            if(null == selected) return;
            ImageRecord r = (ImageRecord)table.get(selected);
            if(null == r) return;
            r.fade = ok.getColor();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        // Add your handling code here:
        
        JFileChooser opener = new JFileChooser();
        javax.swing.filechooser.FileFilter[] def = opener.getChoosableFileFilters();
        for(int f=0;f<def.length; ++f) opener.removeChoosableFileFilter(def[f]);
        javax.swing.filechooser.FileFilter images = 
            new ExtensionFilter("Image Files",
            new String[]{".jpg",".jpeg",".gif",".png"});
        opener.addChoosableFileFilter(images);
        opener.setFileFilter(images);
        int status = opener.showOpenDialog(this);
        if(JFileChooser.APPROVE_OPTION == status)
        {
            File f = opener.getSelectedFile();
            loadImage(f);
        }
    }//GEN-LAST:event_openMenuItemActionPerformed
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        boolean skinned = false;
        try {
            if(args.length > 0)
            {
                Class plafClass = Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
                Object plaf = plafClass.newInstance();
                
                Class[] params = new Class[1];
                params[0] = Class.forName("java.lang.String");
                java.lang.reflect.Method m = plafClass.getMethod("loadThemePack", params);
                
                Object[] arglist = new Object[1];
                arglist[0] = args[0];
                Object skin = m.invoke(plaf, arglist);
                
                params[0] = Class.forName("com.l2fprod.gui.plaf.skin.Skin");
                m = plafClass.getMethod("setSkin", params);
                
                arglist[0] = skin;
                m.invoke(plaf, arglist);

                javax.swing.UIManager.setLookAndFeel((javax.swing.LookAndFeel) plaf);
                skinned = true;
                /*
                com.l2fprod.gui.plaf.skin.SkinLookAndFeel plaf = new
                    com.l2fprod.gui.plaf.skin.SkinLookAndFeel(); 
                com.l2fprod.gui.plaf.skin.Skin skin = 
                    plaf.loadThemePack(args[0]);
                plaf.setSkin(skin);
                javax.swing.UIManager.setLookAndFeel(plaf);
                 */
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            skinned = false;
        }
        
        if(!skinned) try {
            javax.swing.UIManager.setLookAndFeel(
            javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {}
        
        new FadeToBlack().show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem contentMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JRadioButton downRadio;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JRadioButton leftRadio;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JRadioButton rightRadio;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JRadioButton upRadio;
    // End of variables declaration//GEN-END:variables
    
    class LocalIcon implements Icon {
        Dimension size;
        ImageRecord r;
        
        public int getIconHeight() {
            return size.height;
        }
        
        public int getIconWidth() {
            return size.width;
        }
                
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(r.fade);
            g.fillRect(x, y, size.width+1, size.height+1);
            Dimension d = new Dimension(r.baseImage.getWidth(c),
                                        r.baseImage.getHeight(c));
            int y2 = (0==r.direction) ?
                y+(size.height-d.height):y;
            int x2 = (1==r.direction) ?
                x+(size.width-d.width) : x;
            g.drawImage(r.baseImage, x2, y2, d.width, 
              d.height, c);
            
            int i, alpha, from, to, range, pixels;
            Color wash;
            switch(r.direction)
            {
                case 0:
                {
                    // fade above this
                    pixels = (int)(size.height*((100-r.v.getValue())/100.0));
                    from = y+size.height-d.height; //100%
                    to = y+pixels; //zero
                    range = from-to;
                    for(i=from; i<to; ++i)
                    {
                        alpha = (int)(255.0 * (from-i))/range;
                        alpha=255-alpha;
                        if(alpha < 0) alpha = 0; else if (alpha > 255) alpha = 255;
                        wash = new Color(r.fade.getRed(),
                        r.fade.getGreen(), r.fade.getBlue(),
                        alpha);
                        g.setColor(wash);
                        g.drawLine(x,i, x+size.width, i);
                    }
                    break;
                }
                case 3:
                {
                    // fade below this
                    pixels = (int)(size.height*((100-r.v.getValue())/100.0));
                    from = y+pixels; to=y+d.height; range=to-from;
                    for(i=from; i<to; ++i)
                    {
                        alpha = (int) (255.0 * (i-from))/range;
                        if(alpha < 0) alpha = 0; 
                        else if (alpha > 255) alpha = 255;
                        wash = new Color(r.fade.getRed(),
                        r.fade.getGreen(), r.fade.getBlue(),alpha);
                        g.setColor(wash);
                       g.drawLine(x,i, x+size.width, i);
                    }
                    break;
                }
                case 1:
                {
                    // fade to the left of this
                    pixels = (int)(size.width*(r.h.getValue()/100.0));
                    from = x+size.width-d.width; //100%
                    to = x+pixels; //zero
                    range = from-to;
                    for(i=from; i<to; ++i)
                    {
                        alpha = (int)
                        (255.0 * (from-i))/range;
                        if(alpha < 0) alpha = 0; else if (alpha > 255) alpha = 255;
                        wash = new Color(r.fade.getRed(),
                        r.fade.getGreen(), r.fade.getBlue(),
                        255-alpha);
                        g.setColor(wash);
                        g.drawLine(i,y, i, y+size.height);
                    }
                    break;
                }
                default:
                {
                    // fade to the right of this
                    pixels = (int)(size.width*(r.h.getValue()/100.0));
                    from = x+pixels; to=x+d.width; range=to-from;
                    for(i=from; i<to; ++i)
                    {
                        alpha = (int) (255.0 * (i-from))/range;
                        if(alpha < 0) alpha = 0; 
                        else if (alpha > 255) alpha = 255;
                        wash = new Color(r.fade.getRed(),
                        r.fade.getGreen(), r.fade.getBlue(),alpha);
                        g.setColor(wash);
                        g.drawLine(i,y, i, y+size.height);
                    }
                    break;
                }


            }
        }
        
    }
    
    Icon genImageIcon(ImageRecord r)
    {
        ImageIcon base = new ImageIcon(r.baseImage);
        Dimension d = new Dimension(base.getIconWidth(), base.getIconHeight());
        if(r.direction == 1 || r.direction == 2)
        {
            if(r.scroll.getViewport().getSize().width > d.width)
                d.width = r.scroll.getViewport().getSize().width;
        }
        else if(r.scroll.getViewport().getSize().height > d.height)
                d.height = r.scroll.getViewport().getSize().height;

        LocalIcon l = new LocalIcon();
        l.size = d;
        l.r = r;
        return l;
    }
    
    void saveImage(ImageRecord r)
    {
        Icon output = genImageIcon(r);
        java.awt.image.BufferedImage bufferedImage = 
        new java.awt.image.BufferedImage(output.getIconWidth(), 
        output.getIconHeight(), 
        java.awt.image.BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
    
        // Draw graphics
        output.paintIcon(this, g2d, 0, 0);
    
        // Graphics context no longer needed so dispose it
        g2d.dispose();
        
        try {
        if(r.file.toString().toLowerCase().endsWith(".png"))
            javax.imageio.ImageIO.write(bufferedImage, "png", r.file);
        else
            javax.imageio.ImageIO.write(bufferedImage, "jpg", r.file);
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this, 
            "Could not save the image\r\n"+r.file+
            "\r\n"+e.getMessage(), //TODO i18n
                "FadeToBlack", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    void loadImage(File file)
    {
        Image image = null;
        String s ="";
        try {
            image = javax.imageio.ImageIO.read(file);
        } catch (java.io.IOException e) {
            image = null;
            s = e.getMessage();
        }
        if(null == image)
        {
            JOptionPane.showMessageDialog(this, 
            "Could not load the image\r\n"+file+
            "\r\n"+s, //TODO i18n
                "FadeToBlack", JOptionPane.ERROR_MESSAGE);
        }
        
        JInternalFrame pane = new JInternalFrame(""+file, true, true, true, true);
        JLabel label = new JLabel(new ImageIcon(image));
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        JLabel echo = new JLabel(""+label.getSize().width+", "
          +label.getSize().height);
        p.add(echo, BorderLayout.SOUTH);
        
        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
        JSlider vertical = new JSlider(JSlider.VERTICAL);
        vertical.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
               if(null == selected) return;
               ImageRecord r = (ImageRecord)table.get(selected);
               r.icon.setIcon(genImageIcon(r));
                JSlider source = (JSlider) e.getSource();
                if(source.getValueIsAdjusting()) return;
                r.icon.setIcon(genImageIcon(r));
            }});
        
        JSlider horizontal = new JSlider(JSlider.HORIZONTAL);
        horizontal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
               if(null == selected) return;
               ImageRecord r = (ImageRecord)table.get(selected);
               r.icon.setIcon(genImageIcon(r));
                JSlider source = (JSlider) e.getSource();
                if(source.getValueIsAdjusting()) return;
                r.icon.setIcon(genImageIcon(r));
            }});

        /*
        inner.add(vertical, BorderLayout.WEST);
        inner.add(horizontal, BorderLayout.SOUTH);
        inner.add(label, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(inner);
         */
        JScrollPane scroll = new JScrollPane(label);
        scroll.setColumnHeaderView(horizontal);
        scroll.setRowHeaderView(vertical);
        
        p.add(scroll, BorderLayout.CENTER);
        pane.getContentPane().add(p);
        
        ImageRecord r = new ImageRecord();
        r.pane = pane;
        r.baseImage = image;
        r.file = file;
        r.icon = label;
        r.echo = echo;
        r.direction = 2;
        r.fade = jButton2.getBackground();
        r.h = horizontal;
        r.v = vertical;
        r.v.setVisible(false);
        r.scroll = scroll;
        selected = pane;
        
        r.scroll.getViewport().setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
        r.scroll.getViewport().setAlignmentX(JLabel.LEFT_ALIGNMENT);
        scroll.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e)
            {
               if(null == selected) return;
               ImageRecord r = (ImageRecord)table.get(selected);
               r.icon.setIcon(genImageIcon(r));
               r.echo.setText(""+r.icon.getIcon().getIconWidth()+", "
                                +r.icon.getIcon().getIconHeight());
               
            }
        });
        
        table.put(pane, r);
        
        
        pane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if(JInternalFrame.IS_CLOSED_PROPERTY == e.getPropertyName()
                 && (Boolean)e.getNewValue() == Boolean.TRUE)
                {
                    table.remove(e.getSource());
                }    
                else if(JInternalFrame.IS_SELECTED_PROPERTY == e.getPropertyName()
                 && (Boolean)e.getNewValue() == Boolean.TRUE)
                {
                    selected = e.getSource();
                    ImageRecord r = (ImageRecord)table.get(e.getSource());
                    setTitle("Fade to Black - "+r.file);
                    switch(r.direction)
                    {
                        case 0:
                            upRadio.setSelected(true);
                            break;
                        case 1:
                            leftRadio.setSelected(true);
                            break;
                        default:
                        case 2:
                            rightRadio.setSelected(true);
                            break;
                        case 3:
                            downRadio.setSelected(true);
                            break;
                    }
                    jButton2.setBackground(r.fade);
                }    
            }});
        desktopPane.add(pane);
        pane.pack();
        pane.setVisible(true); 
        echo.setText(""+label.getSize().width+", "
          +label.getSize().height);
        
    }
}

class ImageRecord {
    public JInternalFrame pane;
    public Image baseImage;
    public File file;
    public JLabel icon;
    public JLabel echo;
    public int direction;
    public Color fade;
    public JSlider h;
    public JSlider v;
    public JScrollPane scroll;
}

class ColorTracker implements java.awt.event.ActionListener, Serializable {
    JColorChooser chooser;
    Color color;

    public ColorTracker(JColorChooser c) {
        chooser = c;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        color = chooser.getColor();
    }

    public Color getColor() {
        return color;
    }
}

class ColorChooserDialog extends JDialog {
    private Color initialColor;
    private JColorChooser chooserPane;
    private Color initial;
    public boolean cancelled = false;

    public ColorChooserDialog(Component c, String title, boolean modal,
        JColorChooser chooserPane,
        java.awt.event.ActionListener okListener, 
        java.awt.event.ActionListener cancelListener)
        throws HeadlessException {
        super(JOptionPane.getFrameForComponent(c), title, modal);
        //setResizable(false);

        this.chooserPane = chooserPane;
        initial = chooserPane.getColor();

	String okString = UIManager.getString("ColorChooser.okText");
	String cancelString = UIManager.getString("ColorChooser.cancelText");
	String resetString = UIManager.getString("ColorChooser.resetText");

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(chooserPane, BorderLayout.CENTER);

        /*
         * Create Lower button panel
         */
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton(okString);
	getRootPane().setDefaultButton(okButton);
        okButton.setActionCommand("OK");
        if (okListener != null) {
            okButton.addActionListener(okListener);
        }
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });
        buttonPane.add(okButton);

        JButton cancelButton = new JButton(cancelString);

        /*
	// The following few lines are used to register esc to close the dialog
	Action cancelKeyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ((AbstractButton)e.getSource()).fireActionPerformed(e);
            }
        }; 
	KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE, false);
	InputMap inputMap = cancelButton.getInputMap(JComponent.
						     WHEN_IN_FOCUSED_WINDOW);
	ActionMap actionMap = cancelButton.getActionMap();
	if (inputMap != null && actionMap != null) {
	    inputMap.put(cancelKeyStroke, "cancel");
	    actionMap.put("cancel", cancelKeyAction);
	}
	// end esc handling
         */

        cancelButton.setActionCommand("cancel");
        if (cancelListener != null) {
            cancelButton.addActionListener(cancelListener);
        }
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                hide();
            }
        });
        buttonPane.add(cancelButton);

        JButton resetButton = new JButton(resetString);
        resetButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               reset();
           }
        });
        /*
        int mnemonic = UIManager.getInt("ColorChooser.resetMnemonic", -1);
        if (mnemonic != -1) {
            resetButton.setMnemonic(mnemonic);
        }
         */
        buttonPane.add(resetButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = 
            UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                getRootPane().setWindowDecorationStyle(JRootPane.COLOR_CHOOSER_DIALOG);
            }
        }
        applyComponentOrientation(((c == null) ? getRootPane() : c).getComponentOrientation());

        pack();
        setLocationRelativeTo(c);
    }

    public void show() {
        initialColor = chooserPane.getColor();
        super.show();
    }

    public void reset() {
        chooserPane.setColor(initialColor);
    }

    static class Closer extends java.awt.event.WindowAdapter implements Serializable{
        public void windowClosing(java.awt.event.WindowEvent e) {
            Window w = e.getWindow();
            w.hide();
        }
    }

    static class DisposeOnClose extends java.awt.event.ComponentAdapter implements Serializable{
        public void componentHidden(java.awt.event.ComponentEvent e) {
            Window w = (Window)e.getComponent();
            w.dispose();
        }
    }

}


