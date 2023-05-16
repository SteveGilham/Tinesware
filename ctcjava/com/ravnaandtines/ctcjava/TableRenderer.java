/*
 * TableRenderer.java
 *
 * Created on 27 December 2005, 19:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;
import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author Steve
 */
public class TableRenderer  implements javax.swing.table.TableCellRenderer {
    
    /** Creates a new instance of TableRenderer */
    public TableRenderer() {
        title.setHorizontalAlignment(JLabel.LEFT);
        javax.swing.border.Border outer =
                javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK);
        javax.swing.border.Border inner =
                javax.swing.BorderFactory.createEmptyBorder(5,5,5,5);
        title.setBorder(javax.swing.BorderFactory.createCompoundBorder(outer, inner));
        synchronized(renderer) {
            if(null == yes) {
                yes = new ImageIcon(IconSelection.TICK.getImage());
                no = new ImageIcon(IconSelection.CROSS.getImage());
            }
        }
    }
    
    private static Icon yes = null;
    private static Icon no = null;
    
    private static javax.swing.table.DefaultTableCellRenderer renderer =
            new javax.swing.table.DefaultTableCellRenderer();
    
    JLabel title = new JLabel();
    
    public Component getTableCellRendererComponent(JTable table,
                                        Object value,
                                        boolean isSelected,
                                        boolean hasFocus,
                                        int row,
                                        int column)
    {
        Component c = renderer.getTableCellRendererComponent(table,
                     value, isSelected, hasFocus, row, column);
        JLabel worker = (JLabel) c; // hack
        worker.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));        
        if(value instanceof String)
            worker = title;
                
        if(value instanceof Boolean)
        {
            worker.setIcon(((Boolean)value).booleanValue() ? yes : no);
            worker.setText("");
        }
        else if(value instanceof JComboBox)
        {
            JComboBox box = (JComboBox)value;
            String tmp = box.getSelectedItem().toString();            
            worker.setText(GlobalData.getResourceString(tmp));
            worker.setIcon(null);
        }
        else if(value instanceof String)
        {
            worker.setText((String) value);
        }
        else if(value instanceof javax.swing.JLabel)
        {
            worker.setText(((JLabel)value).getText());
            worker.setIcon(((JLabel)value).getIcon());
        } else if (value instanceof PublicKeyRoot) {
            worker.setText(
                Root.instance().getProperty("PublicKeyringDirectory", "")+
                Root.instance().getProperty("PublicKeyring", ""));
            ImageIcon i = null;
            if(PublicKeyRoot.instance().isValid())
                i = new ImageIcon(IconSelection.READONLY.getImage());

            worker.setIcon(i);
        } else if (value instanceof SecretKeyRoot) {
            worker.setText(
                Root.instance().getProperty("SecretKeyringDirectory", "")+
                Root.instance().getProperty("SecretKeyring", ""));
            ImageIcon i = null;
            if(SecretKeyRoot.instance().isValid())
                i = new ImageIcon(IconSelection.READONLY.getImage());
            worker.setIcon(i);
        }
        return worker;
    }
}
