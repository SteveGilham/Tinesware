Tines' dice v1.1
================

Licence:
========
Copyright © 2002 Mr. Tines <tines@ravnaandtines.com>

This program may be freely redistributed or amended, 
but must retain the attribution above, and amended
versions must be clearly marked as such.

THERE IS NO WARRANTY.

Manifest:
=========
dice.lua - source in MS-DOS text format
dice_lua.pdb - source in Palm AportisDoc (DOC) format
Readme.txt - this file
Tines'_dice.PRC - the compiled Plua program 


Installation:
=============
You will need the Plua runtime to run this program.

The latest version should be available at the 
Yahoo Plua discussion group at

http://groups.yahoo.com/groups/plua

or via the Plua home page at

http://netpage.uai.com.br/mmand/plua.htm

As Plua uses MathLib for some of its mathematical 
functions, you may need to seek that out and 
install it.  If you haven't got it already, you'll
likely need it at some point for other apps.

IMPORTANT If you have installed v1.0, you should first 
========= delete that from your PDA (either though the 
launcher, or a utility like Filez or Tom's Catalog).  
This is due to a problem to do with case sensitivity 
of file and program names - installing v1.1 with v1.0
present, the new version will just be masked by rather 
than replace the v1.0 executable.

Install any you haven't already got out of the 
	- the Plua runtime (or IDE), 
	- MathLib and 
	- the Tines'_Dice.PRC file.  
You can now run the dice program.

Use:
====
The format of the input part of the screen is

  __ D __ [+] __ [+] __ D __

 [Roll D&D] [Roll RQ] [Roll Fvlminata]

  __ d6 [+0|+1|+1/2] [KA]

 [Roll Hero] [Roll SRun] [Roll Vampire]

The last two text fields are there for results
to be displayed.

Blank fields in the input part, or ones that aren't 
positive numbers, are treated as zero.  The input 
fields are clear on starting the program, but are not 
cleared by pressing any of the buttons, so you can just 
keep pressing the button you need if the rolls stay the same.

1) D&D or similar games
-----------------------
The top row is interpreted as 

 nDm + x + yDz

If one of the "+" buttons is pressed, it toggles to a 
"-" (and pressing again toggles it back).  If either
button is now "-", then the matching plus is changed 
to a minus.  To roll 2D6-1, for example, fill in the 
fields with 2,6 and 1, and toggle the first "+" button
to show a "-".

Pressing the [Roll D&D] button gives a d20 result and 
the result of the roll from the top line (so you get a 
to-hit and damage in one operation).

2) RQ or similar games
----------------------
The top row is interpreted as before - so if you have a 
bastard sword (d10+1), bladesharp 2, and a 1d6 damage add, 
key in 1,10,3,1,6, and make sure the buttons both show "+".

Pressing the [Roll RQ] button gives a d% and d20 result 
(to hit and hit location), plus the result of the roll.  
The second line also give the result of a special hit 
(slash, crush or impale) as per the RQ2 rules.

3) Hero
-------
Fill in the number of d6 in the 3rd row.  If it's a damage 
value with a +1 or +1/2D6, set the appropriate button, and 
if it's a killing attack, check the checkbox.  

Pressing the [Roll Hero] button gives a 3D6 to-hit roll,
a 3D6 hit location roll, and the stun and body.  You could
even press this one into service for GURPS, but you'll have
to do other adds than +1 or +0 by yourself.

4) Shadowrun
------------
Fill in the number of d6 in the skill in the 3rd row,
and ignore the other buttons in the row.

Pressing the [Roll SRun] button gives the dice results
in order from the top, with the roll-up applied to sixes.

5) Storyteller
--------------
Fill in the number of dots in the skill in the first
input line in the 3rd row, and ignore everything else in 
the row (especially the bit about d6).

Pressing the [Roll Vampire] button gives the dice results
(that many separate d10s) in order from the top.  You'll
have to figur eyour own botches.  You might be able to
press this into service for Ars Magica, but it doesn't
handle the odd re-roll and add rolls.  You might also be 
able to use this for Godlike.

6) Fvlminata
------------

The first row shows a hand of the tali and their sum,
the second a separate hand and its meaning (Senio, Venus,
Vultures, pair, pairs or three of a kind).  The Roman numerals 
are shoen for the raw die tolls (the result sum is in normal 
Arabic numerals).  The program doesn't support re-rolling 
partial hands explicitly - to do this either remember which 
positions (or simply how many) you want to change, and record 
the ones kept and add in the appropriate positions or numbers 
in a new roll.  I'd suggest using the effect roll from the
re-roll, for obvious reasons.

Note:
=====
This program shares the CreatorID AngL with the data files 
produced by my PDABriefcase application (and the now defunct
CTClib for the Palm project), so uninstalling this program 
via the main launcher window will delete any briefcase files
present.  Use FileZ or Tom's Catalog or Zarf Catalog or 
similar to be selective.  It's OK to just delete just the 
app file as this program does not write anything to any 
databases.



 