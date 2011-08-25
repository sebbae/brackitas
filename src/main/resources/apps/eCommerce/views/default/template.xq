declare variable $head external;
declare variable $header as xs:string external;
declare variable $menu as xs:string external;
declare variable $content as xs:string external;
declare variable $footer as xs:string external;

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
{bit:eval($head);}
<body>
  <div class="page_margins">    
    <div id="border-top">
      <div id="edge-tl"></div>
      <div id="edge-tr"></div>
    </div>
	<div class="page">
	  <div id="header" align="center">
		{bit:eval($header);}
	  </div>
	  <div id="main">
	    <div id="col1">
		  <div id="nav">
		    <a id="navigation" name="navigation"></a>
		    <div class="vlist">
		  	  {bit:eval($menu);}
		    </div>
		  </div>	    
	    </div>
	    <div id="col3">
	      <div id="col3_content" class="clearfix">
		  	{bit:eval($content);}
	      </div>
	      <div id="ie_clearing">
	      </div>
	    </div>
	  </div>
	  <div id="footer">
		{bit:eval($footer);}
	  </div>
	</div>
    <div id="border-bottom">
      <div id="edge-bl"></div>
      <div id="edge-br"></div>
    </div>
  </div>
</body>
</html>