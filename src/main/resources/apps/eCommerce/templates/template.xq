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
module namespace template="http://brackit.org/lib/eCommerce/template";

declare function template:content() as item()+
{
    <table style="width: 100%; background-color: #E0E0F0;">
        <tr>
            <td>
              Welcome to eCommerce application
            </td>
        </tr>                 
    </table>
};

declare function template:footer() as item()+
{
    <a href="http://brackit.org">Brackit XQuery engine</a>
};

declare function template:head() as item()+
{
    <head>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
        <title> AppName </title>
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/apps/eCommerce/resources/css/brackit.css"/>
    </head>
};

declare function template:header() as item()+
{
    let 
        $login := session:getAttribute('login')
    let 
        $pass := session:getAttribute('pass')
    return
        <table>
            <tr>
                <td>
                    <img style="width:200px; height:75px; padding: 10px;" align="middle" src="http://localhost:8080/apps/eCommerce/resources/images/ecommerce.jpg" />
                </td>
                <td>
                    {
                    if ((fn:string-length($login) > 0) and (fn:string-length($pass) > 0)) then
                        <p> Welcome {$login} <a href="./logoutExec.xq">Logout</a> </p> 
                    else
                        <form action="./loginExec.xq">
                            <table style="width: 100%;">
                                <tr>
                                    <td>
                                      <b>Login</b>
                                      <input type="text" name="login" value="{if ($login) then $login else ''}"/>
                                    </td>
                                    <td>
                                      <b>Pass</b>
                                      <input type="text" name="pass" value="{if ($pass) then $pass else ''}"/>
                                    </td>
                                    <td>
                                      <input align="middle" type="submit" name="subButton" value="Login"/>
                                    </td>
                                </tr>
                            </table>
                        </form>
                    }
                </td>
            </tr>
        </table>
};

declare function template:menu() as item()+
{
    <ul>
      <li><a href="./createItemForm.xq">Create Items</a></li>
      <li><a href="./listItems.xq">List Items</a></li>
      <li><a href="./showCartForm.xq">Show my Items Cart</a></li>
    </ul>
};

declare function template:default($content as item()+) as item()* 
{
    <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
    {template:head()}
    <body>
        <div class="page_margins">    
            <div id="border-top">
                <div id="edge-tl"></div>
                <div id="edge-tr"></div>
            </div>
            <div class="page">
                <div id="header" align="center">
                    {template:header()}
                </div>
                <div id="main">
                    <div id="col1">
                        <div id="nav">
                            <a id="navigation" name="navigation"></a>
                            <div class="vlist">
                                {template:menu()}
                            </div>
                        </div>        
                    </div>
                    <div id="col3">
                        <div id="col3_content" class="clearfix">
                            {$content}
                        </div>
                        <div id="ie_clearing">
                        </div>
                    </div>
                </div>
                <div id="footer">
                    {template:footer()}
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

declare function template:getItemFromCollection ($name as xs:string) as item()+
{
    for 
        $doc 
    in 
        fn:collection(session:getAttribute('appName'))
    let 
        $docName := $doc/item/fn:data(name)
    where
        $docName = $name 
    return 
        $doc    
}
;

declare function template:showCart() as item()* 
{
let
    $cart := session:getAttribute('cart')
return
    <table>
        <tr>
            <td>
                Name
            </td>
            <td>
                Description
            </td>
            <td>
                Quantity
            </td>
        </tr>
        {
            for 
                $cartItem 
            in 
                $cart/item
            let
                $docName := $cartItem/fn:data(name),
                $quantity := $cartItem/fn:data(quantity),
                $description := template:getItemFromCollection($docName)/item/fn:data(description)
            return 
                <tr>
                    <td>
                        <a href="./showItemForm.xq?itemName={$docName}">{$docName}</a>
                    </td> 
                    <td>
                        {$description}
                    </td>
                    <td>
                        {$quantity}
                    </td>
                </tr>
        }
    </table>
};