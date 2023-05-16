/*
 * TableEditor.java
 *
 * Created on 28 December 2005, 20:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.ravnaandtines.ctcjava;
import javax.swing.*;
/**
 *
 * @author Steve
 */
public class TableEditor extends javax.swing.AbstractCellEditor 
   implements javax.swing.table.TableCellEditor {
    
    /** Creates a new instance of TableEditor */
    public TableEditor() {
        boolCombo.addItem(Boolean.FALSE);
        boolCombo.addItem(Boolean.TRUE);
        boolCombo.setRenderer(new BooleanCellRenderer());

    }
    
    
    private class BooleanCellRenderer extends DefaultListCellRenderer {
        public java.awt.Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel cheat = (JLabel) super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);
            cheat.setText("");
            Boolean b = (Boolean) value;
            if(b.booleanValue())
                cheat.setIcon(new ImageIcon(IconSelection.TICK.getImage()));
            else
                cheat.setIcon(new ImageIcon(IconSelection.CROSS.getImage()));
            return cheat;
        }
    }
    
    public java.awt.Component getTableCellEditorComponent(JTable table,
                                      Object value,
                                      boolean isSelected,
                                      int row,
                                      int column)    
    {
        theValue = value;
        if(value instanceof JComboBox)
        {   
            Root.instance().updated();
            return (JComboBox) value;
        } else if (value instanceof Boolean) {
            return boolCombo;
        } else if (value instanceof JLabel) {
            JLabel v = (JLabel) value;
            text.setText(v.getText());
            return text;
        } else if (value instanceof PublicKeyRoot) {
                ((PublicKeyRoot)value).findKeyRing();
                return renderer.getTableCellRendererComponent
                    (table,
                     value,
                     isSelected,
                     true,
                     row,
                     column);                
                
        } else if (value instanceof SecretKeyRoot) {
                ((SecretKeyRoot)value).findKeyRing();
                return renderer.getTableCellRendererComponent
                    (table,
                     value,
                     isSelected,
                     true,
                     row,
                     column);                
        }            
        return tmp;
    }

    JLabel tmp = new JLabel();
    JComboBox boolCombo = new JComboBox();
    Object theValue = null;
    JTextField text = new JTextField();
    private static TableRenderer renderer = new TableRenderer();    
    
    public Object getCellEditorValue() 
    {
        if(theValue instanceof JComboBox)
            return ((JComboBox)theValue).getSelectedItem();
        else if (theValue instanceof Boolean) {
            int i = boolCombo.getSelectedIndex();            
            return Boolean.valueOf(i>0);
        } else if (theValue instanceof JLabel) {
            JLabel value = (JLabel) theValue;
            value.setText(text.getText());
            return value;
        } else if (theValue instanceof PublicKeyRoot) {
                return theValue;
        } else if (theValue instanceof SecretKeyRoot) {
                return theValue;
        }            

        
        return tmp;
    }

}
