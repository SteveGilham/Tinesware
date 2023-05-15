/*
 * Biham's Key-dependent DES variant -- no test vectors, though.
 * TODO triple
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */


package com.ravnaandtines.crypt.cea;

public final class KDDES extends DESprototype  {
    public KDDES() {  //NOPMD
        super();
    }
    public void init(final byte[] key, final int offset, final boolean triple) {
        spBoxes = new DES_SPboxes(key, offset);
        super.init(key, offset, triple);
    }    
}