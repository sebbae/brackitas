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

declare function appController:create() as item() {
    let $butClick := req:get-parameter("sub")
    return
        if (fn:string-length($butClick) > 0) then
            let $app := req:get-parameter("app"),
                $model := req:get-parameter("model")
            return
                if (appModel:validateAppCreation($app, $model)) then
                    if (app:generate($app,$model)) then
                        appController:index()
                    else
                        appView:default(appView:createAppFormError("Impossible to generate the required application"))
                else
                    appView:default(appView:createAppFormError("Parameters name or model type are not valid."))
        else
            appView:default(appView:createAppForm())
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

declare function appController:terminate() as item() {
    let $app := req:get-parameter("app")
    return
        if (app:terminate($app)) then
            appController:index()
        else
            appView:default(fn:concat("Problems terminating application ",$app))
};

declare function appController:statistics() as item() {
    "TODO"
};

declare function appController:delete() as item() {
    appView:delete(app:delete(req:get-parameter("app")))
};

declare function appController:deploy() as item() {
    let $app := req:get-parameter("app")
    return
        if (app:deploy($app)) then
            appController:index()
        else
            appView:default(fn:concat("Problems deploying application ",$app))
};

declare function appController:load() as item() {
    let $app := req:get-parameter("app"),
        $resource := fn:normalize-space(req:get-parameter("name")),
        $menu := appView:createMenu($app)
    return 
        let $error := xqfile:get-compilation-error($resource)
        return
            if (fn:string-length($error) gt 0) then
                appController:loadAfterAction($error)
            else
                let $content :=
                    if (fn:ends-with($resource, ".xq")) then
                        appView:editQuery($resource,$app,fn:true())
                    else
                        if (fn:starts-with(util:get-mime-type($resource),"text")) then
                            appView:editQuery($resource,$app,fn:false())
                        else
                            rscController:load()
                return
                    appView:menuContent($menu,$content)
};

declare function appController:loadAfterAction($msg as xs:string) as item() {
    let $app := req:get-parameter("app"),
        $resource := fn:normalize-space(req:get-parameter("name")),
        $menu := appView:createMenu($app)
    return 
        let $content :=
            if (fn:ends-with($resource, ".xq")) then
                appView:editQueryAfterAction($resource,$app,$msg,fn:true())
            else
                if (fn:starts-with(util:get-mime-type($resource),"text")) then
                    appView:editQueryAfterAction($resource,$app,$msg,fn:false())
                else
                    rscController:load()
        return
            appView:menuContent($menu,$content)
};