/*
 * JXHTML.java
 *
 * XHTML Validator
 *
 * Public domain application implementation by Mr. Tines <tines@ravnaandtines.com>
 *
 * Version 1.0 (3-May-03)
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ravnaandtines.jxhtml;

import org.apache.xerces.parsers.*;
import org.xml.sax.*;
import org.apache.xerces.xni.parser.*;

/**
 *
 * @author  Tines
 */
public class JXHTML {
    
    /** Creates a new instance of JXHTML */
    private JXHTML() {
    }
    
    public static String doctype = "";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
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
        }
        
        new GUI().show();
    }
    
}

class MyXMLInputSource extends XMLInputSource
{
    private int schema = -1;
    public MyXMLInputSource(XMLInputSource xMLInputSource, int schema) 
        throws java.io.FileNotFoundException
    {
        super(
            xMLInputSource.getPublicId(),
            xMLInputSource.getSystemId(),
            xMLInputSource.getBaseSystemId()
            );
        this.schema = schema;
        JXHTML.doctype = xMLInputSource.getPublicId()+System.getProperty("line.separator");
        JXHTML.doctype += xMLInputSource.getSystemId()+System.getProperty("line.separator");
        if(xMLInputSource.getBaseSystemId() != null)
            JXHTML.doctype += xMLInputSource.getBaseSystemId()+System.getProperty("line.separator");
        JXHTML.doctype += System.getProperty("line.separator");
//        System.out.println("MyXMLInputSource() "+this.schema);
    }
    
    public String getSystemId()
    {
//        System.out.println("MyXMLInputSource.getSystemId() "+schema);
        //Throwable t = new Throwable();
        //t.printStackTrace(System.out);
        //return "e:\\projects\\validator\\dtd\\xhtml1-strict.dtd";
        java.net.URL u = null;
        switch (schema)
        {
            default:
            u = ClassLoader.getSystemResource("xhtml1-strict.dtd");
            break;
            case 0:
            u = ClassLoader.getSystemResource("xhtml1-transitional.dtd");
            break;
            case 2:
            u = ClassLoader.getSystemResource("xhtml11.dtd");
            break;
            case 3:
            String q = super.getSystemId();
            if(q.endsWith("/xhtml11.dtd"))
                u = ClassLoader.getSystemResource("xhtml11.dtd");
            else if(q.endsWith("/xhtml1-transitional.dtd"))
                u = ClassLoader.getSystemResource("xhtml1-transitional.dtd"); 
            else
                u = ClassLoader.getSystemResource("xhtml1-strict.dtd");
            break;
        }
//        System.out.println(u.toString());
        
        return u.toString();
    }
}

class MyXMLDTDScanner extends org.apache.xerces.impl.XMLDTDScannerImpl
{
    private int schema;
    public MyXMLDTDScanner(int schema)
    {
        super();
        this.schema = schema;
//        System.out.println("MyXMLDTDScanner() "+this.schema);
    }
    
    public void setInputSource(org.apache.xerces.xni.parser.XMLInputSource xMLInputSource) 
       throws java.io.IOException 
    {
        XMLInputSource replacement = new MyXMLInputSource(xMLInputSource, schema);
        super.setInputSource(replacement);
    }
}

class XHTML1TransitionalConfiguration extends XML11Configuration
{
    public XHTML1TransitionalConfiguration()
    {
        super();
    }
    
    protected XMLDTDScanner createDTDScanner()
    {
        return new MyXMLDTDScanner(0);
    }
}

class XHTML1StrictConfiguration extends XML11Configuration
{
    public XHTML1StrictConfiguration()
    {
        super();
    }
    
    protected XMLDTDScanner createDTDScanner()
    {
        return new MyXMLDTDScanner(1);
    }
}

class XHTML11Configuration extends XML11Configuration
{
    public XHTML11Configuration()
    {
        super();
    }
    
    protected XMLDTDScanner createDTDScanner()
    {
        return new MyXMLDTDScanner(2);
    }
}

class ByDocumentConfiguration extends XML11Configuration
{
    public ByDocumentConfiguration()
    {
        super();
    }
    
    protected XMLDTDScanner createDTDScanner()
    {
        return new MyXMLDTDScanner(3);
    }
}
