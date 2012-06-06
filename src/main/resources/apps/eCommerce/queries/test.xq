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
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <script src="http://github.com/mbrevoort/CodeMirror/raw/master/js/codemirror.js" type="text/javascript"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <title>CodeMirror: XQuery highlighting demonstration</title>
    <link rel="stylesheet" type="text/css" href="http://localhost:8080/eCommerce/resources/css/jscolors.css"/>
  </head>

  <body style="padding: 20px;">

<div style="margin:auto; width:920px; border-width:1px;">
<h2>XQuery Syntax Support for CodeMirror</h2>
<p style="text-align: justify;  word-spacing: 3px;">This is a demonstration of the XQuery highlighting module
for <a href="http://codemirror.net">CodeMirror</a>. The formatting is CSS driven and very easy to customize to your liking.
There are three sample styles sets below.
You can edit or paste in any code below to give it a test run. 
The <a href="http://github.com/mbrevoort/CodeMirror/tree/master/contrib/xquery/">code</a> is at Github in a fork of the CodeMirror repository. A pull request has been made back to the primary.
</p>

<a href="#" rel="xqcolors.css" class="css-switch">Light 1</a>  <a href="#" rel="xqcolors2.css" class="css-switch">Light 2</a> <a href="#" rel="xqcolors-dark.css" class="css-switch">Dark</a> 


<div class="border">
<code><pre><textarea id="code" cols="120" rows="50"> {util:plain-print(io:read(fn:concat(util:get-property("apps.directory"),'eCommerce/queries/buyCartExec.xq')))} </textarea>
</pre></code>
</div>
<div style="width:100%;text-align:center;padding-top:15px;">
    Developed by <a href="http://mike.brevoort.com">Mike Brevoort</a> (<a href="http://twitter.com">@mbrevoort</a>) 
    <br/>
    <small><a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License, Version 2.0</a></small></div>

</div>
<script src="http://localhost:8080/eCommerce/resources/js/test.js" type="text/javascript"></script>
  </body>
</html>


