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
module namespace controller="http://brackit.org/lib/appServer/appController";
import module namespace model="http://brackit.org/lib/appServer/appModel";
import module namespace view="http://brackit.org/lib/appServer/appView";

declare function index() as item() {
    view:listApps(app:getNames())
};

declare function create() as item() {
    let $butClick := req:getParameter("sub")
    return
        if (fn:string-length($butClick) > 0) then
            let $app := req:getParameter("app"),
                $model := req:getParameter("model")
            return
                if (model:validateAppCreation($app, $model)) then
                    if (app:generate($app,$model)) then
                        index()
                    else
                        view:default(view:createAppFormError("Impossible to generate the required application"))
                else
                    view:default(view:createAppFormError("Parameters name or model type are not valid."))
        else
            view:default(view:createAppForm())
};

declare function edit () as item ()* {
    let $app := req:getParameter("app")
    return
        if (model:validateApp($app)) then
            if (session:setAttribute("editApp",$app)) then
                let $menu := view:createMenu($app)
                return view:menuContent($menu, "Welcome to the development Framework")
            else
                view:default(fn:concat("Problems editing application ",$app))
        else
            view:default(fn:concat("Application ",$app," does not exist."))
};

declare function terminate() as item() {
    let $app := req:getParameter("app")
    return
        if (app:terminate($app)) then
            index()
        else
            view:default(fn:concat("Problems terminating application ",$app))
};

declare function statistics() as item() {
    "TODO"
};

declare function delete() as item() {
    view:delete(app:delete(req:getParameter("app")))
};

declare function deploy() as item() {
    let $app := req:getParameter("app")
    return
        if (app:deploy($app)) then
            index()
        else
            view:default(fn:concat("Problems deploying application ",$app))
};

declare function load($item as xs:string) as item () {
    (: 3 cases: 1. resources, 2. xquery and 3. MVC query
    1. appServer/resources/images/brackit.png
    2. else
    3. appServer/controllers/docController.xq
     :)
    let $app := fn:normalize-space(req:getParameter("app")),
        $resource := fn:normalize-space(req:getParameter("name")),
        $menu := view:createMenu($app)
    return 
        let $content :=
            if (fn:contains($resource, "/resources/")) then
                "Treat resource call"
            else
                if (fn:ends-with($resource, ".xq")) then
                    if (fn:contains($resource, "/controllers/")) then
                        let $model := fn:replace(fn:replace($resource, "controllers", "models"),"Controller","Model"),
                            $view := fn:replace(fn:replace($resource, "controllers", "views"),"Controller","View"),
                            $controller := $resource 
                        return view:editMVC($model,$view,$controller)
                    else
                        if (fn:contains($resource, "/models/")) then
                            let $model := $resource,
                                $view := fn:replace(fn:replace($resource, "models", "views"),"Model","View"),
                                $controller := fn:replace(fn:replace($resource, "models", "controllers"),"Model","Controller") 
                            return view:editMVC($model,$view,$controller)
                        else
                            if (fn:contains($resource, "/views/")) then
                                let $model := fn:replace(fn:replace($resource, "views", "models"),"View","Model"),
                                    $view := $resource,
                                    $controller := fn:replace(fn:replace($resource, "views", "controllers"),"View","Controller") 
                                return view:editMVC($model,$view,$controller)
                            else
                                view:editQuery($resource)
                else
                    "Resource not handled"
        return
            view:editXQuery($menu,$content)
};