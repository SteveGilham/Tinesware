/* cb_info.cpp : Defines the callback abstractions for the application.
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

#include <memory>
#include "ctcfox.h"
#include "callback.h"
#include "../strings/strings.h"
#include <cstdio>
#include <fx.h>
#include "keyhash.h"
#include "pkautils.h"

#include "ctc.h"
#include "HandledList.h"
#include "Registrar.h"
#include "keytree.h"

namespace CTClib {

/* These should both also expand code and context */
extern "C" continue_action cb_exception(cb_condition * condition)
{
    // This routine is called only from ctc.c and is believed complete
    int code = (condition->severity<<24)
        |(condition->module<<16)
            |(condition->code << 8)
                |(condition->context); // assumed all in the 0-255 range

    const char * kode = CTCFox::Strings::getCBExceptCodes(code, 
        CTClib::revocation(condition->pub_key) == 0);

    const char * question = CTCFox::Strings::getCBExceptQuestions(code);

    char fallback[16];

    if(0 == kode)
    {
        std::sprintf(fallback, "0x%08x", code);
        kode = fallback;
    }

    FXString message(kode);
    if(condition->text) 
    {
        message += "\n";
        message += condition->text;
    }
    // TODO add keyID if set
    message += "\n";
    message += question;

    FXuint result = 
        FXMessageBox::question(getMainWindow(),MBOX_YES_NO,"CTCFox",message.text());

    return result == MBOX_CLICKED_YES
        ? CB_CONTINUE
        : CB_ABORT;
}

extern "C" void cb_information(cb_condition * condition)
{
    uint32_t code = (condition->module<<16)
            |(condition->code << 8)
                |(condition->context); // assumed all in the 0-255 range

    //void * key = condition->pub_key;


    // String kode = Integer.toHexString(code);

    bool stickup = false;
    const char * kode = CTCFox::Strings::getCBInfoCodes(code, condition->severity);

    if(0x011901 == code 
        || 0 == kode) stickup = true;

    char fallback[16];

    if(0 == kode)
    {
        std::sprintf(fallback, "0x%02x%06x", condition->severity, (unsigned int)code);
        kode = fallback;
    }

    if((CTClib::CB_INFO <= condition->severity) && !stickup)
    {
        FXString tagline(CTCFox::Strings::libEnumName(
            CTClib::CB_SEVERITY, condition->severity, false));
        tagline += ": ";
        tagline += kode;
        if(condition->text) 
        {
            tagline += " ";
            tagline += condition->text;
        }
        // TODO add keyID if set

        getStatusLine()->setText(tagline);
        return;
    }

    FXString caption("CTCFox ");
    caption += CTCFox::Strings::libEnumName(
            CTClib::CB_SEVERITY, condition->severity, false);

    FXString message(CTCFox::Strings::libEnumName(
            CTClib::CB_MODULE, condition->module, true));
    message += "\n";
    message += CTCFox::Strings::libEnumName(
            CTClib::CB_CONTEXT, condition->context, true);
    message += "\n";
    message += kode;

    if(condition->text) 
    {
        message += "\n";
        message += condition->text;
    }
    // TODO add keyID if set


    switch(condition->severity)
    {
    case CTClib::CB_INFO:
    case CTClib::CB_STATUS:
        FXMessageBox::information(getMainWindow(),MBOX_OK,caption.text(),message.text());
        break;
    case CTClib::CB_WARNING:
        FXMessageBox::warning(getMainWindow(),MBOX_OK,caption.text(),message.text());
        break;
    case CTClib::CB_CRASH:
    case CTClib::CB_FATAL:
    case CTClib::CB_ERROR:
    default:
        FXMessageBox::error(getMainWindow(),MBOX_OK,caption.text(),message.text());
        break;
    }
}

extern "C" void bug_check(char * text)
{
    FXMessageBox::error(getMainWindow(),MBOX_OK,"CTClib fault - please report",text);
    getApplication()->stop();
}

extern "C" char * enumName(cb_enumeration enumeration, int value, boolean full)
{
    return const_cast<char*>(CTCFox::Strings::libEnumName(enumeration, value, full!=0));
}

extern "C" int user_break(void)    /* returns true if the user has requested operation abort */
{
    return 0;
}

//class 

