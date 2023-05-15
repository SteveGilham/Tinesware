
xpp by Mr. Tines <tines@ravnaandtines.com>
=================================================

What is it?
-----------

An X(HT)ML pre-processor for static sites, with semantics rather like "copy"

Usage: xpp "directives" in-filename out-location

Example: "+local -remote include *replace" ..\..\New.html ..\..\bin

out-location may be a file or a directory.  If the latter, the file-name 
of the in-filename is used to create the new file.  The directives indicate
which parts of the file to change.

The in-file has to be valid XML; and in most cases it can be valid XHTML.
Each element tag is inspected for a "class" attribute.

1) If/else sections of the file

If the class value is one of the strings marked with a '+' in the directive, 
the attribute is just removed.

If the class value is one of the strings marked with a '-' in the directive, 
the whole element and all it contains is removed.

2) Including text

These are more complicated.  Class attributes are of the form 

directive facet1 facet2 ... facetN

where facet1/facet2/.../facetN is the filespec (typically a relative one)
from in-filename to the file to include.

If the directive matches a *-ed value, replace the element with the 
contents of the file; if the match is with an un-tagged name, replace
the contents of the element with the contents of the file.

If the included file isn't a valid XML fragment, then the process will fail.

Note that the evaluation carries on through the included XML, recursively,
so an included fragment can have If/else sections

What isn't it?
--------------

Exhaustively cautious.  If something goes wrong, it will throw.  This 
will tell you that your XML is invalid.

Example
=======

<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
       "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>No title</title>
  <meta class="replace .. xpp header.ihtml" />
</head>

<body>
<p class="local">Local&nbsp;text</p>

<p class="remote">Remote text</p>

<p class="display">styled text</p>

<p class="include .. xpp other.ihtml">Insertion point</p>
</body>
</html>


When processed as above, will become

<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>No title</title>
    <meta name="keywords" content="Java Freeware XHTML CSS validator FTP GWT" />
  <meta name="description" content="Java Freeware" />
  <meta name="author" content="Mr. Tines" />
  <meta name="copyright" content="Material Copyright &copy; 2000-2003 Mr. Tines" />
  <meta name="robots" content="all" />
  <meta name="language" content="en" />

</head>

<body>
<p>Local&nbsp;text</p>



<p class="display">styled text</p>

<p>Test <span>local text to</span> insert</p>
</body>
</html>

if header.ihtml contains

 <meta name="keywords" content="Java Freeware XHTML CSS validator FTP GWT" />
  <meta name="description" content="Java Freeware" />
  <meta name="author" content="Mr. Tines" />
  <meta name="copyright" content="Material Copyright &copy; 2000-2003 Mr. Tines" />
  <meta name="robots" content="all" />
  <meta name="language" content="en" />

and other.ihtml contains

Test <span class="local">local text to</span><span class="remote">remote text to</span> insert


What to use for?
================

Factor out common bits of headers, repeated modules like page footers, and make
the bits visible by applying CSS styles to the affected tags (doesn't work
so well on <head /> tags)


NO WARRANTY
===========

BECAUSE THE PROGRAM IS LICENSED FREE OF CHARGE, THERE IS NO WARRANTY FOR
THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES
PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED
OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM
PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR
CORRECTION.

IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING
WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MAY MODIFY AND/OR
REDISTRIBUTE THE PROGRAM AS PERMITTED BY LICENSE, BE LIABLE TO YOU FOR DAMAGES,
INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING
OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO
LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR
THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER
PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE
POSSIBILITY OF SUCH DAMAGES.