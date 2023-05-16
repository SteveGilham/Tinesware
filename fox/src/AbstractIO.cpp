/***************************************************************************
                               AbstractIO.cpp
                             -------------------
    copyright            : (C) 2002 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

/*
* Equivalent interface to CTClib's DataFileP object
*/
#include "AbstractIO.h"

// nugatory definitions

namespace CTCFox {

AbstractIOBase::AbstractIOBase () {}
AbstractIOBase::~AbstractIOBase() {}

AbstractBinaryIO::AbstractBinaryIO () {}
AbstractBinaryIO::~AbstractBinaryIO() {}

AbstractTextIO::AbstractTextIO () {}
AbstractTextIO::~AbstractTextIO() {}

AbstractIO::AbstractIO () {}
AbstractIO::~AbstractIO() {}

}
