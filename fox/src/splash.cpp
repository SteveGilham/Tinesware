/* splash.cpp : A  splash window
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

#include "splash.h"
#include "ctcfox.h"
#include "splashimg.h"

#include <fx.h>

#ifdef WIN32
#include <windows.h>
#else
#include <sys/time.h>
#include <time.h>
#endif

static const int HEIGHT = 200;
static const int WIDTH = 320;
#define VERSION ""


namespace CTCFox {

    class SplashWindowImpl : FXDialogBox
    {

    // Macro for class hierarchy declarations
    FXDECLARE(SplashWindowImpl)

    public:
        SplashWindowImpl();
        virtual ~SplashWindowImpl();
        FXWindow * getWindow();
        long onPaint(FXObject*,FXSelector,void*);
    private:
        SplashWindowImpl(const SplashWindowImpl &);
        void operator= (const SplashWindowImpl &);
        void drawText(FXDCWindow * dc, FXString & str, int top, int left, int right);
        FXGIFIcon icon;

#ifdef WIN32
        LARGE_INTEGER frequency;
        LARGE_INTEGER start;
#else
        struct timeval start;
#endif
    };


    SplashWindow::SplashWindow() :
        impl(new SplashWindowImpl())
    {
    }

    SplashWindow::~SplashWindow()
    {
        delete impl;
    }

    FXWindow * SplashWindow::getWindow()
    {
        return impl->getWindow();
    }


FXDEFMAP(SplashWindowImpl) SplashWindowImplMap[]={
  FXMAPFUNC(SEL_PAINT,0,SplashWindowImpl::onPaint),
};

// Macro for the class hierarchy implementation
FXIMPLEMENT(SplashWindowImpl,FXDialogBox,SplashWindowImplMap,ARRAYNUMBER(SplashWindowImplMap))

    SplashWindowImpl::SplashWindowImpl() :
        FXDialogBox(getApplication(), "", DECOR_NONE, 0,0, 320,200, 0,0,0,0, 0,0),
        icon(getApplication(),splashimg)
    {
        icon.create();
        create();
        show(PLACEMENT_SCREEN);

#ifdef WIN32
        if(QueryPerformanceFrequency(&frequency))
        {
            if(!QueryPerformanceCounter(&start))
                frequency.QuadPart = 0;
        }
        else
            frequency.QuadPart = 0;
#else
        struct timezone tz;
        gettimeofday(&start, &tz);
#endif
    }

    SplashWindowImpl::~SplashWindowImpl()
    {
        int delay = 5; // seconds
#ifdef WIN32
        delay *= 1000; // talk milliseconds
        if(frequency.QuadPart)
        {
            LARGE_INTEGER now;
            if(QueryPerformanceCounter(&now))
            {
                delay -= (int)((now.QuadPart-start.QuadPart)/(1000*frequency.QuadPart));
            }
        }
        if(delay > 0)
            Sleep(delay);
#else
        struct timezone tz;
		struct timeval now;
		gettimeofday(&now, &tz);
		start.tv_sec += delay;
		start.tv_sec -= now.tv_sec;
		start.tv_usec -= now.tv_usec;
		if(start.tv_usec < 0)
		{
			start.tv_usec += 1000000;
			start.tv_sec -= 1;
		}
		if(start.tv_sec >= 0)
		{
			struct timespec req, rem;
			req.tv_sec = start.tv_sec;
			req.tv_nsec = 1000 * start.tv_usec;
			nanosleep(&req, &rem);
		}
#endif
    }

    FXWindow * SplashWindowImpl::getWindow()
    {
        return this;
    }

    long SplashWindowImpl::onPaint(FXObject*a,FXSelector b,void*ptr)
    {
        FXDialogBox::onPaint(a,b,ptr);
        FXEvent *ev=(FXEvent*)ptr;
        FXDCWindow dc(this,ev);

        dc.drawIcon(&icon, 0, 0);

        dc.setForeground(FXRGB(192,192,192));
        for(int i = 0; i<5; ++i)
        {
            dc.drawLine(i, HEIGHT-i, i, i);
            dc.drawLine(i, i, WIDTH-i, i);
        }

        dc.setForeground(FXRGB(64,64,64));
        for(int i2=0; i2<5; ++i2)
        {
            dc.drawLine(i2, HEIGHT-1-i2, WIDTH-1-i2, HEIGHT-1-i2);
            dc.drawLine(WIDTH-1-i2, HEIGHT-1-i2, WIDTH-1-i2, i2);
        }

        dc.setForeground(FXRGB(0,0,0));
        FXString v(VERSION CTCFOX_PRODUCT_VERSION);
        drawText(&dc, v, 30, 10, 100);

        FXString c(CTCFOX_PRODUCT_COPYRIGHT);
        drawText(&dc, c, 10, 210, 310);

        return 1;
    }
    void SplashWindowImpl::drawText(FXDCWindow * dc, FXString & str, int top, int left, int right)
    {
        FXFont *font = getApplication()->getNormalFont();

        const FXchar *text = str.text();
        int l = str.length();

        int start=0, end = str.find(' ', start)+1;

        int x = left;
        int y = top + font->getFontHeight();

        dc->setFont(font);
        while(start < l)
        {
            int w = font->getTextWidth(&text[start],end+1-start);
            if((x+w) > right)
            {
                x = left;
                y += font->getFontHeight();
            }
            dc->drawText(x, y, text+start, end+1-start);
            x += w;
            start = end+1;
            end = str.find(' ', start);
            if(end < 0) end = l-1;
        }
    }

}
