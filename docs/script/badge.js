/*jslint browser: true, bitwise: true, plusplus: true, indent: 2 */
/*global unescape */

(function() {
  "use strict";

  var $b = {
    toEscapeRep : function (c) { return '&#' + c + ';'; },
    getMailComponents: function (toRep) {
      var data = [613, 617, 609, 612, 636, 615, 562, 636,
                609, 614, 621, 635, 584, 634, 617, 638,
                614, 617, 617, 614, 620, 636, 609, 614,
                621, 635, 550, 619, 615, 613, 520, 581,
                617, 609, 612, 552, 613, 621, 520, 636,
                609, 614, 621, 635, 584, 634, 617, 638,
                614, 617, 617, 614, 620, 636, 609, 614,
                621, 635, 550, 619, 615, 613, 520],
        idx = 0,
        n = data[data.length - 1],
        components = {  link: '',   title: '',   text: ''  };
      while (data[idx] !== n) { components.link = components.link + toRep(data[idx++] ^ n);  }
      idx++;
      while (data[idx] !== n) { components.title = components.title + toRep(data[idx++] ^ n);  }
      idx++;
      while (data[idx] !== n) { components.text = components.text + toRep((data[idx++] ^ n)); }
      return components;
    },
    getMailtext: function () {
      var c = $b.getMailComponents($b.toEscapeRep);
      return '<a href = "' + c.link + '" title = "' + c.title + '">' + c.text + '</a>';
    },
    doMailFor: function (who) {
      if (!document.layers) {
        var c, p  =  document.getElementById('mailtag');
        if (!p) { return; }
        p.style.textAlign  =  "center";
        p.removeChild(p.firstChild);
        p.appendChild(document.createTextNode(unescape(who)));
        p.appendChild(document.createElement('a'));

        c = $b.getMailComponents(String.fromCharCode);
        p.lastChild.setAttribute('href', c.link);
        p.lastChild.setAttribute('title', c.title);
        p.lastChild.appendChild(document.createTextNode(c.text));
      }
    },
    doMail:  function () { $b.doMailFor('This document maintained by domain webmaster \u2014 '); },
/* 
createElement function found at http://simon.incutio.com/archive/2003/06/15/javascriptWithXML
*/
    createElement: function (element) {
      if (!(document.createElementNS  ===  undefined)) {
        return document.createElementNS('http://www.w3.org/1999/xhtml', element);
      }
      if (!(document.createElement  ===  undefined)) {
        return document.createElement(element);
      }
      return false;
    },
    init:  function() {
      window.pre_tines_onload  =  window.onload;
      window.onload  =  function () {
        $b.doMail();
        if (window.pre_tines_onload) { window.pre_tines_onload(); }
      };
    }
  }; // end module

  $b.init();
}());