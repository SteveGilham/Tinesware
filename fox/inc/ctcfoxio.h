/* ctcfoxio.h : Defines the I/O abstractions for the application.
   Copyright 2002 Mr. Tines <Tines@RavnaAndTines.com>

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU Library General Public License as published by the
Free Software Foundation; either version 2, or (at your option) any
later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.  */

#ifndef ctcfoxio_h
#define ctcfoxio_h

#include "abstract.h"

namespace FX {
class FXString;
class FXText;
}
using FX::FXString;

namespace CTCFox {

    class AbstractIO;
    using CTClib::DataFileP;

    DataFileP createDataFile(AbstractIO * client, bool deleteOnDestruction = true);
    AbstractIO * openRing(const char * ringname, bool readonly = true);
    AbstractIO * openTemp(long expected_size);
    AbstractIO * openFXText(FX::FXText * text); // write only

    void copyBFile(DataFileP from, DataFileP to);
    void getBackupName(FXString &, FXString &);

} // namespace

#endif
