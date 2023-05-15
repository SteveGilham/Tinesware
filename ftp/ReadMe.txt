FTP utility
===========

This is designed for one main purpose - working in passive mode.  
This means that the FTP server is a server at all times unlike in 
conventional FTP, file transfer is done by having your client 
machine acting as a server.  This normal behaviour is not firewall 
friendly (for example with ZoneAlarm, the free firewall from
www.zonelabs.com, even setting the normal Windows command line
FTP client to be a server, the firewall still blocks the incoming
requests).

Put ftp.jar into your classpath; edit the ftp.ini file to have 
the default username and hostname, and in that directory run

	java -cp %classpath%;ftp.jar com.ravnaandtines.ftp.FTP

or for those using the Micrsoft VM

	jview /cp:a ftp.jar com.ravnaandtines.ftp.FTP


Add the password (and chnage username and hostname if you want to
go elsewhere), and hit Connect.  This will establish one FTP
session that lasts until the server closes it or you close the
dialog that results.

Buttons across the top act on the text input

cd 	- changes server side directory to the given text
mkdir 	- creates a new directory of that name in the current directory
delete 	- deletes the file named
get 	- fetches the file named (and launches a FileDialog for the
local destination)

Buttons on the bottom do other actions

Upload 	- launches a FileDialog to select a file to upload to the
current server side directory
ls -al 	- gives detailed remote directory listing
ls 	- gives simple directory listing
look 	- shows the current remote directory

The ASCII mode checkbox should be set while getting or fetching
text files only.

A FileDialog is launched in the last directory that was selected 
(starts the in the working directory at program start).

Console session style output is shown in the middle text area, and
some status messages below the ASCII mode check box.

Java version 1.1 is all that is required (so Microsoft's VM with
the jview command will do); all the GUI is pure AWT, no Swing, just
a custom (usable but not perfect) display only scrolling text area
that doesn't have java.awt.TextArea's maximum string length problem,
and will handle hundreds of kb of text responsively.

Licensing
=========

This uses FTP code derived from the Linlyn class written by 
Robert Lynch and Peter van der Linden  (Author of "Just Java" book),
which is distributed under the ARTISTIC LICENSE.  Terms of the
license are shown at the end of Linlyn.java; so in this bundle
all code is also covered by that license.

This work is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.


