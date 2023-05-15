#if 0
/* metal.cpp : Skin theme class and helpers
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

#include "metal.h"
#ifdef _MSC_VER
#pragma warning (disable : 4244) // fox workround
#endif
#include "plaf.hpp"
#include <fx.h>

namespace FoxSkin {

    Metal::Metal() : Plaf<Metal>()
    {
    }

    Metal::~Metal()
    {
    }

    void Metal::initFonts()
    {
        if(!controlFont)
        {
            FX::FXFont * appNormal = FX::FXApp::instance()->getNormalFont();
            FX::FXFontDesc std;
            appNormal->getFontDesc(std);

            int base = std.size;
#ifdef WIN32
            base = 10;
#endif
            std.size = 6*base/5;
            std.weight = FONTWEIGHT_BOLD;
            controlFont = new FX::FXFont(FX::FXApp::instance(), std);
            controlFont->create();

            std.weight = FONTWEIGHT_NORMAL;
            systemFont = new FX::FXFont(FX::FXApp::instance(), std);
            systemFont->create();
            userFont = new FX::FXFont(FX::FXApp::instance(), std);
            userFont->create();

            std.size = base;
            smallFont = new FX::FXFont(FX::FXApp::instance(), std);
            smallFont->create();
        }
    }


    // standard colours
    FXColor Metal::primary1 = FXRGB(102,102,153);
    FXColor Metal::primary2 = FXRGB(153,153,204);
    FXColor Metal::primary3 = FXRGB(204,204,255);

    FXColor Metal::secondary1 = FXRGB(102,102,102);
    FXColor Metal::secondary2 = FXRGB(153,153,153);
    FXColor Metal::secondary3 = FXRGB(204,204,204);

    FXColor Metal::black = FXRGB(0,0,0);
    FXColor Metal::white = FXRGB(255,255,255);

    FXFont * Metal::controlFont = NULL;
    FXFont * Metal::smallFont = NULL;
    FXFont * Metal::systemFont = NULL;
    FXFont * Metal::userFont = NULL;

    // Helper functions====================================================

    // Get height of multi-line label
    static FXint labelHeight(const FXString& text, FXFont * font)
    {
        register FXuint beg,end;
        register FXint th=0;
        beg=0;
        do{
            end=beg;
            while(text[end] && text[end]!='\n') end++;
            th+=font->getFontHeight();
            beg=end+1;
            }
        while(text[end]);
        return th;
    }


    // Get width of multi-line label
    static FXint labelWidth(const FXString& text, FXFont * font)
    {
        register FXuint beg,end;
        register FXint w,tw=0;
        beg=0;
        do{
            end=beg;
            while(text[end] && text[end]!='\n') end++;
            if((w=font->getTextWidth(&text[beg],end-beg))>tw) tw=w;
            beg=end+1;
            }
        while(text[end]);
        return tw;
    }

    static void drawBorderRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,FX::FXColor & borderColor)
    {
        dc.setForeground(borderColor);
        dc.drawRectangle(x,y,w-1,h-1);
    }


    static void drawRaisedRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & hiliteColor)
    {
        dc.setForeground(shadowColor);
        dc.fillRectangle(x,y+h-1,w,1);
        dc.fillRectangle(x+w-1,y,1,h);
        dc.setForeground(hiliteColor);
        dc.fillRectangle(x,y,w,1);
        dc.fillRectangle(x,y,1,h);
    }

    static void drawSunkenRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & hiliteColor)
    {
        dc.setForeground(shadowColor);
        dc.fillRectangle(x,y,w,1);
        dc.fillRectangle(x,y,1,h);
        dc.setForeground(hiliteColor);
        dc.fillRectangle(x,y+h-1,w,1);
        dc.fillRectangle(x+w-1,y,1,h);
    }


    static void drawRidgeRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & hiliteColor)
    {
        dc.setForeground(hiliteColor);
        dc.fillRectangle(x,y,w,1);
        dc.fillRectangle(x,y,1,h);
        dc.fillRectangle(x+1,y+h-2,w-2,1);
        dc.fillRectangle(x+w-2,y+1,1,h-2);
        dc.setForeground(shadowColor);
        dc.fillRectangle(x+1,y+1,w-3,1);
        dc.fillRectangle(x+1,y+1,1,h-3);
        dc.fillRectangle(x,y+h-1,w,1);
        dc.fillRectangle(x+w-1,y,1,h);
    }


    static void drawGrooveRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & hiliteColor)
    {
        dc.setForeground(shadowColor);
        dc.fillRectangle(x,y,w,1);
        dc.fillRectangle(x,y,1,h);
        dc.fillRectangle(x+1,y+h-2,w-2,1);
        dc.fillRectangle(x+w-2,y+1,1,h-2);
        dc.setForeground(hiliteColor);
        dc.fillRectangle(x+1,y+1,w-3,1);
        dc.fillRectangle(x+1,y+1,1,h-3);
        dc.fillRectangle(x,y+h-1,w,1);
        dc.fillRectangle(x+w-1,y,1,h);
    }


    static void drawDoubleRaisedRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & borderColor, FX::FXColor & hiliteColor, FX::FXColor & baseColor)
    {
    dc.setForeground(hiliteColor);
    dc.fillRectangle(x,y,w-1,1);
    dc.fillRectangle(x,y,1,h-1);
    dc.setForeground(baseColor);
    dc.fillRectangle(x+1,y+1,w-2,1);
    dc.fillRectangle(x+1,y+1,1,h-2);
    dc.setForeground(shadowColor);
    dc.fillRectangle(x+1,y+h-2,w-2,1);
    dc.fillRectangle(x+w-2,y+1,1,h-1);
    dc.setForeground(borderColor);
    dc.fillRectangle(x,y+h-1,w,1);
    dc.fillRectangle(x+w-1,y,1,h);
    }

    static void drawDoubleSunkenRectangle(FXDCWindow& dc,FXint x,FXint y,FXint w,FXint h,
        FX::FXColor & shadowColor, FX::FXColor & borderColor, FX::FXColor & hiliteColor, FX::FXColor & baseColor)
    {
        dc.setForeground(shadowColor);
        dc.fillRectangle(x,y,w-1,1);
        dc.fillRectangle(x,y,1,h-1);
        dc.setForeground(borderColor);
        dc.fillRectangle(x+1,y+1,w-3,1);
        dc.fillRectangle(x+1,y+1,1,h-3);
        dc.setForeground(hiliteColor);
        dc.fillRectangle(x,y+h-1,w,1);
        dc.fillRectangle(x+w-1,y,1,h);
        dc.setForeground(baseColor);
        dc.fillRectangle(x+1,y+h-2,w-2,1);
        dc.fillRectangle(x+w-2,y+1,1,h-2);
    }

    void Metal::drawFrame(FXDCWindow & dc, int x, int y, int w, int h, int options)
    {
#define FRAME_MASK        (FRAME_SUNKEN|FRAME_RAISED|FRAME_THICK)
        switch(options&FRAME_MASK) 
        {
            case FRAME_LINE: /*drawBorderRectangle(dc,x,y,w,h,secondary2); break;*/
            case FRAME_SUNKEN: /*drawSunkenRectangle(dc,x,y,w,h,secondary1,white); break;*/
            case FRAME_RAISED: /*drawRaisedRectangle(dc,x,y,w,h,secondary1,white); break;*/
            case FRAME_RIDGE: /*drawRidgeRectangle(dc,x,y,w,h,secondary1,white); break;*/
            case FRAME_SUNKEN|FRAME_THICK: /*drawDoubleSunkenRectangle(dc,x,y,w,h,
                                               secondary1,secondary2,white,secondary3); break;*/
            case FRAME_RAISED|FRAME_THICK: /*drawDoubleRaisedRectangle(dc,x,y,w,h,
                                               secondary1,secondary2,white,secondary3); break;*/
            case FRAME_GROOVE: drawGrooveRectangle(dc,x,y,w,h,secondary1,white); break;
        }
    }

    // Draw text fragment
    void Metal::drawTextFragment(FXDCWindow& dc,FXint x,FXint y,FXint fm,FXint to,FXString & contents, FXFont * font)
    {
        x+=font->getTextWidth(contents.text(),fm);
        y+=font->getFontAscent();
        dc.drawText(x,y,&contents[fm],to-fm);
    }


    // Draw text fragment in password mode
    void Metal::drawPWDTextFragment(FXDCWindow& dc,FXint x,FXint y,FXint fm,FXint to, FXFont * font)
    {
        register FXint cw=font->getTextWidth("*",1);
        register FXint i;
        y+=font->getFontAscent();
        for(i=fm; i<to; i++){dc.drawText(x+cw*i,y,"*",1);}
    }

    // Draw range of text
    void Metal::drawText(FXDCWindow& dc, Skin<FX::FXTextField,Metal> * tgt)
    {
   		FXint width=tgt->getWidth(),height=tgt->getHeight(),
            cursor=tgt->getCursorPos(), anchor = tgt->getAnchorPos()
            ,padleft = tgt->getPadLeft()
            ,padright = tgt->getPadRight()
            ,padtop = tgt->getPadTop()
            ,padbottom = tgt->getPadBottom()
            ;
        int options = getOptions<Skin<FX::FXTextField,Metal> >(tgt)
            ,shift = getShift<Skin<FX::FXTextField,Metal> >(tgt)
            ;
        int border = tgt->getBorderWidth();

        FXString contents = tgt->getText();
        // hard code these
        FXint fm = 0;
        FXint to = contents.length();

        FXint sx,ex,xx,yy,cw,hh,ww; FXint si,ei;
        if(to<=fm) return;
        FX::FXFont * font = userFont;

        dc.setTextFont(font);

        // Text color
        if(tgt->isEnabled())
            dc.setForeground(black);
        else
            dc.setForeground(secondary2);

        // Height
        hh=font->getFontHeight();

        
        if((options&JUSTIFY_TOP) && (options&JUSTIFY_BOTTOM))// Text centered in y
        {
            yy=border+padtop+(height-padbottom-padtop-(border<<1)-hh)/2;
        }
        else if(options&JUSTIFY_TOP)// Text sticks to top of field
        {
            yy=padtop+border;
        }
        else if(options&JUSTIFY_BOTTOM)// Text sticks to bottom of field
        {
            yy=height-padbottom-border-hh;
        }
        else// Text centered in y
        {
            yy=border+padtop+(height-padbottom-padtop-(border<<1)-hh)/2;
        }

        if(anchor<cursor)
            {si=anchor;ei=cursor;}
        else
            {si=cursor;ei=anchor;}

        // Password mode
        if(options&TEXTFIELD_PASSWD)
        {
            cw=font->getTextWidth("*",1);
            ww=cw*contents.length();
            if(options&JUSTIFY_RIGHT)// Text sticks to right of field
            {
                xx=shift+width-border-padright-ww;
            }
            else// Text on left is the default
            {
                xx=shift+border+padleft;
            }

            // Nothing selected
            if(!tgt->hasSelection() || to<=si || ei<=fm)
            {
                drawPWDTextFragment(dc,xx,yy,fm,to,font);
            }
            else// Stuff selected
            {
                if(fm<si)
                {
                    drawPWDTextFragment(dc,xx,yy,fm,si,font);
                }
                else
                {
                    si=fm;
                }
                if(ei<to)
                {
                    drawPWDTextFragment(dc,xx,yy,ei,to,font);
                }
                else
                {
                    ei=to;
                }
                if(si<ei)
                {
                    sx=xx+cw*si;
                    ex=xx+cw*ei;
                    if(tgt->hasFocus())
                    {
                        dc.setForeground(primary3);
                        dc.fillRectangle(sx,padtop+border,ex-sx,height-padtop-padbottom-(border<<1));
                        dc.setForeground(black);
                        drawPWDTextFragment(dc,xx,yy,si,ei,font);
                    }
                    else
                    {
                        dc.setForeground(secondary3);
                        dc.fillRectangle(sx,padtop+border,ex-sx,height-padtop-padbottom-(border<<1));
                        dc.setForeground(black);
                        drawPWDTextFragment(dc,xx,yy,si,ei,font);
                    }
                }
            }
        }
        else// Normal mode
        {
            ww=font->getTextWidth(contents.text(),contents.length());
            if(options&JUSTIFY_RIGHT)// Text sticks to right of field
            {
                xx=shift+width-border-padright-ww;
            }
            else// Text on left is the default
            {
                xx=shift+border+padleft;
            }

            // Nothing selected
            if(!tgt->hasSelection() || to<=si || ei<=fm)
            {
                drawTextFragment(dc,xx,yy,fm,to,contents,font);
            }
            else// Stuff selected
            {
                if(fm<si)
                {
                    drawTextFragment(dc,xx,yy,fm,si,contents,font);
                }
                else
                {
                    si=fm;
                }
                if(ei<to)
                {
                    drawTextFragment(dc,xx,yy,ei,to,contents,font);
                }
                else
                {
                    ei=to;
                }
                if(si<ei)
                {
                    sx=xx+font->getTextWidth(contents.text(),si);
                    ex=xx+font->getTextWidth(contents.text(),ei);
                    if(tgt->hasFocus())
                    {
                        dc.setForeground(primary3);
                        dc.fillRectangle(sx,padtop+border,ex-sx,height-padtop-padbottom-(border<<1));
                        dc.setForeground(black);
                        drawTextFragment(dc,xx,yy,si,ei,contents,font);
                    }
                    else
                    {
                        dc.setForeground(secondary3);
                        dc.fillRectangle(sx,padtop+border,ex-sx,height-padtop-padbottom-(border<<1));
                        dc.setForeground(black);
                        drawTextFragment(dc,xx,yy,si,ei,contents,font);
                    }
                }
            }
        }
    }

    // Draw item
    void Metal::drawFXHeaderItem(FX::FXHeaderItem * item, const FX::FXHeader* header,FX::FXDC& dc,FXint x,FXint y,FXint w,FXint h)
    {
#define ARROW_SPACING 8
#define ICON_SPACING  4
        FXFont *font=controlFont;

        // Clip to inside of header control
        dc.setClipRectangle(x,y,w,h);

        // Account for borders
        w=w-(header->getPadLeft()+header->getPadRight()+(header->getBorderWidth()<<1));
        h=h-(header->getPadTop()+header->getPadBottom()+(header->getBorderWidth()<<1));
        x+=header->getBorderWidth()+header->getPadLeft();
        y+=header->getBorderWidth()+header->getPadTop();

        // Draw icon
        FXIcon * icon = item->getIcon();
        if(icon)
        {
            if(icon->getWidth()<=w)
            {
                dc.drawIcon(icon,x,y+(h-icon->getHeight())/2);
                x+=icon->getWidth();
                w-=icon->getWidth();
            }
        }

        // Draw text
        FXString label = item->getText();
        if(!label.empty())
        {
            FXint dw=font->getTextWidth("...",3);
            FXint num=label.length();
            FXint tw=font->getTextWidth(label.text(),num);
            FXint th=font->getFontHeight();
            FXint ty=y+(h-th)/2+font->getFontAscent();
            dc.setTextFont(font);
            if(icon){ x+=ICON_SPACING; w-=ICON_SPACING; }
            dc.setForeground(primary1);
            if(tw<=w)
            {
                dc.drawText(x,ty,label.text(),num);
                x+=tw;
                w-=tw;
            }else{
                while(num>0 && (tw=font->getTextWidth(label.text(),num))>(w-dw)) num--;
                if(num>0)
                {
                    dc.setForeground(primary1);
                    dc.drawText(x,ty,label.text(),num);
                    dc.drawText(x+tw,ty,"...",3);
                    x+=tw+dw;
                    w-=tw+dw;
                }else{
                    tw=font->getTextWidth(label.text(),1);
                    if(tw<=w)
                    {
                        dc.drawText(x,ty,label.text(),1);
                        x+=tw;
                        w-=tw;
                    }
                }
            }
        }

        // Draw arrows
        FXuint arrow = item->getArrowDir();
        if(arrow!=MAYBE)
        {
            FXint aa=(font->getFontHeight()-3)|1;
            if(icon || !label.empty()){ x+=ARROW_SPACING; w-=ARROW_SPACING; }
            if(w>aa)
            {
                if(arrow==TRUE)
                {
                    y=y+(h-aa)/2;
                    dc.setForeground(white);
                    dc.drawLine(x+aa/2,y,x+aa-1,y+aa);
                    dc.drawLine(x,y+aa,x+aa,y+aa);
                    dc.setForeground(secondary1);
                    dc.drawLine(x+aa/2,y,x,y+aa);
                }else{
                    y=y+(h-aa)/2;
                    dc.setForeground(white);
                    dc.drawLine(x+aa/2,y+aa,x+aa-1,y);
                    dc.setForeground(secondary1);
                    dc.drawLine(x+aa/2,y+aa,x,y);
                    dc.drawLine(x,y,x+aa,y);
                }
            }
        }

        // Restore original clip path
        dc.clearClipRectangle();
    }

    void Metal::drawThumb(FXDCWindow & dc, int x, int y, int w, int h, bool horizontal)
    {
        dc.setForeground(primary2);
        if(horizontal)
            dc.fillRectangle(x,y+1,w,h-2);
        else
            dc.fillRectangle(x+1,y,w-2,h);

        dc.setForeground(primary3);
        if(horizontal)
        {
            dc.drawLine(x,y+1,x,y+h-1);
            dc.drawLine(x,y+1,x+w-1,y+1);
        }
        else
        {
            dc.drawLine(x+1,y,x+w-1,y);
            dc.drawLine(x+1,y,x+1,y+h-1);
        }

        int tx, ty;
        for(tx=0; tx<w-6; tx+=2)
        {
            for(ty=0; ty<h-6; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                dc.fillRectangle(x+tx+3,y+ty+3,1,1);
            }
        }

        dc.setForeground(primary1);
#ifdef WIN32
        int delta = 1;
#else
        int delta = 2;
#endif
        if(horizontal)
            dc.drawLine(x+w-1,y+1,x+w-1,y+h-delta);
        else
            dc.drawLine(x+1,y+h-1,x+w-delta,y+h-1);

        for(tx=0; tx<w-6; tx+=2)
        {
            for(ty=0; ty<h-6; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                dc.fillRectangle(x+tx+4,y+ty+4,1,1);
            }
        }

    }

    void Metal::drawTrough(FXDCWindow & dc, int x, int y, int w, int h, const FX::FXColor & fill, bool horizontal)
    {
        dc.setForeground(fill);
        if(horizontal)
            dc.fillRectangle(x,y+2,w,h-4);
        else
            dc.fillRectangle(x+2,y,w-4,h);

        dc.setForeground(white);
        if(horizontal)
        {
            dc.drawLine(x,y+1,x+w,y+1);
            dc.drawLine(x,y+h-1,x+w,y+h-1);
        }
        else
        {
            dc.drawLine(x+1,y,x+1,y+h);
            dc.drawLine(x+w-1,y,x+w-1,y+h);
        }

        dc.setForeground(secondary1);
        if(horizontal)
        {
            dc.drawLine(x,y,x+w,y);
            dc.drawLine(x,y+h-2,x+w,y+h-2);
        }
        else
        {
            dc.drawLine(x,y,x,y+h);
            dc.drawLine(x+w-2,y+h,x+w-2,y);
        }
    }

    // Draw check mark
    void Metal::drawCheck(FXDCWindow& dc,FXint x,FXint y)
    {
        FXSegment seg[6];
        seg[0].x1=1+x; seg[0].y1=3+y; seg[0].x2=3+x; seg[0].y2=5+y;
        seg[1].x1=1+x; seg[1].y1=4+y; seg[1].x2=3+x; seg[1].y2=6+y;
        seg[2].x1=1+x; seg[2].y1=5+y; seg[2].x2=3+x; seg[2].y2=7+y;
        seg[3].x1=3+x; seg[3].y1=5+y; seg[3].x2=7+x; seg[3].y2=1+y;
        seg[4].x1=3+x; seg[4].y1=6+y; seg[4].x2=7+x; seg[4].y2=2+y;
        seg[5].x1=3+x; seg[5].y1=7+y; seg[5].x2=7+x; seg[5].y2=3+y;
        dc.drawLineSegments(seg,6);
    }


    // Draw bullet
    void Metal::drawBullet(FXDCWindow& dc,FXint x,FXint y)
    {
        FXSegment seg[5];
        seg[0].x1= 1+x; seg[0].y1 = 0+y; seg[0].x2 = 3+x; seg[0].y2 = 0+y;
        seg[1].x1= 0+x; seg[1].y1 = 1+y; seg[1].x2 = 4+x; seg[1].y2 = 1+y;
        seg[2].x1= 0+x; seg[2].y1 = 2+y; seg[2].x2 = 4+x; seg[2].y2 = 2+y;
        seg[3].x1= 0+x; seg[3].y1 = 3+y; seg[3].x2 = 4+x; seg[3].y2 = 3+y;
        seg[4].x1= 1+x; seg[4].y1 = 4+y; seg[4].x2 = 3+x; seg[4].y2 = 4+y;
        dc.drawLineSegments(seg,5);
    }

    void Metal::drawTriangle(FXDCWindow& dc,FXint l,FXint t,FXint r,FXint b)
    {
        FXPoint points[3];
        int m=(t+b)/2;
        points[0].x=l;
        points[0].y=t;
        points[1].x=l;
        points[1].y=b;
        points[2].x=r;
        points[2].y=m;
        dc.fillPolygon(points,3);
    }


    // Specializations for drawing====================================================

