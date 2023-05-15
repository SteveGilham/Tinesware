/*jslint browser: true, bitwise: true, continue: true, plusplus: true, indent: 2 */
(function() {
  "use strict";
  document.getElementById("go").onclick = function () {

    var str1, str2, str3, str4, str6, subject, i, j, l, bufferlength, text, seed, lines, output;
    str1 = "mailto:" + document.getElementById("email").value;
    subject = document.getElementById("about").value;
    if (subject.length > 0) {
      str1 = str1 + "?subject=" + encodeURI(subject);
    }

    str2 = document.getElementById("hover").value;
    str3 = document.getElementById("link").value;

    bufferlength = str1.length + str2.length + str3.length + 3;
    text = [];

    j = 0;
    for (i = 0; i < str1.length; ++i, ++j) { text[j] = str1.charCodeAt(i); }
    text[j++] = 0;

    for (i = 0; i < str2.length; ++i, ++j) { text[j] = str2.charCodeAt(i); }
    text[j++] = 0;

    for (i = 0; i < str3.length; ++i, ++j) { text[j] = str3.charCodeAt(i); }
    text[j++] = 0;

    seed = 512;
    for (i = 0; i < bufferlength; ++i) { seed ^= text[i]; }
    for (i = 0; i < bufferlength; ++i) { text[i] ^= seed; }

    str4 = document.getElementById("email").value.replace(/@/g, " &lt;at&gt; ");
    str4 = str4.replace(/\.com/g, " &lt;dot&gt; c_o_m").replace(/\./g, " &lt;dot&gt; ");
    str6 = document.getElementById("id").value;

    lines = Math.floor((bufferlength + 7) / 8);

    output = "<html><head>\n<title>\"E\"-ddress encoder framework page</title>\n";
    output += "</head><body>\n";
    output += "<p id=\"" + str6 + "\">" + str4 + "</p>\n";
    output += "</body>\n";

    output += "<script type=\"text/javascript\">//<![CDATA[\n/*jslint browser: true, bitwise: true, plusplus: true, indent: 2 */\n";
    output += "(function() {\n  \"use strict\";\n";

    output += "  var data = [\n";

    for (l = 0; l < lines; ++l) {
      output += "     ";
      for (j = 0; j < 8; ++j) {
        i = (l * 8) + j;
        if (i >= bufferlength) { continue; }
        output += " " + text[i];
        if (i < bufferlength - 1) { output += ","; }
      }
      output += "\n";
    }

    output += "    ],\n";
    output += "    idx = 0,\n";
    output += "    a = document.createElement('a'),\n";
    output += "    n = data[data.length - 1],\n    mailtext = '',\n";
    output += "    m = document.getElementById('" + str6 + "');\n  while (m.firstChild) { m.removeChild(m.firstChild); }\n";
    output += "  while (data[idx] !== n) {mailtext += String.fromCharCode(data[idx++] ^ n); }\n  idx++;\n  a.setAttribute('href', mailtext);\n  mailtext = '';\n";
    output += "  while (data[idx] !== n) {mailtext += String.fromCharCode(data[idx++] ^ n); }\n  idx++;\n  a.setAttribute('title', mailtext);\n  mailtext = '';\n";
    output += "  while (data[idx] !== n) {mailtext += String.fromCharCode(data[idx++] ^ n); }\n  idx++;\n  a.appendChild(document.createTextNode(mailtext));\n";
    output += "  m.appendChild(a);\n}());\n";
    output += "//]]></sc" + "ript></html>\n";

    document.getElementById("code").value = output;
  };
}());