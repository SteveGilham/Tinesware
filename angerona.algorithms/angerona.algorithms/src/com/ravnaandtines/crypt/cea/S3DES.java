/*
 * A DES variant -- no test vectors to hand, though.
 * TODO triple
 */
 /* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */

package com.ravnaandtines.crypt.cea;

/**
 *
 * @author Steve
 */
public final class S3DES extends DESprototype  {
    public S3DES() {  
        super();
        spBoxes = new DES_SPboxes(23);
    }
}
