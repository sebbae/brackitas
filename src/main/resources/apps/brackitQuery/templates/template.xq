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
module namespace template="http://brackit.org/lib/brackitQuery/template";

declare function head() as item()+
{
    <head>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
        <title> AppName </title>
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/apps/brackitQuery/resources/css/brackit.css"/>
    </head>
};

declare function header() as item()+
{
    <table>
        <tr>
            <td align="center" colspan="2">
                <img align="center" src="http://localhost:8080/apps/brackitQuery/resources/images/brackit.png" />
            </td>
        </tr>
    </table>
};

declare function menu() as item()+
{
    <ul>
        <li><a href="./query">Brackit Query</a></li>
        <li><a href="./procedures">Brackit Procedures</a></li>
        <li><a href="./upload">Upload Files</a></li>
        <li><a href="./download">Download Files</a></li>
    </ul>
};

declare function content() as item()+
{
    <table style="width: 100%; background-color: #E0E0F0;">
        <tr>
            <td>
              Welcome to Brackit XQuery application
            </td>
        </tr>                 
    </table>
};

declare function footer() as item()+
{
    <a href="http://brackit.org">Brackit XQuery engine</a>
};

declare function default($content as item()+) as item()* 
{
    <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    {head();}
    <body>
        <div class="page_margins">    
            <div id="border-top">
                <div id="edge-tl"></div>
                <div id="edge-tr"></div>
            </div>
            <div class="page">
                <div id="header" align="center">
                    {header();}
                </div>
                <div id="main">
                    <div id="col1">
                        <div id="nav">
                            <a id="navigation" name="navigation"></a>
                            <div class="vlist">
                                {menu();}
                            </div>
                        </div>        
                    </div>
                    <div id="col3">
                        <div id="col3_content" class="clearfix">
                            {$content;}
                        </div>
                        <div id="ie_clearing">
                        </div>
                    </div>
                </div>
                <div id="footer">
                    {footer();}
                </div>
            </div>
            <div id="border-bottom">
                <div id="edge-bl"></div>
                <div id="edge-br"></div>
            </div>
        </div>
    </body>
    </html>
};
