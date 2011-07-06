<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <title> AppName </title>
</head>
<body>
  <div class="page_margins">    
    <div id="border-top">
      <div id="edge-tl"></div>
      <div id="edge-tr"></div>
    </div>
	<div class="page">
	  <div id="header" align="center">
		{bit:render('header');}
	  </div>
	  <div id="main">
	    <div id="col1">
		  <div id="nav">
		    <a id="navigation" name="navigation"></a>
		    <div class="vlist">
		  	  {bit:render('menu');}
		    </div>
		  </div>	    
	    </div>
	    <div id="col3">
	      <div id="col3_content" class="clearfix">
		  	{bit:render('content');}
	      </div>
	      <div id="ie_clearing">
	      </div>
	    </div>
	  </div>
	  <div id="footer">
		{bit:render('footer');}
	  </div>
	</div>
    <div id="border-bottom">
      <div id="edge-bl"></div>
      <div id="edge-br"></div>
    </div>
  </div>
</body>
</html>