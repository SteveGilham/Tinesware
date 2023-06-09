 * XHTML Validator
 *
 * Public domain application implementation by Mr. Tines <tines@ravnaandtines.com>
 *
 * Version 1.1 (17-May-03)
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

New at 1.1
==========

There is a general confusion between ASCII (which only defines 128 
character values - a-z, A-Z, 0-9, English punctuation, values 0-31
being control characters); ISO-Latin-1 (or ISO-8859-1) which defines 
256 character values, including accented letters, some more symbols,
and a new control character range (values 128-143); and Microsoft's
cp1252, which is like ISO-Latin-1 but uses some of the 128-143 range 
for other symbols rather than as control characters.

These characters aren't valid SGML; so your page will fail at the 
definitive validator.w3.org test.  The new version of the validator
will now detect these characters, whether they are input directly as, 
for example a "�" for a bullet, or its equivalents &#x95; or &#149; 
and will in this case suggest you use the correct form for this glyph,
which is &bull; - or perhaps you might have � or &#x80; or &#128; rather
than &euro;

The accompanying file cp1252.html contains all these suspect characters
in rows of four - first as hex escapes (&#x... values), then the Unicode 
code point that the character is attempting to be, then the correct
escape sequence to use. 

Note that the Z-caron characters have no named version, unlike &Scaron; 
and &scaron;, and a hex escape needs to be used (&#x017d; and &#x017e;).
You can use the Unicode code point as an alternative to the name in 
the same way for all the others, but that will make the HTML more obscure.

Note that &#x81; &#x8d; &#x8f; &#x90; and &#x9d; don't have an 
associated character glyph so if any of them are encountered, the program
will suggest "?" as a placeholder.



Installation
============
This program requires the Microsoft .NET framework 1.0 to be installed for 
it to be used.  As a .NET program it doesn't need to do installer based
registry fiddling - just unzip and run.  Uninstalling is done by deletion.

Purpose
=======
This program is intended to do a final checking pass over XHTML documents.  You are 
probably best off starting by getting the document validating in at tool
such as Amaya (http://www.w3.org/Amaya/User/BinDist.html) before using this.

Use
===
Type a file name into the edit box or browse for one.  Select the XHTML version
to validate at, then hit validate (the button is disabled if the file can't
be read).

The program ignores any DOCTYPE declaration in your file and reads the DTD for
the XHTML version selected from the ./dtd folder.  The xhtml11.dtd is actually
a **modified** version of the formal http://www.w3.org/TR/xhtml11/DTD/xhtml11-flat.dtd
with the <html> tag xmlns attribute changed to Xmlns because the .NET code
dislikes attributes starting with x.  A similar change is made to the xmlns
attribute read from the file before passing to the validator, so a valid document
should still validate cleanly.

What next?
==========
Since you can validate to different standards without changing your DOCTYPE, you
can get the document validating at the standard you want, first then claim the 
DOCTYPE you want to deploy with.

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
      "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

For example if you have to include the "target" attribute for links because your
page will be shown in a <frame>, then you can check everything else validates
at 1.1 or 1.0 Strict, but publish with the 1.0 Transitional that allows the
"target" attribute.

The following XHTML fragments signal valid XHTML 1.0 and 1.1 and link to
the W3C validator, so a visitor can check the claims.

<p>
      <a href="http://validator.w3.org/check/referer"><img
          src="http://www.w3.org/Icons/valid-xhtml10"
          alt="Valid XHTML 1.0!" height="31" width="88" /></a>
    </p>
    
<p>
      <a href="http://validator.w3.org/check/referer"><img
          src="http://www.w3.org/Icons/valid-xhtml11"
          alt="Valid XHTML 1.1!" height="31" width="88" /></a>
    </p>

Alas, my web hosting appends invalid junk to the page I put up to include 
advertising, so the page as seen by the public is not valid.  *sigh*
