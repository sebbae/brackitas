(:
 *
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 **
 * 
 * @author Henrique Valer
 * 
 *
:)
module namespace template="http://brackit.org/lib/appServer/template";

declare function head($title as xs:string) as item() {
    <head>
      <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
      <title>{$title}</title>
      <!-- add your meta tags here -->
      <link href="http://localhost:8080/apps/appServer/resources/css/my_layout.css" rel="stylesheet" type="text/css" />
      <!--[if lte IE 7]>
      <link href="http://localhost:8080/apps/appServer/resources/css/patches/patch_my_layout.css" rel="stylesheet" type="text/css" />
      <![endif]-->
    </head>
};

declare function header() as item() {
    <div id="header" align="center">
      <img align="center" src="http://localhost:8080/apps/appServer/resources/images/brackit.png" />
    </div>
};

declare function teaser() as item() {
    <div id="teaser" align="center">
      <h2>Brackit Application Server 1.0 </h2>
    </div>
};

declare function menu() as item() {
    <div id="col1_content" class="clearfix">
      <ul>
        <li><a href="../appController/index">Apps</a></li>
        <li><a href="../docController/index">Docs</a></li>
      </ul>
    </div>
};

declare function footerBrackit() as item() {
    <div id="footer">
      <a href="http://brackit.org">Brackit XQuery engine</a>
    </div>          
};

declare function footerYAML() as item() {
    <div id="footer">
      Layout based on <a href="http://www.yaml.de/">YAML</a>
    </div>          
};

declare function default($content as item()) as item() {
    <html xmlns="http://www.w3.org/1999/xhtml" lang="en" lang="en">
    {head("Brackit Application Server");}
    <body>
      <div class="page_margins">
        <div class="page">
          <div id="topnav">
            <!-- start: skip link navigation -->
            <a class="skip" title="skip link" href="#navigation">Skip to the navigation</a><span class="hideme">.</span>
            <a class="skip" title="skip link" href="#content">Skip to the content</a><span class="hideme">.</span>
            <!-- end: skip link navigation --><a href="#">Login</a> | <a href="#">Contact</a> | <a href="#">Imprint</a>
          </div>
            {header();}
          <div id="nav">
            <!-- skiplink anchor: navigation -->
            <a id="navigation" name="navigation"></a>
            <div class="hlist">
              <!-- main navigation: horizontal list -->
              <ul>
                <li class="active"><strong>Button 1</strong></li>
                <li><a href="#">Button 2</a></li>
                <li><a href="#">Button 3</a></li>
                <li><a href="#">Button 4</a></li>
                <li><a href="#">Button 5</a></li>
              </ul>
            </div>
          </div>
            {teaser();}
          <div id="main">
            <div id="col1">
              {menu();}
            </div>
            <div id="col3">
              <div id="col3_content" class="clearfix">
                {$content;}
              </div>
              <!-- IE Column Clearing -->
              <div id="ie_clearing"> &#160; </div>
            </div>
          </div>
          <!-- begin: #footer -->
            {footerBrackit();}
            {footerYAML();}
          <!-- begin: #footer -->
        </div>
      </div>
    </body>
    </html>
};