Pickle & Revive
===============

Use your Palm as a detachable file store.  Put Pickle.jar in your classpath
and using Java 1.2.2 (presumably later versions should work, but that's all
I've tested on, and the SuperWaba code may have version peculiarities)

	java Pickle <directory>/filename.with.dots.in

creates in the current directory a file filename_with_dots_in.pdb, containing
a PalmOS database called filename_with_dots_in which contains the input file
sawn up into 32k byte database records.

Install to your PalmOS device as usual.  When you've resync'd at another 
location, you get the file back by

	java Revive <directory>/filename.with.dots.in

which looks for filename_with_dots_in.pdb, containing a PalmOS database 
called filename_with_dots_in in the current directory.

Uses the SuperWaba library (www.superwaba.org) to do the heavy lifting.

Licensing
=========

These programs are free software; you can redistribute them and/or modify
them under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

They are distributed in the hope that they will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License (e.g. at
http://www.ravnaandtines.com/licences.txt) for more details.

SuperWaba is distributed from www.superwaba.org under the GNU Lesser 
(or Library) Public License (e.g. at http://www.ravnaandtines.com/copying_lib.txt)
