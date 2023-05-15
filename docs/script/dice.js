/*jslint browser: true, indent: 2 */
/*global jQuery, $ */

(function() {
  "use strict";
  // Establish numeric input only
  (function($) {
    $.fn.ForceNumericOnly = function() {
      return this.each(function() {
        $(this).keydown(function(e) {
          var key = e.charCode || e.keyCode || 0;
          // allow backspace, tab, delete, arrows, numbers and keypad numbers ONLY
          return (
            key === 8 ||
            key === 9 ||
            key === 46 ||
            (key >= 37 && key <= 40) ||
            (key >= 48 && key <= 57) ||
            (key >= 96 && key <= 105)
          );
        });
      });
    };
  }(jQuery));

  (function($) {
    $.fn.SignToggle = function() {
      return this.each(function() {
        $(this).click(function(e) {
          if (this.textContent === '+') {
            this.textContent = '-';
          } else {
            this.textContent = '+';
          }
          return this;
        });
      });
    };
  }(jQuery));

  function nparse(f) {
    var n = parseInt($(f)[0].value, 10);
    if (n <= 0 || isNaN(n)) { return 0; }
    return n;
  }

  function roller(n, m) {
    var roll = function(m) {
      return Math.ceil(m * Math.random());
    },
      result = 0,
      i;
    for (i = 0; i < n; ++i) { result += roll(m); }
    return result;
  }

  function nDm(idn, idm) {
    var n = nparse(idn),
      m = nparse(idm);
    return [roller(n, m), n * m];
  }

  // ndm + add + n'dm'
  function rollgrp() {
    var a1 = nDm('#dn', '#dm'),
      s1 = $('#dminus')[0].textContent === '-' ? -1 : 1,
      m = nparse('#dadd') * s1,
      a2 = nDm('#dn2', '#dm2'),
      s2 = $('#dminus2')[0].textContent === '-' ? -1 : 1;
    a2[0] *= s2;
    a2[1] *= s2;

    return [ a1[0], m, a2[0], a1[1], a2[1]];
  }

  // group and d20
  function rolldnd() {
    var a = rollgrp();
    a = a[0] + a[1] + a[2];
    $("#result")[0].value = "d20=" + roller(1, 20) + " roll=" + a;
  }

  // group, d%, d20, specials
  function rollrq() {
    var a = rollgrp(),
      a0 = a[0] + a[1] + a[2],
      i = a0 + a[3],
      c = a0 + a[4],
      s = a0 + nDm('#dn', '#dm')[0];
    $("#result")[0].value = "d%=" + roller(1, 100) +
      " d20=" + roller(1, 20) + " roll=" + a0 + '\n' +
      " slash=" + s + " crush=" + c + " impale=" + i;
  }

  //-- Hero style to hit and effect
  function rollch() {
    var s = 0,
      b = 0,
      d,
      i,
      vn = nparse("#dch"),
      hit,
      loc;
    for (i = 0;  i < vn; ++i) {
      d = roller(1, 6);
      if (6 === d) {
        b += 2;
      } else if (d > 1) { b += 1; }
      s += d;
    }
    if ($("#dp1")[0].checked) {
      s += 1;
    } else if ($("#dph")[0].checked) {
      d = roller(1, 3);
      s += d;
      if (d === 3) { b += 1; }
    }

    if ($("#dka")[0].checked) {
      b = s;
      d = roller(1, 6);
      if (d === 6) { d = 1; }
      s = b * d;
    }

    hit = roller(3, 6);
    loc = roller(3, 6);
    $("#result")[0].value = "to hit = " + hit + "; location = " + loc +
      "; stun = " + s + "; body = " + b;
  }

  function rollsr() {
    var vn = nparse("#dch"),
      d6sr = function() {
        var m = 0,
          n = roller(1, 6);
        while (n === 6) {
          m += n;
          n = roller(1, 6);
        }
        m += n;
        return m;
      },
      i,
      a = [],
      out = '';
    for (i = 0; i < vn; ++i) {
      a.push(d6sr());
    }
    a.sort(function(x, y) {return y - x; });

    for (i = 0; i < vn; ++i) {
      out += a[i] + ",";
    }
    $("#result")[0].value = out.substring(0, out.lastIndexOf(','));
  }

  function rollvtm() {
    var vn = nparse("#dch"),
      i,
      a = [],
      out = '';
    for (i = 0; i < vn; ++i) {
      a.push(roller(1, 10));
    }
    a.sort(function(x, y) {return y - x; });
    for (i = 0; i < vn; ++i) {
      out += a[i] + ",";
    }
    $("#result")[0].value = out.substring(0, out.lastIndexOf(','));
  }

  function rollF() {
    var talus, total, line, roman, i, rollTalus = function() {
      var n = roller(1, 4);
      if (n === 2) { return 6; }
      return n;
    };

    roman = ['', "I", '', "III", "IV", '', "VI"];
    talus = rollTalus();
    total = talus;
    line = "Action " + roman[talus];
    for (i = 0; i < 3; ++i) {
      talus = rollTalus();
      total += talus;
      line += "," + roman[talus];
    }

    line += " = " + total;
    if (4 === total) { line += " success"; }
    if (24 === total) { line += " failure"; }
    $("#result")[0].value = line + "\n";

    talus = [0, 0, 0, 0, 0, 0, 0];
    total = [0, 0, 0, 0];
    for (i = 0; i < 4; ++i) {
      total[i] = rollTalus();
      talus[total[i]] += 1;
    }

    line = "Effect (" + roman[total[0]] +
      "," + roman[total[1]] +
      "," + roman[total[2]] +
      "," + roman[total[3]] +
      ") ";

    total = [0, 0, 0, 0, 0];
    total[talus[1]] += 1;
    total[talus[3]] += 1;
    total[talus[4]] += 1;
    total[talus[6]] += 1;

    if (total[1] === 4) {
      line += "= Venus";
    } else if (talus[6] === 1) {
      line += "= Senio";
    } else if (talus[1] === 4) {
      line += "= Dogs";
    } else if (total[4] === 1) {
      line += "= Vultures";
    } else if (total[3] === 1) {
      line += "= 3 of a kind";
    } else if (total[2] === 2) {
      line += "= Two pairs";
    } else if (total[2] === 1) {
      line += "= One pair";
    }
    $("#result")[0].value += line;
  }

  $("input:text").ForceNumericOnly();
  $(".plusminus").SignToggle();
  $("#dr").bind('click', rolldnd);
  $("#dq").bind('click', rollrq);
  $("#dc").bind('click', rollch);
  $("#dsr").bind('click', rollsr);
  $("#dvtm").bind('click', rollvtm);
  $("#df").bind('click', rollF);
}());
