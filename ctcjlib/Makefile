## bare-bones makefile for g++

OBJDIR = ../../../../cbobj
OUTDIR = ../../../../cbout
JAVAHOME = /opt/jbuilder4/jdk1.3

Cobj = $(CC) -c -Wall -DSHORT_LINES -I../include \
-I../export 

Cxxobj = g++ -c -Wall -DSHORT_LINES -I../include -I../export \
-I$(JAVAHOME)/include 


Cs = $(Cobj) $< -o $@
Cux = $(OUTDIR)/dos2ux $< $@
Cxxs = $(Cxxobj) $< -o $@


OBJFILES =  $(OBJDIR)/3way.o \
      $(OBJDIR)/blowfish.o $(OBJDIR)/cast5.o \
      $(OBJDIR)/cipher.o $(OBJDIR)/des.o \
      $(OBJDIR)/des3keys.o $(OBJDIR)/des3port.o \
      $(OBJDIR)/hash.o $(OBJDIR)/hashpass.o \
      $(OBJDIR)/haval.o $(OBJDIR)/haval_i3.o \
      $(OBJDIR)/haval_i4.o $(OBJDIR)/haval_i5.o \
      $(OBJDIR)/idea.o $(OBJDIR)/md5.o \
      $(OBJDIR)/random.o $(OBJDIR)/rijndael.o \
      $(OBJDIR)/rmd160.o $(OBJDIR)/safer.o \
      $(OBJDIR)/sha.o $(OBJDIR)/square.o  \
      $(OBJDIR)/tea.o $(OBJDIR)/twofish.o \
      $(OBJDIR)/bits.o $(OBJDIR)/compand.o \
      $(OBJDIR)/deflate.o $(OBJDIR)/inflate.o \
      $(OBJDIR)/splay.o $(OBJDIR)/trees.o \
      $(OBJDIR)/ziputils.o $(OBJDIR)/keywords.o \
      $(OBJDIR)/keyhash.o $(OBJDIR)/keyio.o \
      $(OBJDIR)/keyutils.o $(OBJDIR)/armour.o \
      $(OBJDIR)/cleave.o $(OBJDIR)/ctc.o \
      $(OBJDIR)/uuencode.o $(OBJDIR)/ec_crypt.o \
      $(OBJDIR)/ec_curve.o $(OBJDIR)/ec_field.o \
      $(OBJDIR)/pkautils.o $(OBJDIR)/pkbignum.o \
      $(OBJDIR)/pkcipher.o $(OBJDIR)/pkops.o \
      $(OBJDIR)/rsa.o $(OBJDIR)/bignums.o $(OBJDIR)/licences.o \
      $(OBJDIR)/utils.o  $(OBJDIR)/widechar.o \
      $(OBJDIR)/cjcb_act.o \
      $(OBJDIR)/cjcb_info.o $(OBJDIR)/cjport_io.o \
      $(OBJDIR)/ctcjava_CJctclib.o \
      $(OBJDIR)/ctcjava_CJdecodeContext.o \
      $(OBJDIR)/ctcjava_CJencryptInsts.o \
      $(OBJDIR)/ctcjava_CTCIKeyConst.o \
      $(OBJDIR)/ctcjava_CTCJava.o \
      $(OBJDIR)/ctcjava_CTCJLicenceDialog.o \
      $(OBJDIR)/ctcjava_CJFilesPanel.o \
      $(OBJDIR)/ctcjava_CJPubkey.o \
      $(OBJDIR)/rawrand.o $(OBJDIR)/usrbreak.o \
      $(OBJDIR)/utfcode.o


$(OUTDIR)/libctcjlib.so: $(OBJFILES)
	gcc -shared -lstdc++-3-libc6.1-2-2.10.0 -o $(OUTDIR)/libctcjlib.so $(OBJFILES)

$(OUTDIR)/dos2ux: ../../dos2ux.cpp
	$(CC) ../../dos2ux.cpp -o $@

$(OBJDIR)/3way.o: ../ctcalg/3way.c ../ctcalg/3way.h ../include/utils.h
	$(Cs)

$(OBJDIR)/blowfish.o: ../ctcalg/blowfish.c  ../ctcalg/blowfish.h ../include/utils.h
	$(Cs)

$(OBJDIR)/cast5.o: ../ctcalg/cast5.c ../ctcalg/cast5.h ../include/utils.h
	$(Cs)