extern "C" int cb_need_key(seckey * keys[], int Nkeys)
{
    FXDialogBox about(getMainWindow(),
        CTCFox::Strings::load(CTCFox::Strings::ENTER_PASSPHRASE),
        DECOR_TITLE|DECOR_BORDER,0,0,0,0, 0,0,0,0, 0,0);

    // Contents
    std::auto_ptr<FXVerticalFrame> contents(new 
        FXVerticalFrame(&about,LAYOUT_SIDE_RIGHT|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));
  

    char password[256];
    FXRadioButton ** keybuttons = NULL;
    FXGroupBox * box = NULL;
    FXLabel * key = NULL;
    FXLabel * onekey = NULL;

#define MIN 1
    if(Nkeys > MIN)
    {
        box = new FXGroupBox(contents.get(), CTCFox::Strings::load(CTCFox::Strings::KEY_COLON),
            LAYOUT_SIDE_TOP|FRAME_GROOVE|LAYOUT_FILL_X|LAYOUT_FILL_Y);
        keybuttons = new FXRadioButton *[Nkeys];
        for(int i=0; i<Nkeys; ++i)
        {
            name_from_key(publicKey(keys[i]), password);
            keybuttons[i] = new FXRadioButton(box, password);
        }
#if MIN == 0
        if(Nkeys == 1)
            FXRadioButton * extra = new FXRadioButton(box, "dummy");
#endif
        keybuttons[0]->setCheck();
    }
    else
    {
        key = new FXLabel(contents.get(), CTCFox::Strings::load(CTCFox::Strings::KEY_COLON));
        name_from_key(publicKey(keys[0]), password);
        onekey = new FXLabel(contents.get(), password);
    }

    // TODO textfield takes return as OK
    std::auto_ptr<FXTextField> pwd(new
        FXTextField(contents.get(), 60, NULL, 0, TEXTFIELD_PASSWD|FRAME_SUNKEN|FRAME_THICK));

    std::auto_ptr<FXLabel> sum(new 
        FXLabel(contents.get(), CTCFox::Strings::load(CTCFox::Strings::CHECKSUM_COLON)));

    std::auto_ptr<FXHorizontalFrame> t2_contents(new 
      FXHorizontalFrame(contents.get(),LAYOUT_SIDE_TOP|FRAME_NONE|LAYOUT_FILL_X|LAYOUT_FILL_Y));

    // OK button
    FXButton *button=new FXButton(t2_contents.get(),CTCFox::Strings::load(CTCFox::Strings::OK),
        NULL,&about,FXDialogBox::ID_ACCEPT,BUTTON_INITIAL|BUTTON_DEFAULT|FRAME_RAISED|FRAME_THICK|LAYOUT_LEFT,
        0,0,0,0,32,32,2,2);

    // Cancel button
    button=new FXButton(t2_contents.get(),CTCFox::Strings::load(CTCFox::Strings::CANCEL),
        NULL,&about,FXDialogBox::ID_CANCEL,FRAME_RAISED|FRAME_THICK|LAYOUT_RIGHT,
        0,0,0,0,32,32,2,2);

    int on;
    for(int go=0; go<3; ++go)
    {
        pwd->setFocus();
        FXuint what = about.execute(PLACEMENT_OWNER);
        on = -1;
        if(!what)
            break;

        if(Nkeys > MIN)
        {
            for(int i=0; i<Nkeys; ++i)
            {
                if(keybuttons[i]->getCheck() == TRUE)
                    on = i;
            }
        }
        else
            on = 0;

        // TODO - passphrase checksum
        // TODO - don't copy, reference
        if(on >= 0 && internalise_seckey(keys[on], pwd->getText().text()))
        {
            //TODO - wipe passphrase
            getTreeRoot()->logUnlocked(keys[on]);
            break;
        }
        //TODO - wipe passphrase
        if(go < 2)
        {
            FXMessageBox::error(getMainWindow(),MBOX_OK,CTCFox::Strings::load(CTCFox::Strings::ENTER_PASSPHRASE),
                CTCFox::Strings::load(CTCFox::Strings::BADPHRASE));
        }
        else
        {
            FXMessageBox::error(getMainWindow(),MBOX_OK,CTCFox::Strings::load(CTCFox::Strings::ENTER_PASSPHRASE),
                CTCFox::Strings::load(CTCFox::Strings::OUTOFTIME));
        }
    }

    if(Nkeys > MIN)
    {
        for(int i=0; i<Nkeys; ++i)
        {
            delete keybuttons[i];
        }
        delete [] keybuttons;
        delete box;
    }
    else
    {
        delete key;
        delete onekey;
    }

    return on;
}

extern "C" boolean cb_convKey(cv_details *details, void **key, size_t *keylen)
{
    return false;
}

extern "C" void cb_result_file(DataFileP results, cb_details * details)
{
}

extern "C" DataFileP cb_getFile(cb_filedesc * filedesc)
{
    switch(filedesc->purpose)
    {
    case SPLITKEY: // we are about to dump a session key
        return  getTreeRoot()->getWritable();

        /*
    case CYPHERTEXTFILE: // we want to use the session key
        return getInputFile(filedesc, "Open cyphertext...");

    case SIGNEDFILE: // we want to apply the signature
        {
            DataFileP grab = getInputFile(filedesc, "Open file...");
            if(grab && BINCYPHER != filedesc->file_type)
            {
                // need to get text into canonical form
                DataFileP canon = vf_tempfile(vf_length(grab));
                vf_CCmode(CANONICAL, canon);
                copyTFile(grab, canon);
                vf_close(grab);
                grab = canon;
            }
            return grab;
        }
        */
    default:
        bug_check("cb_getFile: default branch taken.");
    }
    /* this is a place-holder*/
    return NULL;
}


} //end namespace
