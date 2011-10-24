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
      <meta xmlns="" http-equiv="Content-Type" content="text/html; charset=utf-8"/>    
      <script xmlns="" src="http://localhost:8080/apps/appServer/resources/js/brackitHeader.js" type="text/javascript">""</script>    
      <script xmlns="" src="http://localhost:8080/apps/appServer/resources/js/codemirror.js" type="text/javascript">""</script>
      <script xmlns="" src="http://localhost:8080/apps/appServer/resources/js/jquery.min.js">""</script>
      <!-- add your meta tags here -->
      <link href="http://localhost:8080/apps/appServer/resources/css/layout_vertical_listnav.css" rel="stylesheet" type="text/css" />
      <!--[if lte IE 7]>
      <link href="http://localhost:8080/apps/appServer/resources/css/patches/patch_my_layout.css" rel="stylesheet" type="text/css" />
      <![endif]-->
    </head>
};

declare function header() as item() {
    <table style="width:100%;">
        <tr>
            <td>
                <div id="header" align="center">
                    <a href="http://localhost:8080/apps/appServer/controllers/appController/index">                  
                        <img align="center" src="http://localhost:8080/apps/appServer/resources/images/brackit.png" />
                    </a>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div align="center">
                    {
                    if (fn:string-length(session:getAttribute("msg")) gt 0) then
                        let 
                            $msg := session:getAttribute("msg")
                        return
                            if (session:removeAttribute("msg")) then
                                $msg
                            else
                                ""
                    else
                        ""
                    }
                </div>
            </td>
        </tr>
    </table>
};

declare function teaser() as item() {
    <div id="teaser" align="center">
      <h2>Brackit Application Server 1.0 </h2>
    </div>
};

declare function menu() as item() {
    <div id="col1_content" class="clearfix">
      <table style="width:100%;">
        <tr>
          <td>
            <ul class="vlist">
              <li><zu><a href="../appController/index"><h6>Apps</h6></a></zu></li>
              <li><zu><a href="../docController/index"><h6>Docs</h6></a></zu></li>
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
              <!-- IE Column Clearing -->
              <div id="ie_clearing"> &#160; </div>
            </div>
          </div>
          <!-- begin: #footer -->
            {$footerBrackit}
            {$footerYAML}
          <!-- begin: #footer -->
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

declare function footerScript() as item() {
    <script src="http://localhost:8080/apps/appServer/resources/js/brackit.js">""</script>
};

declare function baseFooterScript($head as item(),
                                  $header as item(),
                                  $teaser as item(),
                                  $menu as item(),
                                  $content as item(),
                                  $footerBrackit as item(),
                                  $footerYAML as item(),
                                  $footerScript as item()) as item() {

    <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    {$head}
    <body>
      {baseBody($header,$teaser,$menu,$content,$footerBrackit,$footerYAML)}
      {$footerScript}      
    </body>
    </html>
};