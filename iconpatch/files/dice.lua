
-- Tines' dice v 1.0

-- Copyright © 2002 Mr. Tines 
-- <tines@ravnaandtines.com>

-- This program may be freely redistributed
-- or amended, but must retain the
-- attribution above, and amended versions
-- must be clearly marked as such

-- THERE IS NO WARRANTY.

-- core n.dm roll
ndm = function(n, m)
  local v, nn v = 0 nn = n
  while nn > 0 do
    v = v + random(m) 
    nn=nn-1
  end
  return v
end

-- get numbers from text fields and do ndm
ndm2 = function(idn, idm)
  local vn, vm
  vn = tonumber(pgettext(idn))
  vm = tonumber(pgettext(idm))
  if not vn or not vm 
    then return 0,0 end
  if vn <= 0 or vm <= 0
    then return 0,0 end
  return ndm(vn, vm), vn*vm
end

-- popup surrogate for +/-
qtoggle = function(nid)
  if "-" == pgettext(nid)
   then psettext(nid, "+")
   else psettext(nid, "-")
  end
end
qstate = function(nid)
  if "-" == pgettext(nid)
   then return 1
   else return 0
  end
end

-- ndm + add + n'dm'
rollgrp = function ()
  local vadd, v1, v2, v3,v4, v5
  v1,v4=ndm2(dn,dm)
  vadd = tonumber(pgettext(dadd))
  if not vadd
    then vadd = 0 end
-- pop-up surrogate
  if 1 ==qstate(dminus) 
    then vadd=0-vadd end
  v2=vadd
  v3,v5 = ndm2(dn2,dm2)
-- pop-up surrogate
  if 1 == qstate(dminus2) 
    then v3=0-v3 v5=0-v5 end
  return v1,v2,v3,v4,v5
end

-- group and d20
rolldnd = function ()
  local a,n,i a,n,i = rollgrp()
  a=a+n+i
  psettext(out,"d20="..ndm(1,20)
    .." roll="..a)
  psettext(out2,"")
end

-- group, d%, d20, specials
rollrq = function()
  local a,n,i,h,s,c,o, r
  a,n,h,o,r = rollgrp()
  a=a+n+h  i=a+o  c=a+r
  s=a+ndm2(dn,dm)

  psettext(out,"d%="..ndm(1,100)
    .." d20="..ndm(1,20).." roll="..a)
  psettext(out2," slash="..s.." crush="
    ..c.." impale="..i)
end

-- Hero style to hit and effect
rollch = function ()
  local vn, s, b, d s=0 b=0
  vn = tonumber(pgettext(dch))
  if not vn
    then vn = 0 end
-- roll dice here
  while vn>0 do
    d = random(6)
    if 6 == d 
      then b=b+2 
    elseif d>1 
      then b=b+1 end
    s=s+d vn=vn-1
  end -- while
-- extras
  if 1== pgetstate(dp1)
    then s=s+1 
  elseif 1 == pgetstate(dph)
    then vn=random(3)
      s=s+vn
      if vn==3
        then b=b+1 end
  end
-- KA Stun
  if 1 == pgetstate(dka)
    then   b = s vn=random(6)
       if 6==vn
        then vn = 1 end
    s = b*vn
  end
  vn = ndm(3,6) d=ndm(3,6)
  psettext(out,"hit="..vn.." loc="..d..
    " stun="..s.." body="..b)
  psettext(out2,"")
end

-- Srun exploding d6 roll
d6sr = function()
  local m,n  m=0  n=random(6)
  while n == 6 do
    m=m+n
    n=random(6)
  end
  m=m+n
  return m
end

-- Srun ordered list of exploding d6s
rollsr = function ()
  local vn, t, s t={}
  vn = tonumber(pgettext(dch))
  if not vn
    then vn=0 end
  while vn >0 do
    t[vn]=d6sr() vn=vn-1
  end -- while
  sort(t)
  s="" vn=getn(t)
  while vn > 0 do
    s=s..t[vn]
    if vn > 1
     then s=s.."," end
    vn=vn-1
  end
  psettext(out,s) psettext(out2,"")
end

-- Storyteller system ordered d10s
rollvtm = function ()
  local vn, t, s t={}
  vn = tonumber(pgettext(dch))
  if not vn
    then vn=0 end
  while vn >0 do
    t[vn]=random(10) vn=vn-1
  end -- while
  sort(t) s="" vn=getn(t)
  while vn > 0 do
    s=s..t[vn]
    if vn > 1
     then s=s.."," end
    vn=vn-1
  end
  psettext(out,s) psettext(out2,"")