$(OBJDIR)/cipher.o: ../ctcalg/cipher.c ../include/utils.h ../export/keyconst.h ../include/cipher.h \
       ../ctcalg/cast5.h ../ctcalg/des.h ../ctcalg/idea.h ../ctcalg/3way.h ../ctcalg/blowfish.h \
       ../ctcalg/tea.h ../ctcalg/square.h ../ctcalg/safer.h ../ctcalg/rijndael.h ../ctcalg/twofish.h
	$(Cs)

$(OBJDIR)/des.o: ../ctcalg/des.c ../ctcalg/des.h ../ctcalg/des3_c.h ../include/utils.h
	$(Cs)

$(OBJDIR)/des3keys.o: ../ctcalg/des3keys.c ../ctcalg/des3_c.h
	$(Cs)

$(OBJDIR)/des3port.o: ../ctcalg/des3port.c ../ctcalg/des3_c.h
	$(Cs)

$(OBJDIR)/hash.o: ../ctcalg/hash.c ../include/utils.h ../export/keyconst.h ../include/hash.h  \
                  ../ctcalg/3way.h ../ctcalg/md5.h ../ctcalg/sha.h ../ctcalg/haval.h  ../ctcalg/rmd.h
	$(Cs)

$(OBJDIR)/hashpass.o: ../ctcalg/hashpass.c ../include/hash.h ../export/hashpass.h ../export/widechar.h
	$(Cs)

$(OBJDIR)/haval.o: ../ctcalg/haval.c ../export/keyconst.h ../ctcalg/haval_i.h_ux ../include/utils.h
	$(Cs)

$(OBJDIR)/haval_i3.o: ../ctcalg/haval_i3.c ../ctcalg/haval_i.h_ux ../include/utils.h
	$(Cs)

$(OBJDIR)/haval_i4.o: ../ctcalg/haval_i4.c ../ctcalg/haval_i.h_ux ../include/utils.h
	$(Cs)

$(OBJDIR)/haval_i5.o: ../ctcalg/haval_i5.c ../ctcalg/haval_i.h_ux ../include/utils.h
	$(Cs)

$(OBJDIR)/idea.o: ../ctcalg/ux_idea.c  ../ctcalg/idea.h ../include/utils.h
	$(Cs)

../ctcalg/ux_idea.c: ../ctcalg/idea.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/md5.o: ../ctcalg/ux_md5.c  ../ctcalg/md5.h ../include/utils.h
	$(Cs)

../ctcalg/ux_md5.c: ../ctcalg/md5.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/random.o: ../ctcalg/random.c ../export/rawrand.h ../include/random.h ../ctcalg/md5.h
	$(Cs)

$(OBJDIR)/rijndael.o: ../ctcalg/ux_rijndael.c  ../ctcalg/rijndael.h ../include/utils.h
	$(Cs)

../ctcalg/ux_rijndael.c: ../ctcalg/rijndael.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/rmd160.o: ../ctcalg/ux_rmd160.c  ../ctcalg/rmd.h ../include/utils.h
	$(Cs)

../ctcalg/ux_rmd160.c: ../ctcalg/rmd160.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/safer.o: ../ctcalg/safer.c ../ctcalg/safer.h ../include/utils.h
	$(Cs)

$(OBJDIR)/sha.o: ../ctcalg/ux_sha.c ../ctcalg/sha.h ../include/utils.h
	$(Cs)

../ctcalg/ux_sha.c: ../ctcalg/sha.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/square.o: ../ctcalg/square.c ../ctcalg/square.h ../include/utils.h
	$(Cs)

$(OBJDIR)/tea.o: ../ctcalg/tea.c ../ctcalg/tea.h ../include/utils.h
	$(Cs)

$(OBJDIR)/twofish.o: ../ctcalg/ux_twofish.c  ../ctcalg/twofish.h
	$(Cs)

../ctcalg/ux_twofish.c: ../ctcalg/twofish.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/bits.o: ../ctccpa/bits.c ../ctccpa/tailor.h ../ctccpa/gzip.h_ux
	$(Cs)

$(OBJDIR)/compand.o: ../ctccpa/compand.c ../include/utils.h ../include/compand.h \
                     ../ctccpa/splay.h ../ctccpa/ziputils.h ../export/keyconst.h
	$(Cs)

