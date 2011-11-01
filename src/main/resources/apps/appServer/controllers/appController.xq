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

declare function index() as item() {
    appView:listApps(app:getNames())
};

declare function create() as item() {
    let $butClick := req:getParameter("sub")
    return
        if (fn:string-length($butClick) > 0) then
            let $app := req:getParameter("app"),
                $model := req:getParameter("model")
            return
                if (appModel:validateAppCreation($app, $model)) then
                    if (app:generate($app,$model)) then
                        index()
                    else
                        appView:default(appView:createAppFormError("Impossible to generate the required application"))
                else
                    appView:default(appView:createAppFormError("Parameters name or model type are not valid."))
        else
            appView:default(appView:createAppForm())
};

declare function edit() as item ()* {
    let $app := req:getParameter("app")
    return
        if (appModel:validateApp($app)) then
            if (session:setAttribute("editApp",$app)) then
                let 
                    $menu := appView:createMenu($app)
                return 
                    appView:menuContent($menu, "Welcome to the development Framework")
            else
                appView:default(fn:concat("Problems editing application ",$app))
        else
            appView:default(fn:concat("Application ",$app," does not exist."))
};

declare function terminate() as item() {
    let $app := req:getParameter("app")
    return
        if (app:terminate($app)) then
            index()
        else
            appView:default(fn:concat("Problems terminating application ",$app))
};

declare function statistics() as item() {
    "TODO"
};

declare function delete() as item() {
    appView:delete(app:delete(req:getParameter("app")))
};

declare function deploy() as item() {
    let $app := req:getParameter("app")
    return
        if (app:deploy($app)) then
            index()
        else
            appView:default(fn:concat("Problems deploying application ",$app))
};

declare function load() as item() {
    let $app := req:getParameter("app"),
        $resource := fn:normalize-space(req:getParameter("name")),
        $menu := appView:createMenu($app)
    return 
        let $content :=
            if (fn:ends-with($resource, ".xq")) then
                appView:editQuery($resource,$app)
            else
                rscController:load()
        return
            appView:editXQuery($menu,$content)
};

declare function loadAfterAction($msg as xs:string) as item() {
    let $app := req:getParameter("app"),
        $resource := fn:normalize-space(req:getParameter("name")),
        $menu := appView:createMenu($app)
    return 
        let $content :=
            if (fn:ends-with($resource, ".xq")) then
                appView:editQueryAfterAction($resource,$app,$msg)
            else
                rscController:load()
        return
            appView:editXQuery($menu,$content)
};