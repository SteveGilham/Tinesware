/*
 * TreeRenderer.java
 *
 * Created on 27 December 2005, 17:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.ImageIcon;

/**
 *
 * @author Steve
 */
public class TreeRenderer implements javax.swing.tree.TreeCellRenderer {
    
    /** Creates a new instance of TreeRenderer */
    public TreeRenderer() {
        renderer.setClosedIcon(new ImageIcon(IconSelection.FOLDERCLOSED.getImage()));
        renderer.setOpenIcon(new ImageIcon(IconSelection.FOLDEROPEN.getImage()));
        renderer.setLeafIcon(new ImageIcon(IconSelection.LEAF.getImage()));
    }
    
    javax.swing.tree.DefaultTreeCellRenderer renderer =
            new javax.swing.tree.DefaultTreeCellRenderer();
    javax.swing.tree.DefaultTreeCellRenderer special =
            new javax.swing.tree.DefaultTreeCellRenderer();
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
            boolean selected, boolean expanded, boolean leaf, 
            int row, boolean hasFocus)
    {
        if(value instanceof Document)
        {
            Document doc = (Document) value;
            if(doc.isTextDocument())
                special.setLeafIcon(new ImageIcon(IconSelection.TEXT.getImage()));
            else
                special.setLeafIcon(new ImageIcon(IconSelection.BINARY.getImage()));
            return special.getTreeCellRendererComponent(tree, doc.getFileName(),
                    selected, expanded, true, row, hasFocus);
                
        } else if (value instanceof PublicKey) {
            renderer.setClosedIcon(new ImageIcon(IconSelection.KEY.getImage()));
            renderer.setOpenIcon(new ImageIcon(IconSelection.KEY.getImage()));            
        } else if (value instanceof SecretKey) {
            java.awt.Image icon = ((SecretKey)value).isLocked() ?
                IconSelection.LOCK.getImage() :
                IconSelection.UNLOCK.getImage();
            javax.swing.ImageIcon iicon = new javax.swing.ImageIcon(icon);
            renderer.setClosedIcon(iicon);
            renderer.setOpenIcon(iicon);            
        } else if (value instanceof Username) {
            renderer.setClosedIcon(new ImageIcon(IconSelection.USER.getImage()));
            renderer.setOpenIcon(new ImageIcon(IconSelection.USER.getImage()));            
        } else if (value instanceof Signature) {
            java.awt.Image icon = ((Signature)value).getImage();
            javax.swing.ImageIcon iicon = icon == null ? 
                null : new javax.swing.ImageIcon(icon);
            renderer.setClosedIcon(iicon);
            renderer.setOpenIcon(iicon);            
        } else if (value instanceof KeyName) {
            special.setLeafIcon(null);
            return special.getTreeCellRendererComponent(tree, value,
                    selected, expanded, true, row, hasFocus);
        } else {
            renderer.setClosedIcon(new ImageIcon(IconSelection.FOLDERCLOSED.getImage()));
            renderer.setOpenIcon(new ImageIcon(IconSelection.FOLDEROPEN.getImage()));            
        }         
        
        return renderer.getTreeCellRendererComponent(tree, 
                value, selected, expanded, leaf, row, hasFocus);
    }
}
