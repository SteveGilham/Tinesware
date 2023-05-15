	function doApplet()
	{
		if(navigator.appName.indexOf('icrosoft') != -1)
		{

			text=
'<object title="&lambda; Serpentis II system simulation" code="Nbody" archive="nbodyzip.zip" type="application/x-java-object" width="250" height="150" >'+
'<p>Alas! Either your browser is not one for which I\'ve been able to get '+
'XHTML object tags to work, or perhaps it\'s not Java enabled!<\/p>'+
'<\/object><br /><br /><br /><br />';

			document.getElementById('applet').innerHTML=text;


		}
		else if (navigator.userAgent.indexOf('Gecko') == -1 && !document.layers)
		{
			text=
'<applet title="lambda Serpentis II system simulation" code="Nbody.class" archive="nbodyzip.zip" height="150" width="250" >'+
'Alas! Your browser is not Java enabled!<\/applet>';

			document.getElementById('applet').innerHTML=text;

		};

	};

	function doAll()
	{
		doMail();
		doApplet();
	};
