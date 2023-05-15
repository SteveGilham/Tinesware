package com.ravnaandtines.util.lang;

import java.util.*;

/**
*  Class Plugin - dynamic class loading to a theme
*  <P>
*  Coded Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
*  and released into the public domain
*  <P>
* THIS SOFTWARE IS PROVIDED BY THE AUTHORS ''AS IS'' AND ANY EXPRESS
* OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
* BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
* OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
* EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*  <P>
* @author Mr. Tines
* @version 1.0 27-Dec-1998
*/


public class Plugin
{
    /**
    * No sense to instantiate - just static methods
    */
    private Plugin()
    {
    }

    /**
    * finds a Class for the named resource tag
    * @param res tag to class mapping
    * @param name tag name
    * @return class according to name; null if not found
    */
    public static Class find(ResourceBundle res, String name)
    {
        Class result = null;
        try {
            result = Class.forName(res.getString(name));
        } catch ( MissingResourceException mre) { return null;}
        catch ( ClassNotFoundException cnfe) { return null;}
        return result;
    }

    /**
    * returns an instance of the class
    * @param c class to instantiate; null if not found
    */
    public static Object build(Class c)
    {
        if(null == c) return null;
        try {
            return c.newInstance();
        } catch (IllegalAccessException iae) {}
        catch (InstantiationException ie) {}
        return null;
    }

    /**
    * finds a Class for the named resource tag that implements or extends the
    * indicated class
    * @param res tag to class mapping
    * @param name tag name
    * @param base base class or interface which the named class must
    * extend or implement
    * @return class according to name; null if not found or not valid
    */
    public static Class find(ResourceBundle res, String name, Class base)
    {
        if( base.isPrimitive())
        {
                return null;
        }

        Class result = find(res, name);
        if(result == null)
        {
            return result;
        }

        if(!base.isAssignableFrom(result))
        {
            return null;
        }
        return result;
    }
}