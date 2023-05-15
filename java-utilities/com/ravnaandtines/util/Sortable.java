package com.ravnaandtines.util;

/**
*  Class Sortable - interface for use with expanded QSort class; to
* sort an array or Vector of Sortables use QSort.sortables on the array
* or vector
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

public interface Sortable
{
    /**
    * Compares two Sortables. The comparison is as desired by the
    * implementor
    * @param b the Sortable to be compared.  A robust implementation will
    * allow for null input and heterogeneous sortables (e.g. by making null
    * come before anythis else, and sorting by class name if b.class != this.class);
    * but in most cases the input will be homogeneous by construction.
    * @return the value 0 if the argument is equal to this object; a value less
    * than 0 if this object falls before the argument; and a value greater than
    * 0 if this object falls after the string.
    */
    public abstract int compareTo(Sortable b);
} 