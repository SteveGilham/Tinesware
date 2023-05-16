/****************************************************************
 **
 **  $Id: GadgetTimer.java,v 1.10 1997/08/06 23:27:05 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetTimer.java,v $
 **
 ****************************************************************
 **
 **  Gadget Windowing Toolkit (GWT) Java Class Library
 **  Copyright (C) 1997  DTAI, Incorporated (http://www.dtai.com)
 **
 **  This library is free software; you can redistribute it and/or
 **  modify it under the terms of the GNU Library General Public
 **  License as published by the Free Software Foundation; either
 **  version 2 of the License, or (at your option) any later version.
 **
 **  This library is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 **  Library General Public License for more details.
 **
 **  You should have received a copy of the GNU Library General Public
 **  License along with this library (file "COPYING.LIB"); if not,
 **  write to the Free Software Foundation, Inc.,
 **  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **
 ****************************************************************/

package dtai.gwt;


/**
 * GadgetTimer
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetTimer extends Thread {

    private GadgetShell shell;
    private byte timeoutEventType;
    private byte interruptEventType;
    private int msGranularity;
    private int msTimeout;
    private boolean interrupted = false;
    private boolean stopped = false;

    /**
     * GadgetTimer
     * @param shell - TBD
     * @param timeoutEventType - TBD
     * @param interruptEventType - TBD
     * @param msGranularity - TBD
     * @param msTimeout - TBD
     */
    public GadgetTimer( GadgetShell shell, byte timeoutEventType, byte interruptEventType,
                        int msGranularity, int msTimeout ) {
		super("dtai.gwt.GadgetTimer");
        this.shell = shell;
        this.timeoutEventType = timeoutEventType;
        this.interruptEventType = interruptEventType;
        this.msGranularity = msGranularity;
        this.msTimeout = msTimeout;
        start();
    }

    /**
     * setTimeout
     * @param msTimeout - TBD
     */
    public void setTimeout( int msTimeout ) {
        this.msTimeout = msTimeout;
    }

    /**
     * interrupt
     */
    public void interrupt() {
        interrupted = true;
        //super.interrupt();
    }

    /**
     * stopThread
     */
    public void stopThread() {
        stopped = true;
    }

    /**
     * run
     */
    public void run() {

        while( ! stopped ) {
            int timeout = msTimeout;
            int interval = msGranularity;

            while ( ( timeout > 0 ) &&
                    ( ! interrupted ) ) {
                timeout -= interval;
                if ( timeout < 0 ) {
                    interval += timeout;
                }
                try {
                    sleep( interval );
                }
                catch ( InterruptedException ie ) {
                    interrupted = true;
                }
            }

            if ( interrupted ) {
                interrupted = false;
                shell.processTimerEvent( interruptEventType );
            }
            else {
                shell.processTimerEvent( timeoutEventType );
            }
        }
    }
}
