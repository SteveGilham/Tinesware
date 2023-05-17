/*
(c) 2002-2007 Manuel Heras
Permission to modify and redistribute is explicitly granted for all purposes.
I do not provide any guarantee as to the correctness of the decoding, and do
not accept any responsibility.

This HTML page contains a JavaScript decoder for the international METAR
standard code for reporting of weather observations.
I think the decoder is quite complete, according to the unofficial
specifications I have checked against, but I could not thoroughly test
every single field. Should you find any errors, please do not hesitate to
contact me.

Use this page for anything you want, with or without modifications. If you
improve it, I would like to hear from you!

Manuel Heras (manuel@heras-gilsanz.com)
August 2002 

Dec 2006: Parser for "American" expression for altimeter
          contributed by Ricardo Fernandez
Feb 2007: Correction to support "0x0d0a-style" end-of-lines by Wim de Vries
Mar 2007: Parser for "American" Statute-Miles visibility
          contributed by Ricardo Fernandez
*/
var decoded_output = "";

var do_metar = (function(report){

function add_output(text)
{
    decoded_output = decoded_output + text;
}

function is_num_digit(ch)
{
    return ( (ch == '0') || (ch == '1') || (ch == '2') || (ch == '3') ||
             (ch == '4') || (ch == '5') || (ch == '6') || (ch == '7') ||
             (ch == '8') || (ch == '9') );
}

function is_alphabetic_char(ch)
{
    return ( (ch >= 'A') && (ch <= 'Z') );
}

function textToInt(text)
{
  var out = text.replace('M', '-').replace('-0', '-');
  if (out.charAt(0) == '0') out = out.substr(1);
  return out;
}


function decode_token(token)
{
    // Check if token is "calm wind"
    if(token == "00000KT")
    {
        add_output("Calm wind\n");
        return;
    }

    // Check if token is Wind indication
    var reWindKT  = /^(\d{3}|VRB)(\d{2,3})(G\d{2,3})?(KT|MPS|KMH)$/;
    if(reWindKT.test(token))
    {
        // Wind token: dddss(s){Gss(s)}KT -- ddd is true direction, ss(s) speed in knots
        var myArray = reWindKT.exec(token);
        var units = myArray[4];
        add_output("Wind: ");
        if(myArray[1]=="VRB")
          add_output(" Variable in direction");
        else
          add_output(" True direction = " + myArray[1] + " degrees");
        add_output(", Speed: " + parseInt(myArray[2],10));
        if(units=="KT") add_output(" knots");
        else if(units=="KMH") add_output(" km/h");
        else if(units=="MPS") add_output(" m/s");
        if(myArray[3] != null)
        {
            // I don't have the time nor the energy to investigate why
            // MSIE and Firefox behave differently with respect to an
            // omitted regular subexpression. Hence this quick hack to
            // detect if myArray[3] is not a number.
            if (myArray[3]!="")
            {
                add_output(", with Gusts of maximum speed " + parseInt(myArray[3].substr(1,myArray[3].length),10));
                if(units=="KT") add_output(" knots");
                else if(units=="KMH") add_output(" km/h");
                else if(units=="MPS") add_output(" m/s");
             }
        }

        add_output("\n");  return;
    }


    // Check if token is "variable wind direction"
    var reVariableWind = /^(\d{3})V(\d{3})$/;
    if(reVariableWind.test(token))
    {
        // Variable wind direction: aaaVbbb, aaa and bbb are directions in clockwise order
        add_output("Wind direction is variable between "+token.substr(0,3)+" and "+token.substr(4,3)+"\n");
        return;
    }


    // Check if token is visibility
    var reVis = /^(\d{4})(N|S)?(E|W)?$/;
    if(reVis.test(token))
    {
        var myArray = reVis.exec(token);
        add_output("Visibility: ");
        if(myArray[1]=="9999")
          add_output("10 km or more");
        else if (myArray[1]=="0000")
          add_output("less than 50 m");
        else
          add_output(parseInt(myArray[1],10) + " m");

	var dir = "";
        if(typeof myArray[2] != "undefined")
        {
          dir=dir + myArray[2];
        }
        if(typeof myArray[3] != "undefined")
        {
          dir=dir + myArray[3];
        }
        if(dir != "")
        {
          add_output(" direction ");
          if(dir=="N") add_output("North");
          else if(dir=="NE") add_output("North East");
          else if(dir=="E") add_output("East");
          else if(dir=="SE") add_output("South East");
          else if(dir=="S") add_output("South");
          else if(dir=="SW") add_output("South West");
          else if(dir=="W") add_output("West");
          else if(dir=="NW") add_output("North West");
        }
        add_output("\n"); return;
    }

    // Check if token is Statute-Miles visibility
     var reVisUS = /(SM)$/;
     if(reVisUS.test(token))
     {
      add_output("Visibility: ");
      var myVisArray = token.split("S");
      add_output(myVisArray[0]);
      add_output(" Statute Miles\n");
    }
     

    // Check if token is QNH indication in mmHg or hPa
    var reQNHhPa = /Q\d{3,4}/;
    if(reQNHhPa.test(token))
    {
        // QNH token: Qpppp -- pppp is pressure hPa 
        add_output("QNH (Sea-level pressure): ");
        add_output(parseInt(token.substr(1,4),10) + " hPa"); 
        add_output("\n");  return;
    }

    // Check if token is QNH indication in mmHg: Annnn
    var reINHg = /A\d{4}/;
    if(reINHg.test(token))
    {
        add_output("QNH: ");
        add_output(token.substr(1,2) + "." + token.substr(3,4) + " inHg");
        add_output("\n");  return;
    } 

    // Check if token is runway visual range (RVR) indication
    var reRVR = /^R(\d{2})(R|C|L)?\/(M|P)?(\d{4})(V\d{4})?(U|D|N)?$/;
    if(reRVR.test(token))
    {
        var myArray = reRVR.exec(token);
        add_output("Runway ");
        add_output(myArray[1]);
        if(typeof myArray[2] != "undefined")
        {
          if(myArray[2]=="L") add_output(" Left");
          else if(myArray[2]=="R") add_output(" Right");
          else if(myArray[2]=="C") add_output(" Central");
        }
        add_output(", touchdown zone visual range is ");
        if(typeof myArray[5] != "undefined")
        {
            // Variable range
            add_output("variable from a minimum of ");
            if(myArray[3]=="P") add_output("more than ");
            else if(myArray[3]=="M") add_output("less than ");
            add_output(myArray[4]);
            add_output(" meters");
            add_output(" until a maximum of "+myArray[5].substr(1,myArray[5].length)+" meters");
        }
        else
        {
          // Single value
          if( /*(typeof myArray[3] != "undefined") &&*/
              (typeof myArray[4] != "undefined")    )
          {
            if(myArray[3]=="P") add_output("more than ");
            else if(myArray[3]=="M") add_output("less than ");
            add_output(myArray[4]);
            add_output(" meters");
          }
        }
        if( (myArray.length > 5) && (typeof myArray[6] != "undefined") )
        {
          if(myArray[6]=="U") add_output(", and increasing");
          else if(myArray[6]=="D") add_output(", and decreasing");
        }
        add_output("\n");
        return;
    }


    // Check if token is CAVOK
    if(token == "CAVOK")
    {
        add_output("CAVOK conditions: Visibility 10 km or more,\n    no cloud below 5.000 feet or below the MSA (whichever is greater), \n    no cumulonimbus, and no significant weather phenomena in\n    the aerodrome or its vicinity\n");
        return;
    }


    // Check if token is NOSIG
    if(token == "NOSIG")
    {
        add_output("No significant changes expected in the near future\n");
        return;
    }


    // Check if token is a present weather code - The regular expression is a bit
    // long, because several precipitation types can be joined in a token, and I
    // don't see a better way to get all the codes.
    var reWX = /^(\-|\+)?(VC)?(MI|BC|BL|SH|TS|FZ|PR)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS|BR|FG|FU|VA|DU|SA|HZ|PO|SQ|FC|SS|DS)$/;
    if(reWX.test(token))
    {
        add_output("Weather: ");
        var myArray = reWX.exec(token);
        for(var i=1;i<myArray.length; i++)
        {
            if(myArray[i] == "-") add_output("Light ");
            if(myArray[i] == "+") add_output("Strong ");
            if(myArray[i] == "VC") add_output("In the vicinity, ");
            if(myArray[i] == "MI") add_output("Shallow ");
            if(myArray[i] == "BC") add_output("Patches of ");
            if(myArray[i] == "SH") add_output("Showers of ");
            if(myArray[i] == "TS") add_output("Thunderstorms ");
            if(myArray[i] == "FZ") add_output("Freezing (or super-cooled) ");
            if(myArray[i] == "PR") add_output("Partial ");
            if(myArray[i] == "DZ") add_output("Drizzle ");
            if(myArray[i] == "RA") add_output("Rain ");
            if(myArray[i] == "SN") add_output("Snow ");
            if(myArray[i] == "SG") add_output("Snow grains ");
            if(myArray[i] == "IC") add_output("Ice Crystals ");
            if(myArray[i] == "PL") add_output("Ice Pellets ");
            if(myArray[i] == "GR") add_output("Hail ");
            if(myArray[i] == "GS") add_output("Small hail (< 5 mm diameter) and/or snow pellets ");
            if(myArray[i] == "BR") add_output("Mist ");
            if(myArray[i] == "FG") add_output("Fog ");
            if(myArray[i] == "FU") add_output("Smoke ");
            if(myArray[i] == "VA") add_output("Volcanic Ash ");
            if(myArray[i] == "DU") add_output("Widespread dust ");
            if(myArray[i] == "SA") add_output("Sand ");
            if(myArray[i] == "HZ") add_output("Haze ");
            if(myArray[i] == "PO") add_output("Dust/Sand whirls ");
            if(myArray[i] == "SQ") add_output("Squall ");
            if(myArray[i] == "FC") add_output("Funnel clouds ");
            if(myArray[i] == "SS") add_output("Sandstorm ");
            if(myArray[i] == "DS") add_output("Duststorm ");
        }
        add_output("\n");  return;
    }


    // Check if token is recent weather observation
    var reREWX = /^RE(\-|\+)?(VC)?(MI|BC|BL|SH|TS|FZ|PR)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS)?(DZ|RA|SN|SG|IC|PL|GR|GS|BR|FG|FU|VA|DU|SA|HZ|PO|SQ|FC|SS|DS)?$/;
    if(reREWX.test(token))
    {
        add_output("Since the previous observation (but not at present), the following\nmeteorological phenomena were observed: ");
        var myArray = reREWX.exec(token);
        for(var i=1;i<myArray.length; i++)
        {
            if(myArray[i] == "-") add_output("Light ");
            if(myArray[i] == "+") add_output("Strong ");
            if(myArray[i] == "VC") add_output("In the vicinity, ");
            if(myArray[i] == "MI") add_output("Shallow ");
            if(myArray[i] == "BC") add_output("Patches of ");
            if(myArray[i] == "SH") add_output("Showers of ");
            if(myArray[i] == "TS") add_output("Thunderstorms ");
            if(myArray[i] == "FZ") add_output("Freezing (or super-cooled) ");
            if(myArray[i] == "PR") add_output("Partial ");
            if(myArray[i] == "DZ") add_output("Drizzle ");
            if(myArray[i] == "RA") add_output("Rain ");
            if(myArray[i] == "SN") add_output("Snow ");
            if(myArray[i] == "SG") add_output("Snow grains ");
            if(myArray[i] == "IC") add_output("Ice Crystals ");
            if(myArray[i] == "PL") add_output("Ice Pellets ");
            if(myArray[i] == "GR") add_output("Hail ");
            if(myArray[i] == "GS") add_output("Small hail (< 5 mm diameter) and/or snow pellets ");
            if(myArray[i] == "BR") add_output("Mist ");
            if(myArray[i] == "FG") add_output("Fog ");
            if(myArray[i] == "FU") add_output("Smoke ");
            if(myArray[i] == "VA") add_output("Volcanic Ash ");
            if(myArray[i] == "DU") add_output("Widespread dust ");
            if(myArray[i] == "SA") add_output("Sand ");
            if(myArray[i] == "HZ") add_output("Haze ");
            if(myArray[i] == "PO") add_output("Dust/Sand whirls ");
            if(myArray[i] == "SQ") add_output("Squall ");
            if(myArray[i] == "FC") add_output("Funnel clouds ");
            if(myArray[i] == "SS") add_output("Sandstorm ");
            if(myArray[i] == "DS") add_output("Duststorm ");
        }
        add_output("\n"); return;
    }


    // Check if token is temperature / dewpoint pair
    var reTempDew = /^(M?\d\d|\/\/)\/(M?\d\d)?$/;
        if (reTempDew.test(token)) {
            var myArray = reTempDew.exec(token);
            var temp = textToInt(myArray[1].substr(0, myArray[1].charAt(0) == 'M' ? 3 : 2));
            add_output("Temperature: " + eval(temp) + "C\n");
            if (myArray[2] != "") {
                var dew = textToInt(myArray[2].substr(0, myArray[2].charAt(0) == 'M' ? 3 : 2));
                add_output("Dewpoint: " + eval(dew) + "C\n");
                var rh_numer = 100.0*Math.exp((17.27*eval(dew))/(eval(dew)+237.7));
                var rh_denom =       Math.exp((17.27*eval(temp))/(eval(temp)+237.7));
                add_output("Humidity: " + Math.round(rh_numer/rh_denom) + "%\n");
            }                 
            return
        }


    // Check if token is "sky clear" indication
    if(token=="SKC")
    {
        add_output("Clear sky\n");
        return;
    }


    // Check if token is "vertical visibility" indication
    var reVV = /^VV(\d{3}|\/{3})$/;
    if(reVV.test(token))
    {
        // VVddd -- ddd is vertical distance, or /// if unspecified
        var myArray = reVV.exec(token);
        add_output("Sky is obscured -- vertical visibility");
        if(myArray[1] == "///")
          add_output(" cannot be assessed\n");
        else
          add_output(": " + (100*parseInt(myArray[1],10)) + " feet\n");

        return;
    }


    // Check if token is cloud indication
    var reCloud = /^(FEW|SCT|BKN|OVC)(\d{3})(CB|TCU)?$/;
    if(reCloud.test(token))
    {
        // Clouds: aaadddkk -- aaa indicates amount of sky covered, ddd distance over
        //                     aerodrome level, and kk the type of cloud.
        var myArray = reCloud.exec(token);
        add_output("Clouds: ");
        if(myArray[1] == "FEW") add_output("A few ");
        else if(myArray[1] == "SCT") add_output("Scattered ");
        else if(myArray[1] == "BKN") add_output("Broken sky ");
        else if(myArray[1] == "OVC") add_output("Overcast sky ");
       
        add_output(", at " + (100*parseInt(myArray[2],10)) + " feet above aerodrome level");

        if(myArray[3] == "CB") add_output(", cumulonimbus");
        else if(myArray[3] == "TCU") add_output(", towering cumulus");

        add_output("\n"); return; 
    }


    // Check if token is part of a wind-shear indication
    var reRWY = /^RWY(\d{2})(L|C|R)?$/;
    if(token=="WS")       { add_output("There is wind-shear in "); return; }
    else if(token=="ALL") { add_output("all "); return; }
    else if(token=="RWY") { add_output("runways\n"); return; }
    else if (reRWY.test(token))
    {
        var myArray = reRWY.exec(token);
        add_output("runway "+myArray[1]);
        if(myArray[2]=="L")      add_output(" Left");
        else if(myArray[2]=="C") add_output(" Central");
        else if(myArray[2]=="R") add_output(" Right");
        add_output("\n");
        return;
    }
    

    // Check if token is no-significant-weather indication
    if(token=="NSW")
    {
        add_output("No significant weather phenomena are observed at present\n");
        return;
    }


    // Check if token is no-significant-clouds indication
    if(token=="NSC")
    {
        add_output("No significant clouds are observed below 5000 feet or below the minimum sector altitude (whichever is higher)\n");
        return;
    }


    // Check if token is part of trend indication
    if(token=="BECMG")
    {
        add_output("The following weather phenomena are expected to arise soon:\n");
        return;
    }
    if(token=="TEMPO")
    {
        add_output("The following weather phenomena are expected to arise temporarily:\n");
        return;
    }
    var reFM = /^FM(\d{2})(\d{2})Z?$/;
    if(reFM.test(token))
    {
        var myArray = reFM.exec(token);
        add_output("    From "+myArray[1]+":"+myArray[2]+" UTC, ");
        return;
    }
    var reTL = /^TL(\d{2})(\d{2})Z?$/;
    if(reTL.test(token))
    {
        var myArray = reTL.exec(token);
        add_output("Until "+myArray[1]+":"+myArray[2]+" UTC, ");
        return;
    }
    var reAT = /^AT(\d{2})(\d{2})Z?$/;
    if(reAT.test(token))
    {
        var myArray = reAT.exec(token);
        add_output("At "+myArray[1]+":"+myArray[2]+" UTC, ");
        return;
    }


    // Check if item is runway state group
    var reRSG = /^(\d\d)(\d|C|\/)(\d|L|\/)(\d\d|RD|\/)(\d\d)$/;
    if(reRSG.test(token))
    {
        var myArray = reRSG.exec(token);
        add_output("Runway state:\n");

        // Runway designator (first 2 digits)
        var r = parseInt(myArray[1],10);
        if(r < 50) add_output("    Runway " + myArray[1] + " (or "+myArray[1]+" Left): ");
        else if(r < 88) add_output("    Runway " + (r-50) + " Right: ");
        else if(r == 88) add_output("    All runways: ");

        // Check if "CLRD" occurs in digits 3-6
        if(token.substr(2,4)=="CLRD") add_output("clear, ");
        else
        {
          // Runway deposits (third digit)
          if(myArray[2]=="0") add_output("clear and dry, ");
          else if(myArray[2]=="1") add_output("damp, ");
          else if(myArray[2]=="2") add_output("wet or water patches, ");
          else if(myArray[2]=="3") add_output("rime or frost covered, ");
          else if(myArray[2]=="4") add_output("dry snow, ");
          else if(myArray[2]=="5") add_output("wet snow, ");
          else if(myArray[2]=="6") add_output("slush, ");
          else if(myArray[2]=="7") add_output("ice, ");
          else if(myArray[2]=="8") add_output("compacted or rolled snow, ");
          else if(myArray[2]=="9") add_output("frozen ruts or ridges, ");
          else if(myArray[2]=="/") add_output("deposit not reported, ");

          // Extent of runway contamination (fourth digit)
          if(myArray[3]=="1") add_output("contamination 10% or less, ");
          else if(myArray[3]=="2") add_output("contamination 11% to 25%, ");
          else if(myArray[3]=="5") add_output("contamination 26% to 50%, ");
          else if(myArray[3]=="9") add_output("contamination 51% to 100%, ");
          else if(myArray[3]=="/") add_output("contamination not reported, ");

          // Depth of deposit (fifth and sixth digits)
          if(myArray[4]=="//") add_output("depth of deposit not reported, ");
          else
          {
              var d = parseInt(myArray[4],10);
              if(d == 0) add_output("deposit less than 1 mm deep, ");
              else if ((d >  0) && (d < 91)) add_output("deposit is "+d+" mm deep, ");
              else if (d == 92) add_output("deposit is 10 cm deep, ");
              else if (d == 93) add_output("deposit is 15 cm deep, ");
              else if (d == 94) add_output("deposit is 20 cm deep, ");
              else if (d == 95) add_output("deposit is 25 cm deep, ");
              else if (d == 96) add_output("deposit is 30 cm deep, ");
              else if (d == 97) add_output("deposit is 35 cm deep, ");
              else if (d == 98) add_output("deposit is 40 cm or more deep, ");
              else if (d == 99) add_output("runway(s) is/are non-operational due to snow, slush, ice, large drifts or runway clearance, but depth of deposit is not reported, ");
          }
        }

        // Friction coefficient or braking action (seventh and eighth digit)
        if(myArray[5]=="//") add_output("braking action not reported");
        else
        {
            var b = parseInt(myArray[5],10);
            if(b<91) add_output("friction coefficient 0."+myArray[5]);
            else
            {
                 if(b == 91) add_output("braking action is poor");
                 else if(b == 92) add_output("braking action is medium/poor");
                 else if(b == 93) add_output("braking action is medium");
                 else if(b == 94) add_output("braking action is medium/good");
                 else if(b == 95) add_output("braking action is good");
                 else if(b == 99) add_output("braking action figures are unreliable");
            }
        }
        add_output("\n"); return;
    } 

    if(token=="SNOCLO")
    {
        add_output("Aerodrome is closed due to snow on runways\n");
        return;
    }

    // Check if item is sea status indication
    reSea = /^W(M)?(\d\d)\/S(\d)/;
    if(reSea.test(token))
    {
        var myArray = reSea.exec(token);
        add_output("Sea surface temperature: ");
        if(myArray[1]=="M")
            add_output("-");
        add_output(parseInt(myArray[2],10) + " degrees Celsius\n");

        add_output("Sea waves have height: ");
        if(myArray[3]=="0") add_output("0 m (calm)\n");
        else if(myArray[3]=="1") add_output("0-0,1 m\n");
        else if(myArray[3]=="2") add_output("0,1-0,5 m\n");
        else if(myArray[3]=="3") add_output("0,5-1,25 m\n");
        else if(myArray[3]=="4") add_output("1,25-2,5 m\n");
        else if(myArray[3]=="5") add_output("2,5-4 m\n");
        else if(myArray[3]=="6") add_output("4-6 m\n");
        else if(myArray[3]=="7") add_output("6-9 m\n");
        else if(myArray[3]=="8") add_output("9-14 m\n");
        else if(myArray[3]=="9") add_output("more than 14 m (huge!)\n");
        return;
    }
}

