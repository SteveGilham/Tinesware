(function()
{
 "use strict";
 var Global,web_core,NBodyApplet,Vector3,NBodyMass,IntelliFactory,Runtime,WebSharper,UI,Var$1,Submitter,View,Utils,Doc,AttrProxy,Math;
 Global=self;
 web_core=Global.web_core=Global.web_core||{};
 NBodyApplet=web_core.NBodyApplet=web_core.NBodyApplet||{};
 Vector3=NBodyApplet.Vector3=NBodyApplet.Vector3||{};
 NBodyMass=NBodyApplet.NBodyMass=NBodyApplet.NBodyMass||{};
 IntelliFactory=Global.IntelliFactory;
 Runtime=IntelliFactory&&IntelliFactory.Runtime;
 WebSharper=Global.WebSharper;
 UI=WebSharper&&WebSharper.UI;
 Var$1=UI&&UI.Var$1;
 Submitter=UI&&UI.Submitter;
 View=UI&&UI.View;
 Utils=WebSharper&&WebSharper.Utils;
 Doc=UI&&UI.Doc;
 AttrProxy=UI&&UI.AttrProxy;
 Math=Global.Math;
 Vector3.New=function(x,y,z)
 {
  return{
   x:x,
   y:y,
   z:z
  };
 };
 NBodyMass.New=function(x,x0,x0dot,f,fdot,d1,d2,d3,mass,energy,step,t0,t1,t2,t3)
 {
  return{
   x:x,
   x0:x0,
   x0dot:x0dot,
   f:f,
   fdot:fdot,
   d1:d1,
   d2:d2,
   d3:d3,
   mass:mass,
   energy:energy,
   step:step,
   t0:t0,
   t1:t1,
   t2:t2,
   t3:t3
  };
 };
 NBodyApplet.Main$102$48=function(submit)
 {
  return function()
  {
   submit.Trigger();
  };
 };
 NBodyApplet.Main$101$42=Runtime.Curried3(function(submit,$1,$2)
 {
  return submit.Trigger();
 });
 NBodyApplet.Main=function()
 {
  var slider,submit,slid;
  slider=Var$1.Create$1("0");
  submit=Submitter.CreateOption(slider.get_View());
  slid=View.Map(function(a)
  {
   return a!=null&&a.$==1?(function($1)
   {
    return function($2)
    {
     return $1("Separation of the moons (degrees) = "+Utils.toSafe($2));
    };
   }(Global.id))(slider.Get()):"";
  },submit.view);
  return Doc.Element("div",[],[Doc.Element("canvas",[AttrProxy.Create("id","display"),AttrProxy.Create("height","400"),AttrProxy.Create("width","800"),AttrProxy.Create("style","background:  black")],[]),Doc.Element("p",[],[Doc.Element("label",[AttrProxy.Create("id","angle_label"),AttrProxy.Create("for","angle")],[Doc.TextView(slid)]),Doc.Input([AttrProxy.Create("name","angle"),AttrProxy.Create("id","angle"),AttrProxy.Create("type","range"),AttrProxy.Create("min","0"),AttrProxy.Create("max","360"),AttrProxy.Create("step","1"),AttrProxy.Create("title","Range: 0-360"),AttrProxy.Create("value","0"),AttrProxy.Create("style","width: 100%;"),AttrProxy.HandlerImpl("input",function()
  {
   return function()
   {
    return submit.Trigger();
   };
  }),AttrProxy.OnAfterRenderImpl(function()
  {
   submit.Trigger();
  })],slider)]),Doc.Button("Start",[AttrProxy.Create("id","go"),AttrProxy.Create("type","submit")],Global.ignore)]);
 };
 NBodyApplet.setStep=function(m)
 {
  var _step,d,d$1,d$2;
  _step=Math.sqrt(0.01*Math.sqrt(NBodyApplet.op_AtDot(m.f,m.f)/NBodyApplet.op_AtDot(m.d2,m.d2)));
  d=NBodyApplet.op_PlusPlus(NBodyApplet.op_PlusPlus(m.fdot,NBodyApplet.op_AtMultiply(m.d2,_step/-2)),NBodyApplet.op_AtMultiply(m.d3,_step*_step/6));
  d$1=NBodyApplet.op_AtDivide(NBodyApplet.op_PlusPlus(m.d2,NBodyApplet.op_AtMultiply(m.d3,_step*-1)),2);
  d$2=NBodyApplet.op_AtDivide(m.d3,6);
  return NBodyMass.New(m.x,m.x0,m.x0dot,NBodyApplet.op_AtDivide(m.f,2),NBodyApplet.op_AtDivide(m.fdot,6),d,d$1,d$2,m.mass,m.energy,_step,0,_step*-1,_step*-2,_step*-3);
 };
 NBodyApplet.op_AtDot=function(a,b)
 {
  return a.x*b.x+a.y*b.y+a.z*b.z;
 };
 NBodyApplet.op_AtDivide=function(a,b)
 {
  return Vector3.New(a.x/b,a.y/b,a.z/b);
 };
 NBodyApplet.op_AtMultiply=function(a,b)
 {
  return Vector3.New(a.x*b,a.y*b,a.z*b);
 };
 NBodyApplet.op_MinusMinus=function(a,b)
 {
  return Vector3.New(a.x-b.x,a.y-b.y,a.z-b.z);
 };
 NBodyApplet.op_PlusPlus=function(a,b)
 {
  return Vector3.New(a.x+b.x,a.y+b.y,a.z+b.z);
 };
}());