$(OBJDIR)/deflate.o: ../ctccpa/ux_deflate.c  ../ctccpa/tailor.h ../ctccpa/gzip.h_ux ../ctccpa/lzw.h
	$(Cs)

../ctccpa/ux_deflate.c: ../ctccpa/deflate.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/inflate.o: ../ctccpa/inflate.c ../ctccpa/tailor.h ../include/utils.h ../export/usrbreak.h \
                     ../ctccpa/gzip.h_ux
	$(Cs)

$(OBJDIR)/splay.o: ../ctccpa/splay.c ../ctccpa/splay.h ../include/utils.h ../export/usrbreak.h
	$(Cobj) ../ctccpa/splay.c -o $@

$(OBJDIR)/trees.o: ../ctccpa/ux_trees.c ../ctccpa/tailor.h ../ctccpa/gzip.h_ux
	$(Cs)

../ctccpa/ux_trees.c: ../ctccpa/trees.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/ziputils.o: ../ctccpa/ziputils.c ../ctccpa/splay.h ../include/utils.h ../export/usrbreak.h  \
                      ../ctccpa/tailor.h   ../ctccpa/gzip.h_ux ../export/keyconst.h ../export/port_io.h \
                      ../ctccpa/ziputils.h
	$(Cs)

$(OBJDIR)/keywords.o: ../ctcfyi/keywords.c ../export/keyconst.h ../export/callback.h ../include/pkautils.h
	$(Cs)	

$(OBJDIR)/keyhash.o: ../ctckey/keyhash.c ../include/bignums.h ../export/callback.h ../include/hash.h \
                     ../include/keyio.h ../include/keyutils.h ../export/keyhash.h ../export/port_io.h \
                     ../include/utils.h ../include/pkcipher.h ../export/usrbreak.h
	$(Cs)

$(OBJDIR)/keyio.o: ../ctckey/keyio.c ../include/bignums.h ../export/callback.h ../include/hash.h \
                   ../include/keyio.h ../include/keyutils.h ../export/keyhash.h ../export/port_io.h \
                   ../include/utils.h ../include/pkcipher.h
	$(Cs)

$(OBJDIR)/keyutils.o: ../ctckey/keyutils.c  ../include/bignums.h ../include/hash.h \
                      ../include/keyutils.h ../export/keyhash.h ../export/hashpass.h \
                      ../include/utils.h ../include/pkcipher.h ../export/usrbreak.h \
                      ../include/random.h ../export/ctc.h
	$(Cs)

$(OBJDIR)/armour.o: ../ctclib/armour.c ../export/armour.h ../export/usrbreak.h ../export/port_io.h \
                    ../ctclib/uuencode.h ../export/widechar.h
	$(Cs)

$(OBJDIR)/cleave.o: ../ctclib/cleave.c ../export/cleave.h ../export/port_io.h ../export/abstract.h
	$(Cs)

$(OBJDIR)/ctc.o: ../ctclib/ctc.c ../export/armour.h ../include/bignums.h ../include/compand.h \
                 ../include/hash.h ../include/keyio.h ../include/keyutils.h ../export/port_io.h \
                 ../export/ctc.h ../include/pkcipher.h ../include/random.h ../include/utils.h \
                 ../export/widechar.h
	$(Cs)

$(OBJDIR)/uuencode.o: ../ctclib/uuencode.c ../ctclib/uuencode.h ../export/usrbreak.h \
                      ../export/armour.h ../export/port_io.h
	$(Cs)

$(OBJDIR)/ec_crypt.o: ../ctcpka/ec_crypt.c ../include/bignums.h ../export/callback.h ../ctcpka/ec_curve.h \
                      ../ctcpka/ec_crypt.h ../include/pkautils.h ../include/random.h ../include/pkcipher.h
	$(Cs)

$(OBJDIR)/ec_curve.o: ../ctcpka/ec_curve.c ../ctcpka/ec_curve.h ../ctcpka/ec_field.h ../ctcpka/ec_param.h
	$(Cs)

$(OBJDIR)/ec_field.o: ../ctcpka/ux_ec_field.c ../include/bignums.h ../ctcpka/ec_field.h \
                      ../ctcpka/ec_param.h ../include/utils.h ../export/usrbreak.h
	$(Cs)

