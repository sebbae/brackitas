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
 * @author Roxana Zapata
 * 
 *
:)
module namespace template="http://brackit.org/lib/eAuction/template";

declare function template:head() as item() 
{
    <head>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
        <title> Site name </title>
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/apps/eAuction/resources/css/eAuction.css"/>
    </head>
};

declare function template:header() as item() 
{
        <h1>
        
            eAuction
        </h1>
};

declare function template:navigation() as item() 
{
        <ul>
            <li><a href="#">My eAuction</a></li>
            <li><a href="./itemForm">Sell</a></li>
            <li><a href="#">Community</a></li>
            <li><a href="#">Customer Support</a></li>
        </ul>
};

declare function template:searching() as item() 
{
        <ul>
            <input type="text" name="valueSearch" size="60" />
            <input type="text" name="categorySearh" size="30" />    
            <input align="middle" type="submit" name="searchButton" value="Search" size="10" />
        </ul>
};

declare function template:section-navigation() as item() 
{
            <ul>
                <li><a href="./listAllCategories.xq">All Categories</a></li>
                <li><a href="#">Fashion</a></li>
                <li><a href="#">Motors</a></li>
                <li><a href="#">Electronics</a></li>
                <li><a href="#">Collectibles and Art</a></li>
                <li><a href="#">Home, Outdoors and Decor</a></li>
                <li><a href="#">Movies, Music and Games</a></li>
                <li><a href="#">Deals and Gifts</a></li>
                <li><a href="#">Classifieds</a></li>
            </ul>   
};

declare function template:content() as item() 
{
            <h2>
                Welcome to eAuction
            </h2>
};

declare function template:aside() as item() 
{
            <p>
                <l> Welcome </l>
                <a href="./signIn"> Sign in</a>
                <l> or </l>
                <a href="./userForm"> Register</a>
            </p>
};


declare function template:footer() as item() 
{
    <p> Copyright eAuction - Brackit, 2011 </p>
};

declare function template:default($content as item() ) as item()* 
{
    <html xmlns="http://www.w3.org/1999/xhtml">
    {template:head()}
    <body>
        <div id="container">
            <div id="navigation">
                {template:navigation()}
            </div>
            <div id="header">
                  {template:header()}
            </div>
            <div id="searching">
                {template:searching()}
            </div>
            <div id="content-container">
                <div id="section-navigation">
                    {template:section-navigation()}
                </div>
                <div id="content">
                    {$content}
                </div>
                <div id="aside">
                    {template:aside()}
                </div>
                <div id="footer">
                    {template:footer()}
                </div>
            </div>
        </div> 
    </body>
    </html>
};