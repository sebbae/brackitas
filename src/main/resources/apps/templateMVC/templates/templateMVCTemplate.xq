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
 :)
module namespace template="http://brackit.org/lib/templateMVC/templateMVCTemplate";
 
declare function head() as item() {
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>    
    <link href="http://localhost:8080/apps/templateMVC/resources/css/layoutTemplateMVC.css" rel="stylesheet" type="text/css" />
  </head>
};

declare function header() as item() {
  <table style="width:100%;">
    <tr>
      <td>
        <div id="header" align="center">
          <h1>Insert header here!</h1>
        </div>
      </td>
    </tr>
  </table>
};

declare function teaser() as item() {
  <div id="teaser" align="center">
    <h2>Insert teaser here!</h2>
  </div>
};

declare function menu() as item() {
  <div id="col1_content" class="clearfix">
    <table style="width:100%;">
      <tr>
        <td>
          <ul class="vlist">
            <li><zu><a href="./index"><h6>Link 1</h6></a></zu></li>
            <li><zu><a href="./index"><h6>Link 2</h6></a></zu></li>
          </ul>
        </td>
      </tr>
    </table>
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

declare function baseBody($header as item(),
                          $teaser as item(),
                          $menu as item(),
                          $content as item(),
                          $footerBrackit as item(),
                          $footerYAML as item()) as item() {
  <div class="page_margins">
    <div class="page">
      {$header}
      {$teaser}
      <div id="main">
        <div id="col1" role="complementary">
          {$menu}
        </div>
        <div id="col3">
          <div id="col3_content" class="clearfix">
            {$content}
          </div>
          <div id="ie_clearing">   </div>
        </div>
      </div>
      {$footerBrackit}
      {$footerYAML}
    </div>
  </div>
};

declare function base($head as item(),
                      $header as item(),
                      $teaser as item(),
                      $menu as item(),
                      $content as item(),
                      $footerBrackit as item(),
                      $footerYAML as item()) {

  <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    {$head}
    <body>
      {baseBody($header,$teaser,$menu,$content,$footerBrackit,$footerYAML)}
    </body>
  </html>
};