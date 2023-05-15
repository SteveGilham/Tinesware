/* plaf.hpp : Pluggable look and feel class definition
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

#include "plaf.h"
#include <fx.h>

namespace FoxSkin {

    PlafBase * PlafBase::thePlaf = 0;

    void PlafBase::apply(PlafBase * lookAndFeel)
    {
    	thePlaf = lookAndFeel;
    }

    PlafBase * PlafBase::getPlaf()
    {
        return thePlaf;
    }


    template<class X> Plaf<X>::Plaf(void)
    {
        Skin<FX::FXButton, X>::override(true);
        Skin<FX::FXCheckButton, X>::override(true);
        Skin<FX::FXToggleButton, X>::override(true);
        Skin<FX::FXWindow, X>::override(true);
        Skin<FX::FXPacker, X>::override(true);
        Skin<FX::FXLabel, X>::override(true);
        Skin<FX::FXFrame, X>::override(true);
        Skin<FX::FXToolTip, X>::override(true);
        Skin<FX::FXMenuButton, X>::override(true);
        Skin<FX::FXMenuCascade, X>::override(true);
        Skin<FX::FXMenuCommand, X>::override(true);
        Skin<FX::FXMenuSeparator, X>::override(true);
        Skin<FX::FXMenuTitle, X>::override(true);
        Skin<FX::FXToolBarGrip, X>::override(true);
        Skin<FX::FXTextField, X>::override(true);
        Skin<FX::FXStatusLine, X>::override(true);
        Skin<FX::FXDragCorner, X>::override(true);
        Skin<FX::FXHeader, X>::override(true);
        Skin<FX::FXScrollBar, X>::override(true);
        Skin<FX::FXScrollCorner, X>::override(true);
        Skin<FX::FXSeparator, X>::override(true);
        Skin<FX::FXTabBar, X>::override(true);
        Skin<FX::FXTabBook, X>::override(true);
        Skin<FX::FXTabItem, X>::override(true);
        Skin<FX::FXProgressBar, X>::override(true);
    }
/*
E:\fox-1.1.13\src\FXArrowButton.cpp(323):long FXArrowButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXColorBar.cpp(187):long FXColorBar::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXColorWell.cpp(201):long FXColorWell::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXColorWheel.cpp(224):long FXColorWheel::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXDial.cpp(335):long FXDial::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXGLViewer.cpp(732):long FXGLViewer::onPaint(FXObject*,FXSelector,void*){
E:\fox-1.1.13\src\FXGradientBar.cpp(560):long FXGradientBar::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXGroupBox.cpp(164):long FXGroupBox::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXIconList.cpp(1472):long FXIconList::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXImageFrame.cpp(101):long FXImageFrame::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXImageView.cpp(134):long FXImageView::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXList.cpp(770):long FXList::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIButton.cpp(130):long FXMDIDeleteButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIButton.cpp(193):long FXMDIRestoreButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIButton.cpp(250):long FXMDIMaximizeButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIButton.cpp(300):long FXMDIMinimizeButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIButton.cpp(355):long FXMDIWindowButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMDIChild.cpp(544):long FXMDIChild::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXMenuCaption.cpp(178):long FXMenuCaption::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXOptionMenu.cpp(138):long FXOption::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXOptionMenu.cpp(398):long FXOptionMenu::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXPopup.cpp(427):long FXPopup::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXRadioButton.cpp(346):long FXRadioButton::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXRuler.cpp(209):long FXRuler::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXSlider.cpp(692):long FXSlider::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXSwitcher.cpp(81):long FXSwitcher::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXTable.cpp(1649):long FXTable::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXText.cpp(4348):long FXText::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXToolBarShell.cpp(187):long FXToolBarShell::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXToolBarTab.cpp(419):long FXToolBarTab::onPaint(FXObject*,FXSelector,void* ptr){
E:\fox-1.1.13\src\FXTreeList.cpp(1018):long FXTreeList::onPaint(FXObject*,FXSelector,void* ptr){
*/

    template<class X> Plaf<X>::~Plaf(void)
    {
        Skin<FX::FXButton, X>::override(false);
        Skin<FX::FXCheckButton, X>::override(false);
        Skin<FX::FXToggleButton, X>::override(false);
        Skin<FX::FXWindow, X>::override(false);
        Skin<FX::FXPacker, X>::override(false);
        Skin<FX::FXLabel, X>::override(false);
        Skin<FX::FXFrame, X>::override(false);
        Skin<FX::FXToolTip, X>::override(false);
        Skin<FX::FXMenuButton, X>::override(false);
        Skin<FX::FXMenuCascade, X>::override(false);
        Skin<FX::FXMenuCommand, X>::override(false);
        Skin<FX::FXMenuSeparator, X>::override(false);
        Skin<FX::FXMenuTitle, X>::override(false);
        Skin<FX::FXToolBarGrip, X>::override(false);
        Skin<FX::FXTextField, X>::override(false);
        Skin<FX::FXStatusLine, X>::override(false);
        Skin<FX::FXDragCorner, X>::override(false);
        Skin<FX::FXHeader, X>::override(false);
        Skin<FX::FXScrollBar, X>::override(false);
        Skin<FX::FXScrollCorner, X>::override(false);
        Skin<FX::FXSeparator, X>::override(false);
        Skin<FX::FXTabBar, X>::override(false);
        Skin<FX::FXTabBook, X>::override(false);
        Skin<FX::FXTabItem, X>::override(false);
        Skin<FX::FXProgressBar, X>::override(false);
    }


    template<class T, class X> long Skin<T,X>::skin(FX::FXObject*o,unsigned int sel,void*ptr)
    {
        FX::FXEvent *ev=(FX::FXEvent*)ptr;
        if(PlafBase::getPlaf() != 0)
        {
            // Get a subclass instance
            X & x = *(static_cast<X*>(PlafBase::getPlaf()));
            if(x.onSkin(this, ev))
                return 1;
        }
        return T::onPaint(o, sel, ptr);
    }

    template<class T, class X> void Skin<T,X>::override(bool set)
    {
        static typename T::FXMapEntry * oldmap = 0;
        static long (T::*oldfn)(FXObject*,FX::FXSelector, void*) = 0;

        if(!set)
        {
            if(!oldmap)
                return;
            if(!oldfn)
                return;
            oldmap->func = oldfn;
            return;
        }
        const FX::FXMetaClass * meta = &T::metaClass;
        typename T::FXMapEntry * f = reinterpret_cast<typename T::FXMapEntry *>(const_cast<void*>(meta->search(SEL_PAINT<<16)));
        if(f)
        {
            oldmap = f;
            oldfn = f->func;
            f->func = (long (T::*)(FXObject*,FX::FXSelector, void*))(&Skin<T,X>::skin);
        }
    }


}// namespace