../ctcpka/ux_ec_field.c: ../ctcpka/ec_field.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/pkautils.o: ../ctcpka/pkautils.c ../include/bignums.h ../include/pkautils.h \
                      ../include/hash.h ../export/hashpass.h ../include/pkcipher.h \
                      ../include/random.h ../export/usrbreak.h ../include/utils.h
	$(Cs)

$(OBJDIR)/pkbignum.o: ../ctcpka/pkbignum.c ../include/random.h ../include/bignums.h ../export/usrbreak.h \
                      ../include/utils.h ../ctcpka/pkbignum.h
	$(Cs)

$(OBJDIR)/pkcipher.o: ../ctcpka/pkcipher.c ../include/bignums.h ../include/hash.h ../include/keyio.h \
                      ../include/pkautils.h ../include/pkcipher.h ../export/port_io.h ../include/utils.h
	$(Cs)

$(OBJDIR)/pkops.o: ../ctcpka/pkops.c ../include/hash.h ../include/random.h ../include/bignums.h ../ctcpka/pkbignum.h \
                   ../ctcpka/pkops.h ../export/callback.h ../include/utils.h ../include/pkcipher.h \
                   ../export/keyhash.h ../ctcpka/ec_crypt.h ../ctcpka/rsa.h
	$(Cs)

$(OBJDIR)/rsa.o: ../ctcpka/rsa.c ../include/random.h ../ctcpka/pkops.h ../ctcpka/rsa.h ../include/bignums.h \
                 ../ctcpka/pkbignum.h ../export/callback.h ../include/pkcipher.h ../export/keyhash.h
	$(Cs)

$(OBJDIR)/bignums.o: ../ctcutl/ux_bignums.c ../include/bignums.h ../export/usrbreak.h ../include/utils.h
	$(Cs)

../ctcutl/ux_bignums.c: ../ctcutl/bignums.c $(OUTDIR)/dos2ux	
	$(Cux)

$(OBJDIR)/licences.o: ../ctcutl/licences.c ../export/licences.h
	$(Cs)

$(OBJDIR)/utils.o: ../ctcutl/utils.c ../include/utils.h ../export/usrbreak.h
	$(Cs)

$(OBJDIR)/widechar.o: ../ctcutl/widechar.c ../export/widechar.h ../export/basic.h
	$(Cs)

$(OBJDIR)/cjcb_act.o: cjcb_act.cpp
	$(Cxxs)

$(OBJDIR)/cjcb_info.o: cjcb_info.cpp ctcjlib.h utfcode.h ../export/callback.h
	$(Cxxs)

$(OBJDIR)/cjport_io.o: cjport_io.cpp ctcjlib.h ../export/port_io.h ../include/random.h utfcode.h \
                       ../export/widechar.h ../export/usrbreak.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CJFilesPanel.o: ctcjava_CJFilesPanel.cpp com_ravnaandtines_ctcjava_CJFilesPanel.h \
         ../export/abstract.h ../export/ctc.h ../include/pkautils.h ../export/port_io.h \
         ../export/keyhash.h utfcode.h ctcjlib.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CJPubkey.o: ctcjava_CJPubkey.cpp com_ravnaandtines_ctcjava_CJPubkey.h \
         com_ravnaandtines_ctcjava_CJSeckey.h com_ravnaandtines_ctcjava_Username.h \
         com_ravnaandtines_ctcjava_Signature.h ../export/ctc.h ctcjlib.h utfcode.h \
         ../export/port_io.h ../export/callback.h ../export/keyhash.h ../include/keyutils.h \
         ../include/utils.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CJctclib.o: ctcjava_CJctclib.cpp com_ravnaandtines_ctcjava_CJctclib.h ctcjlib.h \
         ../export/ctc.h ../export/port_io.h utfcode.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CJdecodeContext.o: ctcjava_CJdecodeContext.cpp com_ravnaandtines_ctcjava_CJdecodeContext.h \
         ../export/ctc.h ctcjlib.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CJencryptInsts.o: ctcjava_CJencryptInsts.cpp ../export/armour.h ../include/cipher.h \
         ctcjlib.h ../export/ctc.h com_ravnaandtines_ctcjava_CJencryptInsts.h ../export/keyconst.h utfcode.h
	$(Cxxobj) ctcjava_CJencryptInsts.cpp -o $@