function metar_decode(text)
{
    decoded_output = "";

    // Join newline-separated pieces...
    //    var newlineJoined = text.replace(/\n/, " ");
    var newlineJoined = text.replace(/(\x0d\x0a)|\x0D|\x0A/, " ");

    // An '=' finishes the report
    var equalPosition = newlineJoined.indexOf("=");
    if (equalPosition > -1)
    {
        alert("End of a METAR report is indicated by '='. We only decode until the first '='!!");
        newlineJoined = newlineJoined.substr(0,equalPosition);
    }
    
    var arrayOfTokens;
    arrayOfTokens = newlineJoined.split(" ");
    var numToken = 0;

    // Check if initial token is non-METAR date
    var reDate = /^\d\d\d\d\/\d\d\/\d\d/;
    if (reDate.test(arrayOfTokens[numToken]))
        numToken++;

    // Check if initial token is non-METAR time
    var reTime = /^\d\d:\d\d/;
    if (reTime.test(arrayOfTokens[numToken]))
        numToken++;

    // Check if initial token indicates type of report
    if(arrayOfTokens[numToken] == "METAR")
        numToken++;
    else if(arrayOfTokens[numToken] == "SPECI")
    {
        add_output("Report is a SPECIAL report\n");
        numToken++;
    }

    // Parse location token
    if (arrayOfTokens[numToken].length == 4)
    {
        add_output("Location: " + arrayOfTokens[numToken] + "\n");
        numToken++;
    }
    else
    {
        add_output("Invalid report: malformed location token '" + arrayOfTokens[numToken] + "' \n-- it should be 4 characters long!");
        return;
    }


    // Parse date-time token -- we allow time specifications without final 'Z'
    if ( (
           ( (arrayOfTokens[numToken].length == 7) &&
             (arrayOfTokens[numToken].charAt(6) == 'Z') ) ||
           ( arrayOfTokens[numToken].length == 6 )
         ) &&
         is_num_digit(arrayOfTokens[numToken].charAt(0)) &&
         is_num_digit(arrayOfTokens[numToken].charAt(1)) &&
         is_num_digit(arrayOfTokens[numToken].charAt(2)) &&
         is_num_digit(arrayOfTokens[numToken].charAt(3)) &&
         is_num_digit(arrayOfTokens[numToken].charAt(4)) &&
         is_num_digit(arrayOfTokens[numToken].charAt(5))    )
    {
        add_output("Day of month: " + arrayOfTokens[numToken].substr(0,2) + "\n");
        add_output("Time: " + arrayOfTokens[numToken].substr(2,2) +":" +
                              arrayOfTokens[numToken].substr(4,2) + " UTC");

        if(arrayOfTokens[numToken].length == 6)
            add_output(" (Time specification is non-compliant!)");

        add_output("\n");
        numToken++;
    }
    else
    {
        add_output("Time token not found or with wrong format!");
        return;
    }
    

    // Check if "AUTO" or "COR" token comes next.
    if (arrayOfTokens[numToken] == "AUTO")
    {
        add_output("Report is fully automated, with no human intervention or oversight\n");
        numToken++;
    }
    else if (arrayOfTokens[numToken] == "COR")
    {
        add_output("Report is a correction over a METAR or SPECI report\n");
        numToken++;
    }

    // Parse remaining tokens
    for (var i=numToken; i<arrayOfTokens.length; i++)
    {
        if(arrayOfTokens[i].length > 0)
        {
            decode_token(arrayOfTokens[i].toUpperCase());
        }
        /*else
        {
            add_output("Next token has 0 length\n");
        }*/
    }
}

  metar_decode(report);
  return decoded_output;
});