#define TEMPLATE(type) template <> bool Metal::onSkin<FoxSkin::Skin<type, FoxSkin::Metal> >(FoxSkin::Skin<type, FoxSkin::Metal> * tgt, FX::FXEvent * ev)

    TEMPLATE(FX::FXMenuCascade)
    {
#define LEADSPACE   22
#define TRAILSPACE  16
        FXDCWindow dc(tgt,ev);
        FXint width=tgt->getWidth(),height=tgt->getHeight()
            ;
        FXIcon * icon = tgt->getIcon();
        FXString label = tgt->getText();
        FXFont * font = controlFont;
        int hotoff = getHotoff<Skin<FX::FXMenuCascade,Metal> >(tgt);

        FXint xx,yy;

        xx=LEADSPACE;
        dc.setForeground(secondary3);
        dc.fillRectangle(0,0,width,height);

        // Grayed out
        if(!tgt->isEnabled())
        {
            if(icon)
            {
                dc.drawIconSunken(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.setForeground(secondary2);
                dc.drawText(xx,yy,label.text(),label.length());
                if(0<=hotoff)
                {
                    dc.fillRectangle(xx+1+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            yy=(height-8)/2;
            dc.setForeground(secondary2);
            drawTriangle(dc,width-TRAILSPACE+4,yy,width-TRAILSPACE+4+6,yy+8);
        }else if(tgt->isActive()){// Active  
            dc.setForeground(primary2);
            dc.fillRectangle(1,1,width-2,height-2);
            if(icon)
            {
                dc.drawIcon(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.setForeground(tgt->isEnabled() ? black : secondary2);
                dc.drawText(xx,yy,label.text(),label.length());
                if(0<=hotoff)
                {
                    dc.fillRectangle(xx+1+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            yy=(height-8)/2;
            dc.setForeground(black);
            drawTriangle(dc,width-TRAILSPACE+4,yy,width-TRAILSPACE+4+6,yy+8);
        }else{// Normal
            if(icon)
            {
                dc.drawIcon(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.setForeground(black);
                dc.drawText(xx,yy,label.text(),label.length());
                if(0<=hotoff)
                {
                    dc.fillRectangle(xx+1+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            yy=(height-8)/2;
            dc.setForeground(black);
            drawTriangle(dc,width-TRAILSPACE+4,yy,width-TRAILSPACE+4+6,yy+8);
        }
        return true;
    }

    TEMPLATE(FX::FXProgressBar)
    {
#define TAB_ORIENT_MASK    (TAB_TOP|TAB_LEFT|TAB_RIGHT|TAB_BOTTOM)
        FXDCWindow dc(tgt,ev);
        FXint width=tgt->getWidth(),height=tgt->getHeight(), border=tgt->getBorderWidth()
            ,padleft = tgt->getPadLeft()
            ,padright = tgt->getPadRight()
            ,padtop = tgt->getPadTop()
            ,padbottom = tgt->getPadBottom()
            ;
        int options = getOptions<Skin<FX::FXProgressBar,Metal> >(tgt);
        // Draw borders if any
        drawFrame(dc,0,0,width,height, options);
  
        // Background
        dc.setForeground(secondary3);
        dc.fillRectangle(border,border,width-(border<<1),height-(border<<1));

        FXint percent,barlength,barfilled,tx,ty,tw,th,n,d;
        FXchar numtext[5];
        FXuint total = tgt->getTotal();
        FXuint progress = tgt->getProgress();
        FXFont * font = controlFont;

        if(options&PROGRESSBAR_DIAL)
        {
            // If total is 0, it's 100%
            barfilled=23040;
            percent=100;
            if(total!=0)
            {
                barfilled=(FXuint) (((double)progress * (double)23040) / (double)total);
                percent=(FXuint) (((double)progress * 100.0) / (double)total);
            }

            tw=width-(border<<1)-padleft-padright;
            th=height-(border<<1)-padtop-padbottom;
            d=FXMIN(tw,th)-1;

            tx=border+padleft+((tw-d)/2);
            ty=border+padtop+((th-d)/2);

            if(barfilled!=23040)
            {
                dc.setForeground(secondary3);
                dc.fillArc(tx,ty,d,d,5760,23040-barfilled);
            }
            if(barfilled!=0)
            {
                dc.setForeground(primary2);
                dc.fillArc(tx,ty,d,d,5760,-barfilled);
            }

            // Draw outside circle
            dc.setForeground(secondary2);
            dc.drawArc(tx+1,ty,d,d,90*64,45*64);
            dc.drawArc(tx,ty+1,d,d,135*64,45*64);
            dc.setForeground(secondary3);
            dc.drawArc(tx-1,ty,d,d,270*64,45*64);
            dc.drawArc(tx,ty-1,d,d,315*64,45*64);

            dc.setForeground(secondary1);
            dc.drawArc(tx,ty,d,d,45*64,180*64);
            dc.setForeground(white);
            dc.drawArc(tx,ty,d,d,225*64,180*64);

            // Draw text
            if(options&PROGRESSBAR_PERCENTAGE)
            {
                tw=font->getTextWidth("100%",4);
                if(tw>(11*d)/16) return true;
                th=font->getFontHeight();
                if(th>d/2) return true;
                sprintf(numtext,"%d%%",percent);
                n=(FX::FXint)strlen(numtext);
                tw=font->getTextWidth(numtext,n-1);       // Not including the % looks better
                th=font->getFontHeight();
                tx=tx+d/2-tw/2;
                ty=ty+d/2+font->getFontAscent()+5;
                //dc.setForeground(textNumColor);
                //dc.setForeground(FXRGB(255,255,255));
                dc.setForeground(black);
                dc.setFunction(BLT_SRC_XOR_DST);      // FIXME
                dc.drawText(tx,ty,numtext,n);
            }
        }else if(options&PROGRESSBAR_VERTICAL){// Vertical bar
            // If total is 0, it's 100%
            barlength=height-border-border;
            barfilled=barlength;
            percent=100;
            if(total!=0)
            {
                barfilled=(FXuint) (((double)progress * (double)barlength) / (double)total);
                percent=(FXuint) (((double)progress * 100.0) / (double)total);
            }

            // Draw completed bar
            if(0<barfilled)
            {
                dc.setForeground(primary2);
                dc.fillRectangle(border,height-border-barfilled,width-(border<<1),barfilled);
            }

            // Draw uncompleted bar
            if(barfilled<barlength)
            {
                dc.setForeground(secondary3);
                dc.fillRectangle(border,border,width-(border<<1),barlength-barfilled);
            }

            // Draw text
            if(options&PROGRESSBAR_PERCENTAGE)
            {
                sprintf(numtext,"%d%%",percent);
                n=(FX::FXint)strlen(numtext);
                tw=font->getTextWidth(numtext,n);
                th=font->getFontHeight();
                ty=(height-th)/2+font->getFontAscent();
                tx=(width-tw)/2;
                if(height-border-barfilled>ty)
                {           // In upper side
                    dc.setForeground(primary1);
                    dc.drawText(tx,ty,numtext,n);
                }
                else if(ty-th>height-border-barfilled)
                {   // In lower side
                    dc.setForeground(secondary1);
                    dc.drawText(tx,ty,numtext,n);
                }else{                                     // In between!
                    dc.setForeground(secondary1);
                    dc.setClipRectangle(border,height-border-barfilled,width-(border<<1),barfilled);
                    dc.drawText(tx,ty,numtext,n);
                    dc.setForeground(primary1);
                    dc.setClipRectangle(border,border,width-(border<<1),barlength-barfilled);
                    dc.drawText(tx,ty,numtext,n);
                    dc.clearClipRectangle();
                }
            }
        }else{// Horizontal bar
            // If total is 0, it's 100%
            barlength=width-border-border;
            barfilled=barlength;
            percent=100;
            if(total!=0)
            {
                barfilled=(FXuint) (((double)progress * (double)barlength) / (double)total);
                percent=(FXuint) (((double)progress * 100.0) / (double)total);
            }

            // Draw completed bar
            if(0<barfilled)
            {
                dc.setForeground(primary2);
                dc.fillRectangle(border,border,barfilled,height-(border<<1));
            }

            // Draw uncompleted bar
            if(barfilled<barlength)
            {
                dc.setForeground(secondary3);
                dc.fillRectangle(border+barfilled,border,barlength-barfilled,height-(border<<1));
            }

            // Draw text
            if(options&PROGRESSBAR_PERCENTAGE)
            {
                sprintf(numtext,"%d%%",percent);
                n=(FX::FXint)strlen(numtext);
                tw=font->getTextWidth(numtext,n-1); // Not including the % looks better
                th=font->getFontHeight();
                ty=(height-th)/2+font->getFontAscent();
                tx=(width-tw)/2;
                if(border+barfilled<=tx)
                {           // In right side
                    dc.setForeground(primary1);
                    dc.drawText(tx,ty,numtext,n);
                }
                else if(tx+tw<=border+barfilled)
                {   // In left side
                    dc.setForeground(secondary1);
                    dc.drawText(tx,ty,numtext,n);
                }else{                               // In between!
                    dc.setForeground(secondary1);
                    dc.setClipRectangle(border,border,barfilled,height);
                    dc.drawText(tx,ty,numtext,n);
                    dc.setForeground(primary1);
                    dc.setClipRectangle(border+barfilled,border,barlength-barfilled,height);
                    dc.drawText(tx,ty,numtext,n);
                    dc.clearClipRectangle();
                }
            }
        }
        return true;
    }

    TEMPLATE(FX::FXTabItem)
    {
#define TAB_ORIENT_MASK    (TAB_TOP|TAB_LEFT|TAB_RIGHT|TAB_BOTTOM)
        FXDCWindow dc(tgt,ev);
        int options = getOptions<Skin<FX::FXTabItem,Metal> >(tgt);
        FXint width=tgt->getWidth(),height=tgt->getHeight(), border=tgt->getBorderWidth();

        bool current = tgt->hasFocus() != 0;
        int index = 0;
        int currentIndex = -2;
        FX::FXTabBar * bar = dynamic_cast<FX::FXTabBar*>(tgt->getParent());
        if(bar)
        {
            int currentIndex = bar->getCurrent();
            int i = 0;
            FX::FXWindow * w = bar->getFirst();
            while(w != tgt && w)
            {
                if(dynamic_cast<FX::FXTabItem*>(w))
                    ++i;
                w = w->getNext();
            }
            if(w)
            {
                current = i==currentIndex;
                index = i;
            }
        }

        if(current)
            dc.setForeground(secondary3);
        else
            dc.setForeground(secondary2);
        dc.fillRectangle(0,0,width,height);

        FXint tw=0,th=0,iw=0,ih=0,tx,ty,ix,iy;
        switch(options&TAB_ORIENT_MASK)
        {
            case TAB_LEFT://todo
            dc.setForeground(white);
            dc.drawLine(2,0,width-1,0);
            dc.drawLine(0,2,1,1);
            dc.drawLine(0,height-2,0,2);
            dc.setForeground(secondary1);
            dc.drawLine(2,height-2,width-1,height-2);
            dc.setForeground(secondary2);
            dc.drawLine(3,height-1,width-1,height-1);
            break;
            case TAB_RIGHT://todo
            dc.setForeground(white);
            dc.drawLine(0,0,width-3,0);
            dc.drawLine(width-3,0,width-1,3);
            dc.setForeground(secondary1);
            dc.drawLine(width-2,2,width-2,height-2);
            dc.drawLine(0,height-2,width-2,height-2);
            dc.setForeground(secondary2);
            dc.drawLine(0,height-1,width-3,height-1);
            dc.drawLine(width-1,3,width-1,height-4);
            dc.drawLine(width-3,height-1,width-1,height-4);
            break;
            case TAB_BOTTOM://todo
            dc.setForeground(white);
            dc.drawLine(0,0,0,height-4);
            dc.drawLine(0,height-4,1,height-2);
            dc.setForeground(secondary1);
            dc.fillRectangle(2,height-2,width-4,1);
            dc.drawLine(width-2,0,width-2,height-3);
            dc.fillRectangle(width-2,0,2,1);
            dc.setForeground(secondary2);
            dc.drawLine(3,height-1,width-4,height-1);
            dc.drawLine(width-4,height-1,width-1,height-4);
            dc.fillRectangle(width-1,1,1,height-4);
            break;
            case TAB_TOP:
                {
                    dc.setForeground(secondary3);
                    dc.fillRectangle(0,0,6,1);
                    dc.fillRectangle(0,1,5,1);
                    dc.fillRectangle(0,2,4,1);
                    dc.fillRectangle(0,3,3,1);
                    dc.fillRectangle(0,4,2,1);
                    dc.fillRectangle(0,5,1,1);

                    dc.setForeground(secondary1);
                    int delta = 0==index?0:1;
                    dc.drawLine(0,height-delta,0,6);
                    dc.drawLine(6,0,width-1,0);
                    dc.drawLine(0,6,6,0);
                    dc.drawLine(width-1,0,width-1,height-1);
                    if(index > 0 && index != currentIndex+1)
                        dc.drawLine(0,2,0,6);

                    if(current)
                        dc.setForeground(white);
                    else
                        dc.setForeground(secondary3);
                    dc.drawLine(1,6,1,height);
                    dc.drawLine(6,1,width-1,1);
                    dc.drawLine(1,6,6,1);
                    if(current && 0!=index)
                        dc.drawLine(0,height-1,1,height-1);

                }
            break;
        }
        FXString label = tgt->getText();
        FXIcon * icon = tgt->getIcon();
        FXFont * font = controlFont;
        if(!label.empty())
        {
            tw=font->getTextWidth(label.text(),label.length());
            th=font->getFontHeight();
        }
        if(icon)
        {
            iw=icon->getWidth();
            ih=icon->getHeight();
        }
  		just_x<Skin<FX::FXTabItem,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXTabItem,Metal> >(tgt,ty,iy,th,ih);
        if(icon)
        {
            if(tgt->isEnabled())
                dc.drawIcon(icon,ix,iy);
            else
                dc.drawIconSunken(icon,ix,iy);
        }
        int hotoff = getHotoff<Skin<FX::FXTabItem,Metal> >(tgt);
        if(!label.empty())
        {
            dc.setTextFont(controlFont);
            if(tgt->isEnabled())
            {
                dc.setForeground(black);
                drawLabel<Skin<FX::FXTabItem,Metal> >(tgt,dc,label,hotoff,tx,ty,tw,th);
                if(tgt->hasFocus())
                {
                    dc.drawFocusRectangle(border+2,border+2,width-2*border-4,height-2*border-4);
                }
            }else{
                dc.setForeground(secondary2);
                drawLabel<Skin<FX::FXTabItem,Metal> >(tgt,dc,label,hotoff,tx,ty,tw,th);
            }
        }
        return true;
    }

    TEMPLATE(FX::FXTabBook)
    {
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        int options = getOptions<Skin<FX::FXTabBook,Metal> >(tgt);
        FXint width=tgt->getWidth(),height=tgt->getHeight();
        drawFrame(dc,0,0,width,height,options);
        return true;
    }

    TEMPLATE(FX::FXTabBar)
    {
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        int options = getOptions<Skin<FX::FXTabBar,Metal> >(tgt);
        FXint width=tgt->getWidth(),height=tgt->getHeight();
        drawFrame(dc,0,0,width,height,options);
        return true;
    }

    TEMPLATE(FX::FXSeparator)
    {
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        int options = getOptions<Skin<FX::FXSeparator,Metal> >(tgt);
   		FXint width=tgt->getWidth(),height=tgt->getHeight()
            ,padleft = tgt->getPadLeft()
            ,padright = tgt->getPadRight()
            ,padtop = tgt->getPadTop()
            ,padbottom = tgt->getPadBottom()
            ;
        int border = tgt->getBorderWidth(), kk,ll;

        // Draw frame
        drawFrame(dc,0,0,width,height,options);
        if((height-padbottom-padtop) < (width-padleft-padright))// Horizonal orientation
        {
            kk=(options&(SEPARATOR_GROOVE|SEPARATOR_RIDGE)) ? 2 : 1;
            ll=border+padtop+(height-padbottom-padtop-(border<<1)-kk)/2;
            if(options&SEPARATOR_GROOVE)
            {
                dc.setForeground(secondary1);
                dc.drawLine(border+padleft,ll,width-padright-padleft-(border<<1),ll);
                dc.setForeground(white);
                dc.drawLine(border+padleft,ll+1,width-padright-padleft-(border<<1),ll+1);
            }
            else if(options&SEPARATOR_RIDGE)
            {
                dc.setForeground(white);
                dc.drawLine(border+padleft,ll,width-padright-padleft-(border<<1),ll);
                dc.setForeground(secondary1);
                dc.drawLine(border+padleft,ll+1,width-padright-padleft-(border<<1),ll+1);
            }
            else if(options&SEPARATOR_LINE)
            {
                dc.setForeground(secondary1);
                dc.drawLine(border+padleft,ll,width-padright-padleft-(border<<1),ll);
            }
        }else{// Vertical orientation
            kk=(options&(SEPARATOR_GROOVE|SEPARATOR_RIDGE)) ? 2 : 1;
            ll=border+padleft+(width-padleft-padright-(border<<1)-kk)/2;
            if(options&SEPARATOR_GROOVE)
            {
                dc.setForeground(secondary1);
                dc.drawLine(ll,padtop+border,ll,height-padtop-padbottom-(border<<1));
                dc.setForeground(white);
                dc.drawLine(ll+1,padtop+border,ll+1,height-padtop-padbottom-(border<<1));
            }
            else if(options&SEPARATOR_RIDGE)
            {
                dc.setForeground(white);
                dc.drawLine(ll,padtop+border,ll,height-padtop-padbottom-(border<<1));
                dc.setForeground(secondary1);
                dc.drawLine(ll+1,padtop+border,ll+1,height-padtop-padbottom-(border<<1));
            }
            else if(options&SEPARATOR_LINE)
            {
                dc.setForeground(secondary1);
                dc.drawLine(ll,padtop+border,ll,height-padtop-padbottom-(border<<1));
            }
        }
        return true;
    }


    TEMPLATE(FX::FXMenuCommand)
    {
#define LEADSPACE   22
#define TRAILSPACE  16
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);

        FXint xx,yy;
        xx=LEADSPACE;

        FXIcon * icon = tgt->getIcon();
        FXString label = tgt->getText();
        FXFont * font = controlFont;
        FXString accel = tgt->getAccelText();
        int hotoff = getHotoff<Skin<FX::FXMenuCommand,Metal> >(tgt);
        int state = getState<Skin<FX::FXMenuCommand,Metal> >(tgt);
        int height = tgt->getHeight();
        int width = tgt->getWidth();

        if(!tgt->isEnabled())// Grayed out
        {
            if(icon)
            {
                dc.drawIconSunken(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            dc.setForeground(secondary2);
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.drawText(xx,yy,label.text(),label.length());
                dc.setTextFont(systemFont);
                if(!accel.empty()) 
                    dc.drawText(width-TRAILSPACE-font->getTextWidth(accel.text(),accel.length()),yy,accel.text(),accel.length());
                if(0<=hotoff)
                {
                    dc.fillRectangle(xx+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            if(state==MENUSTATE_CHECKED)
            {
                drawCheck(dc,5,(height-8)/2);
            }
            if(state==MENUSTATE_RCHECKED)
            {
                drawBullet(dc,7,(height-5)/2);
            }
        }
        else if(tgt->isActive())// Active
        {
            dc.setForeground(primary2);
            dc.fillRectangle(0,0,width,height);
            if(icon)
            {
                dc.drawIcon(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.setForeground(tgt->isEnabled() ? black : secondary2);
                dc.drawText(xx,yy,label.text(),label.length());
                dc.setTextFont(systemFont);
                if(!accel.empty()) 
                    dc.drawText(width-TRAILSPACE-font->getTextWidth(accel.text(),accel.length()),yy,accel.text(),accel.length());
                if(0<=hotoff){
                    dc.fillRectangle(xx+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            if(state==MENUSTATE_CHECKED)
            {
                dc.setForeground(black);
                drawCheck(dc,5,(height-8)/2);
            }
            if(state==MENUSTATE_RCHECKED)
            {
                dc.setForeground(black);
                drawBullet(dc,7,(height-5)/2);
            }
        }
        else// Normal
        {
            if(icon)
            {
                dc.drawIcon(icon,3,(height-icon->getHeight())/2);
                if(icon->getWidth()+5>xx) xx=icon->getWidth()+5;
            }
            if(!label.empty())
            {
                yy=font->getFontAscent()+(height-font->getFontHeight())/2;
                dc.setTextFont(font);
                dc.setForeground(black);
                dc.drawText(xx,yy,label.text(),label.length());
                dc.setTextFont(systemFont);
                if(!accel.empty()) 
                    dc.drawText(width-TRAILSPACE-font->getTextWidth(accel.text(),accel.length()),yy,accel.text(),accel.length());
                if(0<=hotoff){
                    dc.fillRectangle(xx+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
                }
            }
            if(state==MENUSTATE_CHECKED)
            {
                dc.setForeground(black);
                drawCheck(dc,5,(height-8)/2);
            }
            if(state==MENUSTATE_RCHECKED)
            {
                dc.setForeground(black);
                drawBullet(dc,7,(height-5)/2);
            }
        }
        return true;
    }

    TEMPLATE(FX::FXMenuSeparator)
    {
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);

        dc.setForeground(secondary1);
        dc.fillRectangle(1,0,tgt->getWidth(),1);
        dc.setForeground(white);
        dc.fillRectangle(1,1,tgt->getWidth(),1);
        return true;
    }


    TEMPLATE(FX::FXScrollBar)
    {
#define PRESSED_INC          1
#define PRESSED_DEC          2
#define PRESSED_PAGEINC      4
#define PRESSED_PAGEDEC      8
#define PRESSED_THUMB       16
#define PRESSED_FINETHUMB   32
#ifdef WIN32
        int delta = 1;
#else
        int delta = 2;
#endif

        FXDCWindow dc(tgt,ev);
        initFonts();
        FXint width=tgt->getWidth(),height=tgt->getHeight() ;
        int options = getOptions<Skin<FX::FXScrollBar,Metal> >(tgt)
            ,thumbsize=getThumbSize<Skin<FX::FXScrollBar,Metal> >(tgt)
            ,thumbpos=getThumbPos<Skin<FX::FXScrollBar,Metal> >(tgt)
            ,pressed=getPressed<Skin<FX::FXScrollBar,Metal> >(tgt)
            ;

        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        drawFrame(dc,ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        register int total;

        if(options&SCROLLBAR_HORIZONTAL)
        {
            total=width-height-height;
            if(thumbsize<total)// Scrollable
            {                     
                drawThumb(dc,thumbpos,0,thumbsize,height);
                if(pressed&PRESSED_PAGEDEC)
                {
                    drawTrough(dc, height,0,thumbpos-height,height,secondary1);
                }else{
                    drawTrough(dc, height,0,thumbpos-height,height,secondary3);
                }
                if(pressed&PRESSED_PAGEINC)
                {
                    drawTrough(dc, thumbpos+thumbsize,0,width-height-thumbpos-thumbsize,height,secondary1);
                }else{
                    drawTrough(dc, thumbpos+thumbsize,0,width-height-thumbpos-thumbsize,height,secondary3);
                }
            }else{                                                   // Non-scrollable
                drawThumb(dc,height,0,total,height);

            }
            dc.setForeground(secondary1);
            dc.drawLine(width-height-1,0,width-height-1,height-delta);
            dc.drawLine(height,0,height,height-delta);
            dc.setForeground(white);
            dc.drawLine(width-height,0,width-height,height-1);
            dc.drawLine(height+1,1,height+1,height-2);
            if(pressed & PRESSED_DEC)
                dc.setForeground(secondary2);
            else
                dc.setForeground(secondary3);
            dc.fillRectangle(2,2,height-3,height-4);
            if(pressed & PRESSED_INC)
                dc.setForeground(secondary2);
            else
                dc.setForeground(secondary3);
            dc.fillRectangle(width-height+1,2,height-4,height-4);

            dc.setForeground(black);
            FXPoint points[3];
            FXint ah,ab;
            int x = width-height, y=0, s=height;
            ab=(s-7)|1;
            ah=ab>>1;
            x=x+((s-ah)>>1);
            y=y+((s-ab)>>1);
            if(pressed&PRESSED_INC){ ++x; ++y; }
            points[0].x=x;
            points[0].y=y;
            points[1].x=x;
            points[1].y=y+ab-1;
            points[2].x=x+ah;
            points[2].y=y+(ab>>1);
            dc.fillPolygon(points,3);

            x=y=0;
            ab=(s-7)|1;
            ah=ab>>1;
            x=x+((s-ah)>>1);
            y=y+((s-ab)>>1);
            if(pressed&PRESSED_DEC){ ++x; ++y; }
            points[0].x=x+ah;
            points[0].y=y;
            points[1].x=x+ah;
            points[1].y=y+ab-1;
            points[2].x=x;
            points[2].y=y+(ab>>1);
            dc.fillPolygon(points,3);

        }else{
            total=height-width-width;
            if(thumbsize<total)
            {                                    // Scrollable
                drawThumb(dc,0,thumbpos,width,thumbsize,false);
                if(pressed&PRESSED_PAGEDEC)
                {
                    drawTrough(dc,0,width,width,thumbpos-width,secondary1,false);
                }else{
                    drawTrough(dc,0,width,width,thumbpos-width,secondary3,false);
                }
                if(pressed&PRESSED_PAGEINC)
                {
                    drawTrough(dc,0,thumbpos+thumbsize,width,height-width-thumbpos-thumbsize,secondary1,false);
                }else{
                    drawTrough(dc,0,thumbpos+thumbsize,width,height-width-thumbpos-thumbsize,secondary3,false);
                }
            }else{                                                   // Non-scrollable
                drawThumb(dc,0,width,width,total,false);
            }
            dc.setForeground(secondary1);
            dc.drawLine(0,height-width-1,width-delta,height-width-1);
            dc.drawLine(0,width,width-delta,width);
            dc.setForeground(white);
            dc.drawLine(0,height-width,width-1,height-width);
            dc.drawLine(1,width+1,width-2,width+1);

            if(pressed & PRESSED_DEC)
                dc.setForeground(secondary2);
            else
                dc.setForeground(secondary3);
            dc.fillRectangle(2,2,width-4,width-2);
            if(pressed & PRESSED_INC)
                dc.setForeground(secondary2);
            else
                dc.setForeground(secondary3);
            dc.fillRectangle(2,height-width+1,width-4,width-3);

            dc.setForeground(black);
            FXPoint points[3];
            FXint ah,ab;
            int x=0, y=height-width, s=width;
            ab=(s-7)|1;
            ah=ab>>1;
            x=x+((s-ab)>>1);
            y=y+((s-ah)>>1);
            if(pressed&PRESSED_INC){ ++x; ++y; }
            points[0].x=x+1;
            points[0].y=y;
            points[1].x=x+ab-1;
            points[1].y=y;
            points[2].x=x+(ab>>1);
            points[2].y=y+ah;
            dc.fillPolygon(points,3);

            x=y=0;
            ab=(s-7)|1;
            ah=ab>>1;
            x=x+((s-ab)>>1);
            y=y+((s-ah)>>1);
            if(pressed&PRESSED_DEC){ ++x; ++y; }
            points[0].x=x+(ab>>1);
            points[0].y=y-1;
            points[1].x=x;
            points[1].y=y+ah;
            points[2].x=x+ab;
            points[2].y=y+ah;
            dc.fillPolygon(points,3);
        }

        return true;
    }

    TEMPLATE(FX::FXHeader)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();
   		FXint width=tgt->getWidth(),height=tgt->getHeight() ;
        int options = getOptions<Skin<FX::FXHeader,Metal> >(tgt),
            active = getActive<Skin<FX::FXHeader,Metal> >(tgt)
            ;
        bool state = getBoolState<Skin<FX::FXHeader,Metal> >(tgt);
        FXint nitems = tgt->getNumItems();


        FXint x,y,w,h,i;
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        if(options&HEADER_VERTICAL)
        {
            for(i=0,y=0; i<nitems; i++)
            {
                h=tgt->getItem(i)->getHeight(tgt);
                if(ev->rect.y<y+h && y<ev->rect.y+ev->rect.h)
                {
                    if(i==active && state)
                    {
                        dc.setForeground(primary2);
                        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
                    }
                    drawFXHeaderItem(tgt->getItem(i),tgt,dc,0,y,width,h);
                    drawFrame(dc,0,y,width,h);
                }
                y+=h;
            }
            if(y<height)
            {
                drawFrame(dc,0,y,width,height-y);
            }
        }else{
            for(i=0,x=0; i<nitems; i++)
            {
                w=tgt->getItem(i)->getWidth(tgt);
                if(ev->rect.x<x+w && x<ev->rect.x+ev->rect.w)
                {
                    if(i==active && state)
                    {
                        dc.setForeground(primary2);
                        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
                    }
                    drawFXHeaderItem(tgt->getItem(i),tgt,dc,x,0,w,height);
                    drawFrame(dc,x,0,w,height);
                }
                x+=w;
            }
            if(x<width)
            {
                drawFrame(dc,x,0,width-x,height);
            }
        }
        return true;
    }

    TEMPLATE(FX::FXDragCorner)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

        FXint tx,ty;
   		FXint width=tgt->getWidth(),height=tgt->getHeight();

        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);

        dc.setForeground(white);

        int shorter = width;
        if(width > height) shorter = height;
        int xoff = width-shorter;
        int yoff = height-shorter;

        for(tx=0; tx<width-2-xoff; tx+=2)
        {
            for(ty=0; ty<height-yoff-2; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                if(tx+ty < shorter-2)
                    continue;
                dc.fillRectangle(tx+xoff,ty+yoff,1,1);
            }
        }

        dc.setForeground(secondary1);
        for(tx=0; tx<width-2-xoff; tx+=2)
        {
            for(ty=0; ty<height-2-yoff; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                if(tx+ty < shorter-2)
                    continue;
                dc.fillRectangle(tx+1+xoff,ty+1+yoff,1,1);
            }
        }
        return true;
    }


    TEMPLATE(FX::FXStatusLine)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

   		FXint width=tgt->getWidth(),height=tgt->getHeight()
            ,padleft = tgt->getPadLeft()
            ,padtop = tgt->getPadTop()
            ,padbottom = tgt->getPadBottom()
            ;

        FXString contents = tgt->getText();
        FX::FXFont * font = controlFont;

        int options = getOptions<Skin<FX::FXStatusLine,Metal> >(tgt);

        FXint ty=padtop+(height-padtop-padbottom-font->getFontHeight())/2;
        FXint pos,len;
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        if(!contents.empty())
        {
            dc.setTextFont(font);
            pos=contents.find('\n');
            len=contents.length();
            if(pos>=0)
            {
                dc.setForeground(primary3);
                dc.drawText(padleft,ty+font->getFontAscent(),contents.text(),pos);
                dc.setForeground(primary1);
                dc.drawText(padleft+font->getTextWidth(contents.text(),pos),
                    ty+font->getFontAscent(),contents.text()+pos+1,len-pos-1);
            }else{
                dc.setForeground(primary1);
                dc.drawText(padleft,ty+font->getFontAscent(),contents.text(),len);
            }
        }
        drawFrame(dc,0,0,width,height,options);
        return true;
    }


    TEMPLATE(FX::FXTextField)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

        FXint width=tgt->getWidth(),height=tgt->getHeight(),
            cursor=tgt->getCursorPos()
            ,padleft = tgt->getPadLeft()
            ,padright = tgt->getPadRight()
            ,padtop = tgt->getPadTop()
            ,padbottom = tgt->getPadBottom()
            ;


        FXString contents = tgt->getText();
        FX::FXFont * font = userFont;

        int options = getOptions<Skin<FX::FXTextField,Metal> >(tgt)
            ,shift = getShift<Skin<FX::FXTextField,Metal> >(tgt)
            ;
        int border = tgt->getBorderWidth();
        FXuint flags = getFlags<Skin<FX::FXTextField,Metal> >(tgt);

        // Draw frame
        drawFrame(dc,0,0,width,height,options);

        // Draw background
        // Gray background if disabled
        if(tgt->isEnabled())
            dc.setForeground(white);
        else
            dc.setForeground(secondary3);
        dc.fillRectangle(border,border,width-(border<<1),height-(border<<1));

        // Draw text, clipped against frame interior
        dc.setClipRectangle(border,border,width-(border<<1),height-(border<<1));
        drawText(dc,tgt);

        // Draw caret
#define FLAG_CARET 0x00008000
        if(flags&FLAG_CARET)
        {
            int xx;
            if(options&JUSTIFY_RIGHT){
                if(options&TEXTFIELD_PASSWD){
                    xx=shift+width-border-padright-font->getTextWidth("*",1)*(contents.length()-cursor);
                }else{
                    xx=shift+width-border-padright-font->getTextWidth(&contents[cursor],contents.length()-cursor);
                }
            }else{
                if(options&TEXTFIELD_PASSWD){
                    xx=shift+border+padleft+font->getTextWidth("*",1)*cursor;
                }else{
                    xx=shift+border+padleft+font->getTextWidth(contents.text(),cursor);
                }
            }
            --xx;
            if(tgt->isEnabled())
                dc.setForeground(black);
            else
                dc.setForeground(secondary2);
            dc.fillRectangle(xx,padtop+border,1,height-padbottom-padtop-(border<<1));
            dc.fillRectangle(xx-2,padtop+border,5,1);
            dc.fillRectangle(xx-2,height-border-padbottom-1,5,1);
        }
        return true;
    }

    TEMPLATE(FX::FXMenuTitle)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

        FXint width=tgt->getWidth(),height=tgt->getHeight();

        FXint xx=6,yy=0;
        dc.setTextFont(controlFont);
        if(tgt->isEnabled())
        {
            if(tgt->isActive())
            {
                dc.setForeground(primary2);
                dc.fillRectangle(0,0,width-1,height);
                dc.setForeground(primary1);
                dc.drawLine(0,0,width-2,0);
                dc.drawLine(0,0,0,height-1);
                dc.drawLine(0,height-1,width-2,height-1);
                dc.drawLine(width-2,height-1,width-2,2);
                dc.setForeground(white);
                dc.drawLine(width-1,height-2,width-1,1);
                ++xx;
                ++yy;
            }else if(tgt->underCursor()){
                dc.setForeground(primary3);
                dc.fillRectangle(0,0,width-1,height);
                dc.setForeground(primary2);
                dc.drawLine(0,0,width-2,0);
                dc.drawLine(0,0,0,height-1);
                dc.drawLine(0,height-1,width-2,height-1);
                dc.drawLine(width-2,height-1,width-2,2);
                dc.setForeground(white);
                dc.drawLine(width-1,height-2,width-1,1);
            }else{
                dc.setForeground(secondary3);
                dc.fillRectangle(0,0,width,height);
            }
        }else{
            dc.setForeground(secondary3);
            dc.fillRectangle(0,0,width,height);
        }

        // Draw the icon
        FXIcon * icon = getIcon<Skin<FX::FXMenuTitle,Metal> >(tgt);
  		if(icon)
        {
    		if(tgt->isEnabled())
            {
                dc.drawIcon(icon,xx,yy+(height-icon->getHeight())/2);
                xx+=5+icon->getWidth();
            }
    		else
            {
                dc.drawIconSunken(icon,xx,yy+(height-icon->getHeight())/2);
                xx+=5+icon->getWidth();
            }
    	}

  		// Draw the text
        FXString label = getLabel<Skin<FX::FXMenuTitle,Metal> >(tgt);
  		if(!label.empty())
        {
    		dc.setTextFont(controlFont);
            yy+=controlFont->getFontAscent()+(height-controlFont->getFontHeight())/2;
            FX::FXFont * font = controlFont;
            int hotoff = getHotoff<Skin<FX::FXMenuTitle,Metal> >(tgt);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(black);
      		}
    		else
            {
      			dc.setForeground(secondary2);
      		}
            dc.drawText(xx,yy,label.text(),label.length());
            if(0<=hotoff)
            {
                dc.fillRectangle(xx+font->getTextWidth(&label[0],hotoff),yy+1,font->getTextWidth(&label[hotoff],1),1);
            }
        }
        return 1;
    }


    TEMPLATE(FX::FXMenuButton)
    {

#define MENUBUTTONARROW_WIDTH   11
#define MENUBUTTONARROW_HEIGHT  5
        FXDCWindow dc(tgt,ev);
        initFonts();

   		FXint width=tgt->getWidth(),height=tgt->getHeight(),state=getState<Skin<FX::FXMenuButton,Metal> >(tgt);

        FXint tw=0,th=0,iw=0,ih=0,tx,ty,ix,iy;
        FXPoint points[3];
        int options = getOptions<Skin<FX::FXMenuButton,Metal> >(tgt);

        dc.setForeground(secondary3);
        // Toolbar style
        if(options&MENUBUTTON_TOOLBAR)
        {
            // Enabled and cursor inside, and not popped up
            dc.fillRectangle(0,0,width,height);
            if(tgt->isEnabled() && tgt->underCursor() && !state)
            {
                dc.setForeground(secondary1);
                dc.drawLine(0,0,width-2,0);
                dc.drawLine(0,0,0,height-2);
                dc.drawLine(2,height-2,width-2,height-2);
                dc.drawLine(width-2,2,width-2,height-2);
                dc.setForeground(white);
                dc.drawLine(1,1,width-3,1);
                dc.drawLine(1,1,1,height-3);
                dc.drawLine(1,height-1,width-1,height-1);
                dc.drawLine(width-1,1,width-1,height-1);
            }
        }
        else if(tgt->isEnabled() && state)
        {
            dc.setForeground(primary2);
            dc.fillRectangle(0,0,width-1,height);
            dc.setForeground(primary1);
            dc.drawLine(0,0,width-2,0);
            dc.drawLine(0,0,0,height-1);
            dc.drawLine(0,height-1,width-2,height-1);
            dc.drawLine(width-2,height-1,width-2,2);
            dc.setForeground(white);
            dc.drawLine(width-1,height-2,width-1,1);
        }
        else
        {
            dc.fillRectangle(0,0,width,height);
        }

  		// Place text & icon
  		if(!getLabel<Skin<FX::FXMenuButton,Metal> >(tgt).empty())
        {
    		tw=labelWidth(getLabel<Skin<FX::FXMenuButton,Metal> >(tgt), controlFont);
    		th=labelHeight(getLabel<Skin<FX::FXMenuButton,Metal> >(tgt), controlFont);
    	}
  		if(getIcon<Skin<FX::FXMenuButton,Metal> >(tgt))
        {
    		iw=getIcon<Skin<FX::FXMenuButton,Metal> >(tgt)->getWidth();
    		ih=getIcon<Skin<FX::FXMenuButton,Metal> >(tgt)->getHeight();
    	}
        else if(!(options&MENUBUTTON_NOARROWS))
        {
            if(options&MENUBUTTON_LEFT)
            {
                ih=MENUBUTTONARROW_WIDTH;
                iw=MENUBUTTONARROW_HEIGHT;
            } else {
                iw=MENUBUTTONARROW_WIDTH;
                ih=MENUBUTTONARROW_HEIGHT;
            }
        }

  		just_x<Skin<FX::FXMenuButton,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXMenuButton,Metal> >(tgt,ty,iy,th,ih);

  		// Shift a bit when pressed
  		if(state)
            { ++tx; ++ty; ++ix; ++iy; }

  		// Draw the icon
  		if(getIcon<Skin<FX::FXMenuButton,Metal> >(tgt))
        {
    		if(tgt->isEnabled())
      			dc.drawIcon(getIcon<Skin<FX::FXMenuButton,Metal> >(tgt),ix,iy);
    		else
      			dc.drawIconSunken(getIcon<Skin<FX::FXMenuButton,Metal> >(tgt),ix,iy);
    	}
        else if(!(options&MENUBUTTON_NOARROWS))  // Draw arrows
        {
            if(tgt->isEnabled())
                dc.setForeground(black);
            else
                dc.setForeground(secondary2);
            
            if((options&MENUBUTTON_RIGHT)==MENUBUTTON_RIGHT)// Right arrow
            {
                points[0].x=ix;
                points[0].y=iy;
                points[1].x=ix;
                points[1].y=iy+MENUBUTTONARROW_WIDTH-1;
                points[2].x=ix+MENUBUTTONARROW_HEIGHT;
                points[2].y=(FXshort)(iy+(MENUBUTTONARROW_WIDTH>>1));
            }
            else if(options&MENUBUTTON_LEFT)// Left arrow
            {
                points[0].x=ix+MENUBUTTONARROW_HEIGHT;
                points[0].y=iy;
                points[1].x=ix+MENUBUTTONARROW_HEIGHT;
                points[1].y=iy+MENUBUTTONARROW_WIDTH-1;
                points[2].x=ix;
                points[2].y=(FXshort)(iy+(MENUBUTTONARROW_WIDTH>>1));
            }
            else if(options&MENUBUTTON_UP)// Up arrow
            {
                points[0].x=(FXshort)(ix+(MENUBUTTONARROW_WIDTH>>1));
                points[0].y=iy-1;
                points[1].x=ix;
                points[1].y=iy+MENUBUTTONARROW_HEIGHT;
                points[2].x=ix+MENUBUTTONARROW_WIDTH;
                points[2].y=iy+MENUBUTTONARROW_HEIGHT;
            }
            else// Down arrow
            {
                points[0].x=ix+1;
                points[0].y=iy;
                points[2].x=ix+MENUBUTTONARROW_WIDTH-1;
                points[2].y=iy;
                points[1].x=(FXshort)(ix+(MENUBUTTONARROW_WIDTH>>1));
                points[1].y=iy+MENUBUTTONARROW_HEIGHT;
            }
            dc.fillPolygon(points,3);
        }

  		// Draw the text
  		if(!getLabel<Skin<FX::FXMenuButton,Metal> >(tgt).empty())
        {
    		dc.setTextFont(controlFont);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(black);
      			drawLabel<Skin<FX::FXMenuButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXMenuButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXMenuButton,Metal> >(tgt),tx,ty,tw,th);
      			if(tgt->hasFocus())
                {
                    int border = tgt->getBorderWidth();
        			dc.drawFocusRectangle(border+2,border+2,width-2*border-4,height-2*border-4);
        		}
      		}
    		else
            {
      			dc.setForeground(secondary2);
      			drawLabel<Skin<FX::FXMenuButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXMenuButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXMenuButton,Metal> >(tgt),tx,ty,tw,th);
      		}
        }
        return true;
    }

    TEMPLATE(FX::FXToolBarGrip)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

        FXint tx,ty;
   		FXint width=tgt->getWidth(),height=tgt->getHeight();

        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);

        dc.setForeground(white);
        for(tx=0; tx<width-2; tx+=2)
        {
            for(ty=0; ty<height-2; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                dc.fillRectangle(tx,ty,1,1);
            }
        }

        dc.setForeground(secondary1);
        for(tx=0; tx<width-2; tx+=2)
        {
            for(ty=0; ty<height-2; ty+=2)
            {
                if((tx+ty)%4)
                    continue;
                dc.fillRectangle(tx+1,ty+1,1,1);
            }
        }
        return true;
    }

    TEMPLATE(FX::FXToolTip)
    {
#define HSPACE  4
#define VSPACE  2
        FXDCWindow dc(tgt,ev);
        initFonts();

        const FXchar *beg,*end;
        FXint tx,ty;
   		FXint width=tgt->getWidth(),height=tgt->getHeight();


        dc.setForeground(primary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        dc.setForeground(black);
        dc.setTextFont(systemFont);
        dc.drawRectangle(0,0,width-1,height-1);
        beg=getLabel<Skin<FX::FXToolTip,Metal> >(tgt).text();
        if(beg)
        {
            tx=1+HSPACE;
            ty=1+VSPACE+systemFont->getFontAscent();
            do
            {
                end=beg;
                while(*end!='\0' && *end!='\n') end++;
                dc.drawText(tx,ty,beg,end-beg);
                ty+=systemFont->getFontHeight();
                beg=end+1;
            }
            while(*end!='\0');
        }
        return true;
    }


    TEMPLATE(FX::FXCheckButton)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

   		FXint w=tgt->getWidth(),h=tgt->getHeight();

        dc.setForeground(secondary3);

        FXint      tw=0,th=0,iw=13,ih=13,tx,ty,ix,iy;
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);

  		// Place text & checkbox
  		if(!getLabel<Skin<FX::FXCheckButton,Metal> >(tgt).empty())
        {
    		tw=labelWidth(getLabel<Skin<FX::FXCheckButton,Metal> >(tgt), controlFont);
    		th=labelHeight(getLabel<Skin<FX::FXCheckButton,Metal> >(tgt), controlFont);
    	}
  		just_x<Skin<FX::FXCheckButton,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXCheckButton,Metal> >(tgt,ty,iy,th,ih);

        // draw the check box frame
        if(tgt->isEnabled())
        {
            dc.setForeground(secondary1);
            dc.drawLine(ix,iy,ix+iw-2,iy);
            dc.drawLine(ix,iy,ix,iy+ih-2);
            dc.drawLine(ix+2,iy+ih-2,ix+iw-2,iy+ih-2);
            dc.drawLine(ix+iw-2,iy+2,ix+iw-2,iy+ih-2);
            dc.setForeground(white);
            dc.drawLine(ix+1,iy+1,ix+iw-3,iy+1);
            dc.drawLine(ix+1,iy+1,ix+1,iy+ih-3);
            dc.drawLine(ix+1,iy+ih-1,ix+iw-1,iy+ih-1);
            dc.drawLine(ix+iw-1,iy+1,ix+iw-1,iy+ih-1);
        }
        else
        {
            dc.setForeground(secondary2);
            dc.drawRectangle(ix,iy,ix+12,iy+12);
        }

        if(tgt->getCheck()!=FALSE){
            FXSegment seg[6];
#ifndef WIN32
        seg[0].x1=3+ix; seg[0].y1=5+iy; seg[0].x2=5+ix; seg[0].y2=7+iy;
        seg[1].x1=3+ix; seg[1].y1=6+iy; seg[1].x2=5+ix; seg[1].y2=8+iy;
        seg[2].x1=3+ix; seg[2].y1=7+iy; seg[2].x2=5+ix; seg[2].y2=9+iy;
        seg[3].x1=5+ix; seg[3].y1=7+iy; seg[3].x2=9+ix; seg[3].y2=3+iy;
        seg[4].x1=5+ix; seg[4].y1=8+iy; seg[4].x2=9+ix; seg[4].y2=4+iy;
        seg[5].x1=5+ix; seg[5].y1=9+iy; seg[5].x2=9+ix; seg[5].y2=5+iy;
#else
        seg[0].x1=3+ix; seg[0].y1=5+iy; seg[0].x2=5+ix; seg[0].y2=7+iy;
        seg[1].x1=3+ix; seg[1].y1=6+iy; seg[1].x2=5+ix; seg[1].y2=8+iy;
        seg[2].x1=3+ix; seg[2].y1=7+iy; seg[2].x2=5+ix; seg[2].y2=9+iy;
        seg[3].x1=5+ix; seg[3].y1=7+iy; seg[3].x2=10+ix; seg[3].y2=2+iy;
        seg[4].x1=5+ix; seg[4].y1=8+iy; seg[4].x2=10+ix; seg[4].y2=3+iy;
        seg[5].x1=5+ix; seg[5].y1=9+iy; seg[5].x2=10+ix; seg[5].y2=4+iy;
#endif
        if(tgt->isEnabled()){
            if(tgt->getCheck()==MAYBE)
                dc.setForeground(secondary2);
            else
                dc.setForeground(black);
        }
        else
        {
            dc.setForeground(secondary2);
        }
        dc.drawLineSegments(seg,6);
        }


  		// Draw the text
  		if(!getLabel<Skin<FX::FXCheckButton,Metal> >(tgt).empty())
        {
    		dc.setTextFont(controlFont);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(primary1);
      			drawLabel<Skin<FX::FXCheckButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXCheckButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXCheckButton,Metal> >(tgt),tx,ty,tw,th);
      			if(tgt->hasFocus())
                {
        			dc.drawFocusRectangle(tx-1,ty-1,tw+2,th+2);
        		}
      		}
    		else
            {
      			dc.setForeground(secondary2);
      			drawLabel<Skin<FX::FXCheckButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXCheckButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXCheckButton,Metal> >(tgt),tx,ty,tw,th);
      		}
        }

        int options = getOptions<Skin<FX::FXCheckButton,Metal> >(tgt);
        drawFrame(dc,0,0,w,h,options);
        return true;
    }


    TEMPLATE(FX::FXLabel)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();

   		FXint w=tgt->getWidth(),h=tgt->getHeight();

        dc.setForeground(secondary3);
        dc.fillRectangle(0,0,w,h);

        FXint      tw=0,th=0,iw=0,ih=0,tx,ty,ix,iy;

  		// Place text & icon
  		if(!getLabel<Skin<FX::FXLabel,Metal> >(tgt).empty())
        {
    		tw=labelWidth(getLabel<Skin<FX::FXLabel,Metal> >(tgt), controlFont);
    		th=labelHeight(getLabel<Skin<FX::FXLabel,Metal> >(tgt), controlFont);
    	}
  		if(getIcon<Skin<FX::FXLabel,Metal> >(tgt))
        {
    		iw=getIcon<Skin<FX::FXLabel,Metal> >(tgt)->getWidth();
    		ih=getIcon<Skin<FX::FXLabel,Metal> >(tgt)->getHeight();
    	}
  		just_x<Skin<FX::FXLabel,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXLabel,Metal> >(tgt,ty,iy,th,ih);

        // Draw the icon
  		if(getIcon<Skin<FX::FXLabel,Metal> >(tgt))
        {
    		if(tgt->isEnabled())
      			dc.drawIcon(getIcon<Skin<FX::FXLabel,Metal> >(tgt),ix,iy);
    		else
      			dc.drawIconSunken(getIcon<Skin<FX::FXLabel,Metal> >(tgt),ix,iy);
    	}

  		// Draw the text
  		if(!getLabel<Skin<FX::FXLabel,Metal> >(tgt).empty())
        {
    		dc.setTextFont(controlFont);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(primary1);
      			drawLabel<Skin<FX::FXLabel,Metal> >(tgt,dc,getLabel<Skin<FX::FXLabel,Metal> >(tgt),
                	getHotoff<Skin<FX::FXLabel,Metal> >(tgt),tx,ty,tw,th);
      		}
    		else
            {
      			dc.setForeground(secondary2);
      			drawLabel<Skin<FX::FXLabel,Metal> >(tgt,dc,getLabel<Skin<FX::FXLabel,Metal> >(tgt),
                	getHotoff<Skin<FX::FXLabel,Metal> >(tgt),tx,ty,tw,th);
      		}
        }

        int options = getOptions<Skin<FX::FXLabel,Metal> >(tgt);
        drawFrame(dc,0,0,w,h,options);
        return true;
    }

    TEMPLATE(FX::FXScrollCorner)
    {
        FXDCWindow dc(tgt,ev);
        dc.setForeground(secondary3);
        dc.fillRectangle(ev->rect.x,ev->rect.y,ev->rect.w,ev->rect.h);
        return true;
    }

    TEMPLATE(FX::FXPacker)
    {
        FXDCWindow dc(tgt,ev);
        FXint w=tgt->getWidth(),h=tgt->getHeight();

        dc.setForeground(secondary3);
        dc.fillRectangle(0,0,w,h);
        int options = getOptions<Skin<FX::FXPacker,Metal> >(tgt);
        drawFrame(dc,0,0,w,h,options);
        return true;
    }

    TEMPLATE(FX::FXFrame)
    {
        FXDCWindow dc(tgt,ev);
   		FXint w=tgt->getWidth(),h=tgt->getHeight();

        dc.setForeground(secondary3);

        int options = getOptions<Skin<FX::FXFrame,Metal> >(tgt);
        int border=(options&FRAME_THICK)?2:(options&(FRAME_SUNKEN|FRAME_RAISED))?1:0;
        dc.fillRectangle(border,border,tgt->getWidth()-(border<<1),tgt->getHeight()-(border<<1));
        drawFrame(dc,0,0,w,h,options);
        return true;
    }

    TEMPLATE(FX::FXWindow)
    {
        FXDCWindow dc(tgt,ev);
   		FXint w=tgt->getWidth(),h=tgt->getHeight();

        dc.setForeground(secondary3);
        dc.fillRectangle(0,0,w,h);
        return true;
    }

    TEMPLATE(FX::FXButton)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();
        FX::FXWindow * p = tgt->getParent();
        FX::FXTreeListBox * c = dynamic_cast<FX::FXTreeListBox*>(p);

        //FX::FXFont * buttonFont = tgt->getFont();
        //tgt->setFont(control);

        FXint w=tgt->getWidth(),h=tgt->getHeight();

        if(c)
            dc.setForeground(white);
        else
            dc.setForeground(secondary3);
        dc.fillRectangle(0,0,w,h);

        dc.setForeground(secondary1);

      	if(tgt->isEnabled() && (tgt->getState()==STATE_UP))
        {
            /*
        	if(tgt->underCursor()) // draw mouseover
            {
            }
            else if (getOptions<Skin<FX::FXButton,Metal> >(tgt)&BUTTON_TOOLBAR) // draw toolbar
            {
            }
            else // draw standard
            */
            if( (getOptions<Skin<FX::FXButton,Metal> >(tgt)&BUTTON_TOOLBAR) &&
                (!tgt->underCursor()))
            {}
            else if(c)
            {}
            else if (tgt->isDefault()) // draw default
            {
                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(1,1,w-3,1);
                dc.drawLine(1,1,1,h-3);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.drawLine(3,h-3,w-3,h-3);
                dc.drawLine(w-3,3,w-3,h-3);
                dc.setForeground(white);
                dc.drawLine(2,2,w-4,2);
                dc.drawLine(2,2,2,h-4);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
            }
            else
            {
                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.setForeground(white);
                dc.drawLine(1,1,w-3,1);
                dc.drawLine(1,1,1,h-3);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
            }
        }
        else if (!tgt->isEnabled()) // draw disabled
        {
            if((getOptions<Skin<FX::FXButton,Metal> >(tgt)&BUTTON_TOOLBAR))
            {}
            else if(c)
            {}
            else
            {
                dc.setForeground(secondary2);
                dc.drawRectangle(0,0,w-1,h-1);
            }
        }
        /*
		else if (tgt->getState()==STATE_ENGAGED) // draw latched
        {
        }
        */
		else if (tgt->getState()) // draw pressed
        {
            dc.setForeground(secondary2);
            dc.fillRectangle(0,0,w,h);

                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.setForeground(white);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
                dc.setForeground(secondary2);
                dc.drawRectangle(1,1,w-3,h-3);
        }


  		FXint tw=0,th=0,iw=0,ih=0,tx,ty,ix,iy;
  		// Place text & icon
  		if(!getLabel<Skin<FX::FXButton,Metal> >(tgt).empty())
        {
    		tw=labelWidth(getLabel<Skin<FX::FXButton,Metal> >(tgt), controlFont);
    		th=labelHeight(getLabel<Skin<FX::FXButton,Metal> >(tgt), controlFont);
    	}
  		if(getIcon<Skin<FX::FXButton,Metal> >(tgt))
        {
    		iw=getIcon<Skin<FX::FXButton,Metal> >(tgt)->getWidth();
    		ih=getIcon<Skin<FX::FXButton,Metal> >(tgt)->getHeight();
    	}
  		just_x<Skin<FX::FXButton,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXButton,Metal> >(tgt,ty,iy,th,ih);

  		// Shift a bit when pressed
        // TODO:: control from Metal
  		if(tgt->getState())
            { ++tx; ++ty; ++ix; ++iy; }

  		// Draw the icon
  		if(getIcon<Skin<FX::FXButton,Metal> >(tgt))
        {
    		if(tgt->isEnabled())
      			dc.drawIcon(getIcon<Skin<FX::FXButton,Metal> >(tgt),ix,iy);
    		else
      			dc.drawIconSunken(getIcon<Skin<FX::FXButton,Metal> >(tgt),ix,iy);
    	}

  		// Draw the text
  		if(!getLabel<Skin<FX::FXButton,Metal> >(tgt).empty())
        {
    		dc.setTextFont(controlFont);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(black);
      			drawLabel<Skin<FX::FXButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXButton,Metal> >(tgt),tx,ty,tw,th);
      			if(tgt->hasFocus())
                {
        			dc.drawFocusRectangle(tgt->getBorderWidth()+1,
                    	tgt->getBorderWidth()+1,
                        tgt->getWidth()-2*tgt->getBorderWidth()-2,
                        tgt->getHeight()-2*tgt->getBorderWidth()-2);
        		}
      		}
    		else
            {
      			dc.setForeground(secondary2);
      			drawLabel<Skin<FX::FXButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXButton,Metal> >(tgt),tx,ty,tw,th);
      		}
        }

        return true;
    }

    TEMPLATE(FX::FXToggleButton)
    {
        FXDCWindow dc(tgt,ev);
        initFonts();
        FX::FXWindow * p = tgt->getParent();
        FX::FXTreeListBox * c = dynamic_cast<FX::FXTreeListBox*>(p);

        //FX::FXFont * buttonFont = tgt->getFont();
        //tgt->setFont(control);

        FXint w=tgt->getWidth(),h=tgt->getHeight();

        if(c)
            dc.setForeground(white);
        else
            dc.setForeground(secondary3);
        dc.fillRectangle(0,0,w,h);

        dc.setForeground(secondary1);

      	if(tgt->isEnabled() && (tgt->getState()==STATE_UP))
        {
            /*
        	if(tgt->underCursor()) // draw mouseover
            {
            }
            else if (getOptions<Skin<FX::FXToggleButton,Metal> >(tgt)&BUTTON_TOOLBAR) // draw toolbar
            {
            }
            else // draw standard
            */
            if( (getOptions<Skin<FX::FXToggleButton,Metal> >(tgt)&BUTTON_TOOLBAR) &&
                (!tgt->underCursor()))
            {}
            else if(c)
            {}
            else if (tgt->isDefault()) // draw default
            {
                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(1,1,w-3,1);
                dc.drawLine(1,1,1,h-3);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.drawLine(3,h-3,w-3,h-3);
                dc.drawLine(w-3,3,w-3,h-3);
                dc.setForeground(white);
                dc.drawLine(2,2,w-4,2);
                dc.drawLine(2,2,2,h-4);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
            }
            else
            {
                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.setForeground(white);
                dc.drawLine(1,1,w-3,1);
                dc.drawLine(1,1,1,h-3);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
            }
        }
        else if (!tgt->isEnabled()) // draw disabled
        {
            if((getOptions<Skin<FX::FXToggleButton,Metal> >(tgt)&TOGGLEBUTTON_TOOLBAR))
            {}
            else if(c)
            {}
            else
            {
                dc.setForeground(secondary2);
                dc.drawRectangle(0,0,w-1,h-1);
            }
        }
        /*
		else if (tgt->getState()==STATE_ENGAGED) // draw latched
        {
        }
        */
		else if (tgt->getState()) // draw pressed
        {
            dc.setForeground(secondary2);
            dc.fillRectangle(0,0,w,h);

                dc.drawLine(0,0,w-2,0);
                dc.drawLine(0,0,0,h-2);
                dc.drawLine(2,h-2,w-2,h-2);
                dc.drawLine(w-2,2,w-2,h-2);
                dc.setForeground(white);
                dc.drawLine(1,h-1,w-1,h-1);
                dc.drawLine(w-1,1,w-1,h-1);
                dc.setForeground(secondary2);
                dc.drawRectangle(1,1,w-3,h-3);
        }


  		FXint tw=0,th=0,iw=0,ih=0,tx,ty,ix,iy;
  		// Place text & icon
  		if(!getLabel<Skin<FX::FXToggleButton,Metal> >(tgt).empty())
        {
    		tw=labelWidth(getLabel<Skin<FX::FXToggleButton,Metal> >(tgt), controlFont);
    		th=labelHeight(getLabel<Skin<FX::FXToggleButton,Metal> >(tgt), controlFont);
    	}
  		if(getIcon<Skin<FX::FXToggleButton,Metal> >(tgt))
        {
    		iw=getIcon<Skin<FX::FXToggleButton,Metal> >(tgt)->getWidth();
    		ih=getIcon<Skin<FX::FXToggleButton,Metal> >(tgt)->getHeight();
    	}
  		just_x<Skin<FX::FXToggleButton,Metal> >(tgt,tx,ix,tw,iw);
  		just_y<Skin<FX::FXToggleButton,Metal> >(tgt,ty,iy,th,ih);

  		// Shift a bit when pressed
        // TODO:: control from Metal
  		if(tgt->getState())
            { ++tx; ++ty; ++ix; ++iy; }

  		// Draw the icon
  		if(getIcon<Skin<FX::FXToggleButton,Metal> >(tgt))
        {
    		if(tgt->isEnabled())
      			dc.drawIcon(getIcon<Skin<FX::FXToggleButton,Metal> >(tgt),ix,iy);
    		else
      			dc.drawIconSunken(getIcon<Skin<FX::FXToggleButton,Metal> >(tgt),ix,iy);
    	}

  		// Draw the text
  		if(!getLabel<Skin<FX::FXToggleButton,Metal> >(tgt).empty())
        {
    		dc.setTextFont(controlFont);
    		if(tgt->isEnabled())
            {
      			dc.setForeground(black);
      			drawLabel<Skin<FX::FXToggleButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXToggleButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXToggleButton,Metal> >(tgt),tx,ty,tw,th);
      			if(tgt->hasFocus())
                {
        			dc.drawFocusRectangle(tgt->getBorderWidth()+1,
                    	tgt->getBorderWidth()+1,
                        tgt->getWidth()-2*tgt->getBorderWidth()-2,
                        tgt->getHeight()-2*tgt->getBorderWidth()-2);
        		}
      		}
    		else
            {
      			dc.setForeground(secondary2);
      			drawLabel<Skin<FX::FXToggleButton,Metal> >(tgt,dc,getLabel<Skin<FX::FXToggleButton,Metal> >(tgt),
                	getHotoff<Skin<FX::FXToggleButton,Metal> >(tgt),tx,ty,tw,th);
      		}
        }

        return true;
    }


    // Defaults must follow specializations or the specializations
    // will be the second version of the same function
    template<class T> bool Metal::onSkin(T * t, FX::FXEvent * e)
    {
        return false;
    }
} //namespace
#else
namespace FoxSkin {

	class Metal
	{
	};
}
#endif
