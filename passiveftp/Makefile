## bare-bones makefile for g++

OBJDIR = .
OUTDIR = .

Cxxobj = g++ -c -Wall -I/usr/local/include/fox-1.1 -I/usr/X11R6/include 
Cxxs = $(Cxxobj) $< -o $@

OBJFILES =  $(OBJDIR)/FileIcon.o \
      $(OBJDIR)/FileSelector.o $(OBJDIR)/ftp.o \
      $(OBJDIR)/icons.o $(OBJDIR)/pftp.o \
      $(OBJDIR)/licences.o $(OBJDIR)/SetLanguage.o \
      $(OBJDIR)/Icon_Java.o

$(OUTDIR)/ClanGenerator: $(OBJFILES)
	g++ --mode=link -L/usr/X11R6/lib -lc -lm -lX11 -lXext -ljpeg -lpng -ltiff -o $(OUTDIR)/passiveftp $(OBJFILES) /usr/local/lib/libFOX-1.1.a


$(OBJDIR)/FileIcon.o: FileIcon.cpp
	$(Cxxs)

$(OBJDIR)/FileSelector.o: FileSelector.cpp
	$(Cxxs)

$(OBJDIR)/ftp.o: ftp.cpp
	$(Cxxs)

$(OBJDIR)/icons.o: icons.cpp
	$(Cxxs)

$(OBJDIR)/pftp.o: pftp.cpp
	$(Cxxs)

$(OBJDIR)/licences.o: licences.c
	$(Cxxs)

$(OBJDIR)/SetLanguage.o: SetLanguage.cpp
	$(Cxxs)

$(OBJDIR)/Icon_Java.o: Icon_Java.cpp
	$(Cxxs)

clean :
	rm -f $(OBJDIR)/*.o
	rm -f $(OUTDIR)/passiveftp



