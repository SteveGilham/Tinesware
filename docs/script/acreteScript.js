/*jslint browser: true, continue: true, plusplus: true, indent: 2 */
(function() {
  "use strict";

  function getMass() { return Math.pow(10.0, 0.02 * (document.getElementById("mass").value - 50)); }

  function getEcc() { return 0.01 * document.getElementById("ecc").value; }

  function doUpdate() {
    var update = getMass().toFixed(3);
    document.getElementById("mass_label").firstChild.nodeValue = "Primary mass = " + update;
    update = getEcc().toFixed(3);
    document.getElementById("ecc_label").firstChild.nodeValue = "Eccentricity = " + update;
  }

  var Main, sweep, store;

  // output formatting as IE9 seems to use 12-column tabs
  String.prototype.pad =  function pad(n, a) {
    var tmp = this.toString(),
      stops = [0, 4, 12, 20, 28, 34, 42, 48, 56, 64, 72, 84];
    while (tmp.length < stops[n]) { tmp = tmp + " "; }
    return tmp + a;
  };

  function toFixed(n, pre, post) {
    var result = n.toFixed(post);
    if (post === 0) {
      while (result.length < pre) { result = ' ' + result; }
    } else {
      while (result.indexOf('.') < pre) { result = ' ' + result; }
    }
    return result;
  }

  function postprocess() {
    var i, line = "#";
    document.getElementById("output").value =
      line.pad(1, '  a').pad(2, '  e').pad(3, '  year').pad(4, 'T(C)').pad(5, ' incl').pad(6, 'r(km)').
          pad(7, 'g(m/s\u00B2)').pad(8, 'size').pad(9, '  tide').pad(10, 'day(hrs)').pad(11, 'status\n');

    for (i = 0; i < sweep.length; ++i) { sweep[i].postProcess(i); }
  }

  function doSort(a, b) { return a.compareTo(b); }

  function Planet(ecc) {
    this.v_x = "rocky";
    this.e  = ecc * Math.random();
    this.totalMass = 0;
    this.rockyMass = 0;

    this.h = function () { return this.totalMass * Math.sqrt(this.a * (1 - Math.pow(this.e, 2))); };

    this.set = function(p) {
      this.e = p.e;
      this.v_x = p.v_x;
      this.a = p.a;
      this.inner = p.inner;
      this.outer = p.outer;
      this.rockyMass = p.rockyMass;
      this.totalMass = p.totalMass;
    };

    this.merge = function(p) {
      var kf, hh = this.h() + p.h();
      this.rockyMass += p.rockyMass;
      this.totalMass += p.totalMass;
      kf = Math.min(this.e, p.e);
      this.e = Math.random() * kf;
      hh /= this.totalMass;
      this.a = (hh * hh) / (1.0 - (this.e * this.e));
    };

    this.compareTo = function(p) {
      if (p.a < this.a) { return 1; }
      if (p.a > this.a) { return -1; }
      return 0;
    };

    this.output = function(message) { document.getElementById("output").value += message + "\n"; };

    this.postProcess = function(i) {
//this.output("postprocessing "+i);
      var mass, diff, m0, x0, xx, tide, day,
        year = this.a * Math.sqrt(this.a / Main.ms),
        incl = 180 * (1.0 - Math.pow(Math.random(), 2.0 / 9.0)),
        line,
        radius = 0,
        rn = 0,
        s = 0,
        g = 0,
        n = sweep.length < 10 ? 1 : 2;

      this.temp = 288 * Math.sqrt(Math.sqrt(Main.luminosity) / this.a) - 273;

//this.output("check 1");
      this.u_x = "maybe";
      if (!(this.v_x === "rocky")) { this.u_x = "satellite only"; }
      if (this.totalMass < 0.009) { this.u_x = "too small"; }

//this.output("check 2 : temp = "+this.temp.toFixed(2));
      if (this.temp < (-20)) { this.u_x = "too cold"; }
//this.output("check 3");
      if (this.temp > 65) { this.u_x = "too hot"; }
//this.output("check 4");
      line = "".pad(0, toFixed((i + 1), n, 0)).
                 pad(1, toFixed(this.a, 2, 3)).
                 pad(2, toFixed(this.e, 1, 3)).
                 pad(3, toFixed(year, 3, 3)).
                 pad(4, toFixed(this.temp, 4, 0)).
                 pad(5, toFixed(incl, 3, 1));
//this.output(line);

//this.output("check 5");
      if (this.v_x === "rocky") {
        mass = function(r) { return r * r * r * Math.pow(2, r - 1); };
        diff = function(f, x) { return 100 * (f(x + 0.01) - f(x)); };
        m0 = this.totalMass / 0.03;
        x0 = Math.pow(m0, 0.333);
        do {
          xx = x0;
          x0 = x0 - (mass(x0) - m0) / diff(mass, x0);
//                  this.output(""+x0.toFixed(3));
        } while (Math.abs(x0 - xx) > 0.003);
        rn = x0;
        s = Math.round(5.673 * rn);
      } else if (this.v_x === "gas-giant") {
        s = 8;
        if (this.totalMass > 1) { s = 9; }
        if (this.totalMass > 10) { s = 10; }
        rn = 4.2952 * Math.pow(this.totalMass, 0.3846);
        if (rn > 10.5) { rn = 10.5; }
      } else {
        rn = 0.6956 * Math.pow(this.totalMass, 5936);
        s = 11;
      }

      radius = 6400 * rn;
      g = 327 * this.totalMass / (rn * rn);
      line = line.pad(6, toFixed(Math.round(radius), 5, 0)).
                    pad(7, toFixed(g, 2, 2)).
                    pad(8, toFixed(s, 3, 0));

      tide = 0.020214 * Main.ms * rn * rn * rn * rn / (this.totalMass * this.a * this.a * this.a);
      tide *= tide;
      if (tide > 2 && this.u_x === "maybe") { this.u_x = "tidal limiting"; }
      if (tide > 400 && this.u_x === "maybe") {this.u_x = "destructive tides"; }
      day = 2.667 * rn / Math.sqrt(this.totalMass);

      line = line.pad(9, (tide < 400 ? toFixed(tide, 3, 3) : "*******")).
                    pad(10, toFixed(day, 3, 3)).
                    pad(11, this.u_x + "\n");
      document.getElementById("output").value += line;
    };
  } //end of class planet

  function drawLine(context, x1, y1, x2, y2) {
    context.beginPath();
    context.moveTo(x1, y1);
    context.lineTo(x2, y2);
    context.stroke();
  }

  function draw() {
    var i, j, x, y, range, px, notch, p, fraction, r, ra, rp, xa, xp, x1, x2, delta,
      canvas = document.getElementById("display"),
      context = null,
      size = null;

    try { context = canvas.getContext("2d"); } catch (e) { return; }

    context.globalCompositeOperation = "source-over";
    context.fillStyle = "black";
    context.fillRect(0, 0, canvas.width, canvas.height);
    size = canvas;

    context.strokeStyle = "white";
    context.lineWidth = 1;

    y = size.height / 2;
    range = size.width - 10;
    px = range / 300.0;

    notch = [0.2, 0.5, 1, 2, 5, 10, 20];
    for (i = 0; i < notch.length; ++i) {
      x = 5 + Math.round(range * Math.log(notch[i] * 10) / Math.log(500.0));
      drawLine(context, x, y - 5, x, y + 5);
    }

    drawLine(context, 5, y, 5 + range, y);

    for (i = 0; i < sweep.length; ++i) {
      p = sweep[i];
      context.strokeStyle = "cyan";
      if (p.totalMass > p.rockyMass + 0.01) { context.strokeStyle = "#f0f";
        } else if (p.totalMass > 200) { context.strokeStyle = "yellow"; }

      fraction = Math.log(10 * p.a) / Math.log(500.0);

      x = 5 + Math.round(range * fraction);
      r =  Math.round(10 * px * Math.pow(p.totalMass, 0.333));
      context.beginPath();
      context.moveTo(x + r, y);
      context.arc(x, y, r, 0, 2 * Math.PI, 1);
      context.stroke();

      ra = p.a * (1 + p.e);
      rp = p.a * (1 - p.e);
      xa = 5 + Math.round(range * Math.log(ra * 10) / Math.log(500.0));
      xp = 5 + Math.round(range * Math.log(rp * 10) / Math.log(500.0));
      drawLine(context, xa, y + 2 * r, xp, y + 2 * r);
    }

    context.strokeStyle = "silver";

    for (i = 0; i <= 500; ++i) {
      if (store[i] < Main.minmass / 2) { continue; }
      x1 = 5 + Math.round(range * Math.log(i - 0.5) / Math.log(500.0));
      if (x1 < 5) { x1 = 5; }
      x2 = 5 + Math.round(range * Math.log(i + 0.5) / Math.log(500.0));
      if (x2 > size.width - 5) { x2 = size.width - 5; }
      for (j = x1; j <= x2; ++j) {
        delta = (5.0 * Math.pow((store[i] / Main.minmass), 0.333));
        drawLine(context, j, y - delta, j, y + delta);
      }
    }
  }

  function init() {
    var i, r, u;
    sweep = [];
    store = new Array(501);
    Main = {
      ms : getMass(),
      e : getEcc(),
      extreme : 500,
      inner : 0,
      d : 1,
      g : 25,
      minmass : 0
    };

    Main.luminosity = Math.pow(Main.ms, 4);
    if (Main.ms < 0.4) { Main.luminosity = 0.23 * Math.pow(Main.ms, 2.3); }
    Main.inner = Math.round(3.0 * Math.sqrt(Main.luminosity));
    if (Main.inner < 1) { Main.inner = 1; }

    for (i = 0; i < store.length; ++i) { store[i] = 0; }
    for (i = Main.inner; i <= Main.extreme; ++i) {
      r = (0.1 * i) / Math.pow(Main.ms, 0.333);
      u = Math.pow(r, 0.333);
      store[i] = 1.5 * Main.d * r * r * Math.exp(-5.0 * u);
    }
    Main.minmass = Math.min(store[Main.extreme], store[Main.inner]) / 2;
  }

  function putNucleus(p) {
    var i, sum = 0;
    for (i = Main.inner; i <= Main.extreme; ++i) { sum += store[i];  }
    if (sum < Main.minmass) { return false; }
    sum *= Math.random();
    for (i = Main.inner;  i <= Main.extreme; ++i) {
      sum -= store[i];
      if (sum < 0) {
        p.a = 0.1 * i;
        sweep.push(p);
        return true;
      }
    }
    return false;
  }

  function accreteMatter(k) {
    var kf, i, im, ix, dm = 0,
      apastron = sweep[k].a * (1.0 + sweep[k].e),
      periastron = sweep[k].a * (1.0 - sweep[k].e),
      critical = 0.12 * Math.pow(periastron, -0.75) * Math.pow(Main.luminosity, 0.375);

    do {
      sweep[k].totalMass = sweep[k].rockyMass;
      if (sweep[k].rockyMass > critical) { sweep[k].totalMass = critical + Main.g * (sweep[k].rockyMass - critical); }
      kf = 0.1 * Math.pow(sweep[k].totalMass / Main.ms, 0.25);
      sweep[k].inner = periastron - kf;
      sweep[k].outer = apastron + kf;

      im = Math.max(Math.round(10 * sweep[k].inner), Main.inner);
      ix = Math.min(Math.round(10 * sweep[k].outer), Main.extreme);
      dm = 0;
      for (i = im; i <= ix; ++i) {
        dm += store[i];
        store[i] = 0;
      }
      sweep[k].rockyMass += dm;
    } while (dm > Main.minmass / 2);

    if (sweep[k].rockyMass > critical) { sweep[k].v_x = "gas-giant"; }
    sweep[k].totalMass = sweep[k].rockyMass;

    if (sweep[k].rockyMass > critical) { sweep[k].totalMass = critical + Main.g * (sweep[k].rockyMass - critical); }
    if (sweep[k].totalMass > 200) { sweep[k].v_x = "stellar"; }
  }

  function collide() {
    var tmp, k;
    if (sweep.length < 2) { return; }
    for (k = 0; k < sweep.length - 1; ++k) {
      // If the orbits don't intersect, skip
      if (sweep[sweep.length - 1].outer < sweep[k].inner) { continue; }
      if (sweep[sweep.length - 1].inner > sweep[k].outer) { continue; }

      // merge the most recently added platetesimal with this one
      // and shorten the list -- inherently not a foreach operaton
      sweep[k].merge(sweep[sweep.length - 1]);
      sweep.pop();
      accreteMatter(k);

      if (k === sweep.length - 1) {
        collide();
      } else {
        tmp = new Planet();
        tmp.set(sweep[k]);
        sweep[k].set(sweep[sweep.length - 1]);
        sweep[sweep.length - 1].set(tmp);
      }
    }
  }

  function acrete() {
    init();
    for(;;) {
      if (document.getElementById("display")) { draw(); }
      if (!putNucleus(new Planet(Main.e))) { break; }
      accreteMatter(sweep.length - 1);
      collide();
    }

    sweep.sort(doSort);
    postprocess();
    draw();
  }

  document.getElementById("go").onclick = acrete;
  // reset on reload
  document.getElementById("output").value = "";

  // Add onchange event
  document.getElementById("mass").onchange = doUpdate;
  document.getElementById("ecc").onchange = doUpdate;
  doUpdate(null);
}());
