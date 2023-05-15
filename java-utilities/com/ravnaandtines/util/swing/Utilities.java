package com.ravnaandtines.util.swing;
import javax.swing.*;
/**
*  class Utilities - static Swing utility classes
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
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
*<p>
* @author Mr. Tines
* @version 1.0 08-Nov-1998
*
*/



public class Utilities
{

    private Utilities()
    {
    }

    /*
    * Does the the run() method now if from the event dispatch
    * thread, or uses invokeAndWait in other circumstances -
    * essentially a generally useable invokeAndWait.
    * @param doRun the action to do, presumably on the GUI
    */
    public static void invokeNow(Runnable doRun)
    {
        if(SwingUtilities.isEventDispatchThread())
        {
            doRun.run();
        }
        else try
        {
            SwingUtilities.invokeAndWait(doRun);
        }
        catch(Exception e) {}
    }

} 