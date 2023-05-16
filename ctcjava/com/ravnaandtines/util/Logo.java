/**
 *  Class Logo
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1998
 *  All rights reserved.
 *
 *  This application is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This application is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this library (file "COPYING"); if not,
 *  write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Mr. Tines
 * @version 1.0 27-Jun-1998
 *
 */

package com.ravnaandtines.util;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Logo implements ImageProducer {
    static int width = 118;
    static int height = 118;
    static int[] col = {
        0xff000000, 0xffffffff        };
    static long[] image = {
        0xf5f5f5f5f5f5f5f5L, 0xf5f5f5f5f5f5f5f5L, 0xf5f5f5f5a307c9a1L,
        0x184018004c4a000L, 0x8606c59f008a04c4L, 0xa00085028103c4a1L,
        0x84008303c4a201L, 0x83038101c4a30383L, 0x8101c4a5058201L,
        0xc4a80184009706a5L, 0xa801840094058101L, 0xa5a8018400920280L,
        0x280008001a5a801L, 0x8400910280028001L, 0x8001a5a80184008fL,
        0x881008001a5a801L, 0x84008c0b8201a6a8L, 0x184008f01840182L,
        0x1a6a80184018e01L, 0x8504a7a80184008fL, 0x181018103a8a801L,
        0x84008f0082008202L, 0xa9a80184018e0082L, 0x8101aba8018400L,
        0x8e018302aca80184L, 0x18d018401aca801L, 0x84018d008501aca8L,
        0x184018c018500adL, 0xa70284018c018500L, 0xada70185018c0185L,
        0xada70185018c00L, 0x8501ada70185018bL, 0x18501ad89009c01L,
        0x85018b018501ad89L, 0x8307900185018bL, 0x18501ad88058003L,
        0x82008f0185018b00L, 0x8600ae880982018fL, 0x185018a018600aeL,
        0x8800820582009001L, 0x8400800189018600L, 0xae87018001800383L,
        0x8f028002820189L, 0x18600ae87018000L, 0x8004820090018001L,
        0x8100810188028600L, 0xa605818700810081L, 0x381019003810080L,
        0x800188018700a2L, 0x483008187008204L, 0x8001920380038100L,
        0x8801830181019e03L, 0x8001840081870083L, 0x593058000800080L,
        0x8801820380019dL, 0x180048301818700L, 0x8402950280008102L,
        0x8001870181048001L, 0x9b02810082008300L, 0x8287018301960280L,
        0x81028001860181L, 0x481019c01800182L, 0x82018287018301L,
        0x9503830281008601L, 0x810580019b018001L, 0x8201810183870183L,
        0x195038300800081L, 0x185018003800180L, 0x19a018600810184L,
        0x8701840095038201L, 0x8000810185018000L, 0x8000820080019901L,
        0x8101800080018101L, 0x8487018401940380L, 0x82018101850180L,
        0x183008001980181L, 0x180008001810185L, 0x8701840194038000L,
        0x8201810086048000L, 0x8100800296028101L, 0x8100810286870184L,
        0x194008001800082L, 0x81028505800080L, 0x80029502820788L,
        0x8701850094048003L, 0x8102850380008000L, 0x8000800393028401L,
        0x8d87028400930180L, 0x384028504810081L, 0x18101920284018eL,
        0x880184019201800bL, 0x8504810080008000L, 0x8102900185018f88L,
        0x185019101810180L, 0x281028502810280L, 0x282018e02860090L,
        0x8801850191008208L, 0x8000850080008000L, 0x8100800282018d02L,
        0x8600918802850091L, 0x83048001800085L, 0x68001800083018bL,
        0x187019189018501L, 0x8f00840180038100L, 0x8600800380038301L,
        0x8902880092890186L, 0x8f008401800381L, 0x86048004840088L,
        0x288019289018601L, 0x8e00850080018000L, 0x8100850280078401L,
        0x86018901938a0186L, 0x8d008600800180L, 0x8100850b840184L,
        0x28a00948a018700L, 0x8c00860080018000L, 0x8100850180018101L,
        0x8001850082028b00L, 0x958b0185028b0185L, 0x80018000810085L,
        0x180018100810185L, 0x78901958b018400L, 0x8200810985018001L,
        0x8000810085018001L, 0x8100810185088701L, 0x968b018300820380L,
        0x481008601800180L, 0x81008501800181L, 0x81028501830285L,
        0x1978c0182008301L, 0x8001810181008601L, 0x8000810081008500L,
        0x8100820080038501L, 0x800181018401988cL, 0x182008004800180L,
        0x281008502800081L, 0x81008500810082L, 0x80038505800282L,
        0x1998d0082008001L, 0x8001800680018400L, 0x8000800081008100L,
        0x8500810082008001L, 0x8000860481018101L, 0x9a8d018102820381L,
        0x683008000800081L, 0x180008500810082L, 0x80018001850181L,
        0x1800180019b8d01L, 0x8102820080018104L, 0x8002800180008000L,
        0x8101800085008001L, 0x8200800082008502L, 0x800180039c8e0180L,
        0x180008102800280L, 0x281038100800081L, 0x180008401800083L,
        0x80008200860480L, 0x29d8e0180018002L, 0x8008840280008000L,
        0x8200800084008100L, 0x8300800082008602L, 0x800080029d8e0280L,
        0x180038103800085L, 0x380008200800084L, 0x81008300800080L,
        0x68202800080019eL, 0x8f01800181018000L, 0x8002810087038200L,
        0x8000840081008209L, 0x8006800080019e8fL, 0x280018301800180L,
        0x2881a8203800181L, 0x80019e8e018004L, 0x8201800180008002L,
        0x8502830181018401L, 0x8203810180028001L, 0x800081009f8e0180L,
        0x181038101800082L, 0x283058201810183L, 0x182028200800482L,
        0x80019f8e008201L, 0x8101820284018201L, 0x8302810081018201L,
        0x8203810184018000L, 0x81019f8d01810383L, 0x386018101800280L,
        0x280018001820181L, 0x180018000800783L, 0x19f8c0181018306L,
        0x8701800180038001L, 0x8001800181018101L, 0x8101830380008100L,
        0x8001a08b01800284L, 0x81008002860280L, 0x81008000810380L,
        0x81018102810280L, 0x84018000800180L, 0x19e8b0080028400L,
        0x8106860680018005L, 0x8001820182028204L, 0x810280018006978aL,
        0x80028401800885L, 0x282008001800682L, 0x282058601800286L,
        0x692890385018001L, 0x8304830180018100L, 0x8001800080008501L,
        0x80048001801c9188L, 0x386008001880780L, 0x281058104800a81L,
        0x282008e03918401L, 0x8003860080018003L, 0x8203841082049e03L,
        0x9183068305820482L, 0x158502b683038000L, 0x84048404d78f0088L,
        0xdaf5f5f5f5f5f5L, 0xf5f5f5f5f5f5f5f5L        };


    Hashtable ix = new Hashtable(5);
    ColorModel cm;

    byte[] r = new byte[2];
    byte[] g = new byte[2];
    byte[] b = new byte[2];

    public Logo()
    {
        for(int i=0; i<2; ++i)
        {
            r[i] = (byte)((col[i]>>16) &0xFF);
            g[i] = (byte)((col[i]>> 8) &0xFF);
            b[i] = (byte)((col[i]    ) &0xFF);
        }
        cm = new IndexColorModel(3, 2, r, g, b);
    }

    public void addConsumer(ImageConsumer  ic)
    {
        ix.put(ic, ic);
    }


    public boolean isConsumer(ImageConsumer  ic)
    {
        return ix.contains(ic);
    }

    public void removeConsumer(ImageConsumer  ic)
    {
        ix.remove(ic);
    }

    public void startProduction(ImageConsumer  ic)
    {
        addConsumer(ic);
        Enumeration scan = ix.elements();
        while(scan.hasMoreElements())
        {
            requestTopDownLeftRightResend(
            (ImageConsumer) scan.nextElement()
                );
        }
    }

    public void requestTopDownLeftRightResend(ImageConsumer  ic)
    {
        long byteno = 56;
        int buffindex = 0;

        ic.setHints(
        ImageConsumer.TOPDOWNLEFTRIGHT |
            ImageConsumer.SINGLEFRAME |
            ImageConsumer.SINGLEPASS |
            ImageConsumer.COMPLETESCANLINES );
        ic.setColorModel(cm);
        ic.setDimensions(width, height);

        byte[] line = new byte[width];

        for(int y=0; y<height; ++y)
        {
            int x = 0;
            while(x < width)
            {
                byte b = (byte)((image[buffindex] >> byteno) & (0xFF));
                byteno -= 8;
                if(byteno < 0) {
                    byteno = 56; 
                    ++buffindex;
                }
                int run = (b&0x7F) + 1;
                for(int i=0; i<run && x<width; ++i, ++x)
                {
                    line[x] = (byte)(0x1 & (b>>7));
                }
            }
            ic.setPixels(0,y,width,1, cm, line, 0, width);
        }

        ic.imageComplete( ImageConsumer.STATICIMAGEDONE );
    }
}
