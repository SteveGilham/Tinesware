/*
 * ExtensionFilter.java
 *
 * Created on 03 July 2004, 08:07
 */

package com.ravnaandtines;

/**
 *
 * @author  Steve
 */
public class ExtensionFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of ExtensionFilter */
    private ExtensionFilter() {
    }    

    private String extensions[];
    private String description;
    
    public ExtensionFilter(String description, String extension) {
      this(description, new String[] {extension});
    }
    
    public ExtensionFilter(String description, String extensions[]) {
      this.description = description;
      this.extensions = (String[])extensions.clone();
    }
    
    public boolean accept(java.io.File file) {
      if (file.isDirectory()) {
        return true;
      }
      int count = extensions.length;
      String path = file.getAbsolutePath().toLowerCase();
      for (int i =0;i < count;i++) {
        String ext = extensions[i].toLowerCase();
        if (path.endsWith(ext) && 
            (path.charAt(path.length()-ext.length()) == '.')) {
          return true;
        }
      }
      return false;
    }
    public String getDescription() {
      return(description == null ? extensions[0] : description);
    }
}

