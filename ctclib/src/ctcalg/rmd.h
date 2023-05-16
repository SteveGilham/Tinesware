/* rmd.h - RIPE-MD hash functions
 * Copyright (C) 1998 Free Software Foundation, Inc.
 *
 * This file is part of GNUPG.
 *
 * GNUPG is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GNUPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 *
 * Modified for CTC-interface Mr. Tines, 28-Feb-1998
 */
#ifndef _rmd
#define _rmd

#include "basic.h"
#define RMD160HASHSIZE  20

typedef struct {
    uint32_t h0,h1,h2,h3,h4;
    uint32_t nblocks;
    byte buf[64];
    int count;
}
RMD160_CONTEXT;


void rmd160Init( RMD160_CONTEXT **c, size_t *length );
void rmd160Update( RMD160_CONTEXT *hd, byte *inbuf, uint32_t inlen);
void rmd160Final(RMD160_CONTEXT **hd,
byte digest[RMD160HASHSIZE], size_t length);

#endif /*_rmd */
