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
module namespace appController="http://brackit.org/lib/appServer/appController";
import module namespace appModel="http://brackit.org/lib/appServer/appModel";
import module namespace appView="http://brackit.org/lib/appServer/appView";
import module namespace rscController="http://brackit.org/lib/appServer/rscController";

declare function appController:index() as item() {
    appView:listApps(app:get-names())
};

declare function appController:edit() as item ()* {
    let $app := req:get-parameter("app")
    return
        if (appModel:validateApp($app)) then
            if (session:set-attribute("editApp",$app)) then
                let 
                    $menu := appView:createMenu($app)
                return 
                    appView:menuContent($menu, "Welcome to the development Framework")
            else
                appView:default(fn:concat("Problems editing application ",$app))
        else
            appView:default(fn:concat("Application ",$app," does not exist."))
};

declare function template:head() as item() {
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="http://localhost:8080/apps/highlighting2/resources/css/highlighting2.css" rel="stylesheet" type="text/css"/>
    <link href="http://localhost:8080/apps/highlighting2/resources/css/codemirror.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="http://localhost:8080/apps/highlighting2/resources/js/codemirror.js"/>
    <script type="text/javascript" src="http://localhost:8080/apps/highlighting2/resources/js/clike.js"/>
    <script type="text/javascript" src="http://localhost:8080/apps/highlighting2/resources/js/simple-hint.js"/>
    <script type="text/javascript" src="http://localhost:8080/apps/highlighting2/resources/js/xquery-hint.js"/>
  </head>
};