end

rollTalus = function()
  local n
  n = random(4)
  if n == 2 then return 6 end
  return n
end

rollF = function()
  local talus, total, line, roman
  roman = {} roman[1] = "I"
  roman[3] = "III" roman[4] = "IV"
  roman[6] ="VI"
  talus = rollTalus() total=talus 
  line="Action "..roman[talus]
  talus = rollTalus() total=total+talus 
  line=line..","..roman[talus]
  talus = rollTalus() total=total+talus
  line=line..","..roman[talus]
  talus = rollTalus() total=total+talus
  line=line..","..roman[talus]
  line=line.." = "..total
  if 4== total then line=line.." success" end
  if 24== total then line=line.." failure" end
  psettext(out,line) 
  talus ={}
  talus[1] = 0
  talus[3] = 0
  talus[4] = 0
  talus[6] = 0
  total = {}
  total[0] = rollTalus()
  talus[total[0]] = talus[total[0]] + 1 
  total[1] = rollTalus()
  talus[total[1]] = talus[total[1]] + 1 
  total[2] = rollTalus()
  talus[total[2]] = talus[total[2]] + 1   
  total[3] = rollTalus()
  talus[total[3]] = talus[total[3]] + 1   
  line="Effect ("..roman[total[0]]
  line=line..","..roman[total[1]]
  line=line..","..roman[total[2]]
  line=line..","..roman[total[3]]
  line=line..") "
  total = {}
  total[0] = 0
  total[1] = 0
  total[2] = 0
  total[3] = 0
  total[4] = 0
  total[talus[1]] = total[talus[1]] + 1
  total[talus[3]] = total[talus[3]] + 1
  total[talus[4]] = total[talus[4]] + 1
  total[talus[6]] = total[talus[6]] + 1 

  if total[1] == 4 then
      line=line.."= Venus" 
  elseif talus[6] == 1 then
      line=line.."= Senio"
  elseif talus[1] == 4 then
    line=line.."= Dogs"
  elseif total[4] == 1 then
    line=line.."= Vultures"
  elseif total[3] == 1 then
    line=line.."= 3 of a kind"
 elseif total[2] == 2 then
    line=line.."= Two pairs"
  elseif total[2] == 1 then
    line=line.."= One pair"
  end
  psettext(out2,line) 
end


-- main program
ptitle("Tines' Dice")

-- top row
dn = pfield(1,3,3) plabel("D")
dm = pfield(1,4,4)
dminus = pbutton("+")
dadd = pfield(1,3,3) -- pnl()
dminus2 = pbutton("+")
dn2 = pfield(1,3,3) plabel("D")
dm2 = pfield(1,4,4) pnl()

-- basic buttons
dr = pbutton("Roll D&D")
dq = pbutton("Roll RQ")
df = pbutton("Roll Fvlminata")pnl()

-- 3rd row for oddities
x,y=ppos() pmoveto(x,y+2)
dch=pfield(1,3,3) plabel("d6")
dp0=ppbutton("+0",2)
dp1=ppbutton("+1",2)
dph=ppbutton("+1/2",2)
dka=pcheckbox("KA") pnl()

-- Advanced buttons
x,y=ppos() pmoveto(x,y+2)
dc=pbutton("Roll Hero")
dsr=pbutton("Roll SRun")
dvtm=pbutton("Roll Vampire")
pnl()

-- Results
pnl() out = pfield(1,60,60)
pnl() out2 = pfield(1,60,60)

-- decoration
dmenu = pmenu({"A:About Tines' dice"})

-- setup
psetfocus(dn)

-- Event loop
while 1 do
  e, id, ix  = pevent()
  if e == ctlSelect and id == dr then
    rolldnd()
  elseif e == ctlSelect and id == dq then
    rollrq()
  elseif e == ctlSelect and id == dc then
    rollch()
  elseif e == ctlSelect and id == dsr then
    rollsr()
  elseif e == ctlSelect and id == dvtm then
    rollvtm()
  elseif e == ctlSelect and id == dminus then
    qtoggle(dminus)
  elseif e == ctlSelect and id == dminus2 then
    qtoggle(dminus2)
  elseif e == ctlSelect and id == df then
    rollF()
  elseif e == menuSelect then
    palert(
"Tines' dice v 1.1\n"..
"Copyright © 2002 "..
"<tines@ravnaandtines.com>\n\n"..
"This program may be freely "..
"redistributed. See readme.txt for ".. "license. "..
"THERE IS NO WARRANTY."
    )
  end -- event
end -- while