$(OBJDIR)/ctcjava_CTCIKeyConst.o: ctcjava_CTCIKeyConst.cpp com_ravnaandtines_ctcjava_CTCIKeyConst.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CTCJLicenceDialog.o: ctcjava_CTCJLicenceDialog.cpp com_ravnaandtines_ctcjava_CTCJLicenceDialog.h \
        ../export/basic.h ../export/licences.h utfcode.h
	$(Cxxs)

$(OBJDIR)/ctcjava_CTCJava.o: ctcjava_CTCJava.cpp com_ravnaandtines_ctcjava_CTC.h ctcjlib.h
	$(Cxxs)

$(OBJDIR)/rawrand.o: rawrand.cpp ../export/rawrand.h ctcjlib.h ../export/port_io.h ../export/keyconst.h \
         ../include/hash.h ../ctcalg/md5.h ../include/utils.h
	$(Cxxobj) $< -I../ctcalg -o $@


$(OBJDIR)/usrbreak.o: usrbreak.cpp ctcjlib.h utfcode.h
	$(Cxxs)


$(OBJDIR)/utfcode.o: utfcode.cpp utfcode.h
	$(Cxxs)

../ctcalg/3way.h: ../export/basic.h

../ctcalg/blowfish.h: ../export/basic.h

../ctcalg/cast5.h: ../export/basic.h

../ctcalg/des.h: ../export/basic.h

../ctcalg/des3_c.h: ../ctcalg/des.h ../export/basic.h

../ctcalg/haval_i.h_ux: ../ctcalg/haval_i.h $(OUTDIR)/dos2ux
	$(Cux)

../ctcalg/haval.h: ../export/abstract.h

../ctcalg/haval_i.h: ../export/abstract.h

../ctcalg/idea.h: ../export/basic.h

../ctcalg/md5.h: ../export/basic.h

../ctcalg/rijndael.h: ../export/basic.h

../ctcalg/rmd.h: ../export/basic.h

../ctcalg/safer.h: ../export/basic.h

../ctcalg/sha.h: ../export/basic.h

../ctcalg/square.h: ../export/basic.h

../ctcalg/tea.h: ../export/basic.h

../ctcalg/twofish.h: ../export/basic.h

../ctccpa/gzip.h_ux: ../ctccpa/gzip.h $(OUTDIR)/dos2ux
	$(Cux)

../ctccpa/lzw.h: ../ctccpa/gzip.h_ux

../ctccpa/splay.h: ../export/abstract.h

../ctccpa/ziputils.h: ../export/abstract.h

../ctclib/uuencode.h: ../export/abstract.h

../ctcpka/ec_curve.h: ../ctcpka/ec_field.h ../include/bignums.h

../ctcpka/ec_field.h: ../ctcpka/ec_param.h ../export/abstract.h

../ctcpka/pkbignum.h: ../include/pkcipher.h ../export/abstract.h

../ctcpka/pkops.h: ../export/abstract.h

../ctcpka/rsa.h: ../export/abstract.h

../export/abstract.h: ../export/basic.h

../export/armour.h: ../export/abstract.h

../export/callback.h: ../export/abstract.h

../export/ctc.h: ../export/keyhash.h ../export/callback.h ../include/cipher.h

../export/hashpass.h: ../export/abstract.h

../export/keyconst.h: ../export/basic.h

../export/keyhash.h: ../export/abstract.h ../export/keyconst.h

../export/port_io.h: ../export/abstract.h

../export/rawrand.h: ../export/basic.h

../export/widechar.h: ../export/basic.h

../include/bignums.h: ../export/abstract.h

../include/cipher.h: ../export/abstract.h

../include/compand.h: ../export/port_io.h

../include/hash.h: ../export/abstract.h

../include/keyio.h: ../export/abstract.h ../export/keyconst.h ../include/pkcipher.h

../include/keyutils.h: ../export/abstract.h ../include/pkautils.h ../export/keyconst.h ../export/keyhash.h

../include/pkautils.h: ../include/cipher.h ../export/keyconst.h

../include/pkcipher.h: ../export/abstract.h ../include/pkautils.h

../include/random.h: ../export/basic.h

../include/utils.h: ../export/basic.h

.PHONY : clean

clean :
	rm -f $(OBJDIR)/*.o
	rm -f $(OUTDIR)/libctcjlib.so
	rm -f $(OUTDIR)/dos2ux



