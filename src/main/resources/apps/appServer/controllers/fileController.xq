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

module namespace controller="http://brackit.org/lib/appServer/fileController";
import module namespace model="http://brackit.org/lib/appServer/fileModel";
import module namespace view="http://brackit.org/lib/appServer/fileView";
import module namespace appController="http://brackit.org/lib/appServer/appController";
import module namespace appView="http://brackit.org/lib/appServer/appView";
import module namespace rscController="http://brackit.org/lib/appServer/rscController";

declare function controller:save() as item() {
    let $fPathName := req:get-parameter("name"),
        $query := req:get-parameter("query")
    return
        let $msg := 
            if (xqfile:save($fPathName, $query)) then
                view:msgSuccess("Saved sucessfully!")
            else
                view:msgFailure("Problems while saving...")
        return
            appController:loadAfterAction($msg)
}; 

declare function controller:compile() as item() {
    let $fPathName := req:get-parameter("name"),
        $query := req:get-parameter("query")
    return
        let $msg := 
            try {
                if (xqfile:compile($fPathName, $query)) then
                    view:msgSuccess("Compiled sucessfully!")
                else
                    view:msgFailure("Compilation failed ... ")
            } catch * {
                view:msgFailure(fn:concat("Compilation failed: ", $err:description))
            }        
       return
            appController:loadAfterAction($msg)
};

declare function controller:delete() as item() {
    let $fPathName := req:get-parameter("name"),
        $app := req:get-parameter("app")
    return
        let $msg := 
            if (xqfile:delete($fPathName)) then
                view:msgSuccess("Deleted sucessfully!")
            else
                view:msgSuccess("Deletion failed!")
        return
            appView:menuContent(appView:createMenu($app),$msg)
};

declare function controller:action() as item() {
    let $action := fn:normalize-space(req:get-parameter("action"))
    return
        if (fn:compare($action,"save") eq 0) then
            controller:save()
        else 
            if (fn:compare($action,"compile") eq 0) then
                controller:compile()
            else
                if (fn:compare($action,"delete") eq 0) then
                    controller:delete()
                else
                    if (fn:compare($action,"rename") eq 0) then
                        rscController:rename()
                    else
                        "ops"
};

declare function controller:create() as item() {
    let $butClick := req:get-parameter("sub"),
        $fBasePath := req:get-parameter("name"),
        $app := req:get-parameter("app"),
        $menu := appView:createMenu($app),
        $fName := req:get-parameter("fName"),
        $fPathName := fn:concat($fBasePath,"/",$fName)
    return
        if (fn:string-length($butClick) > 0) then
            if (model:validateXQFile($fName)) then
                if (xqfile:create($fPathName)) then
                    appView:menuContent(appView:createMenu($app),
                                       appView:editQuery($fPathName,$app,fn:true()))
                else
                    appView:menuContent($menu,view:msgFailure("Problems creating new file"))
            else
                let $msg := 
                    view:msgFailure("File name cannot contain space and must finish with .xq")
                return
                    appView:menuContent($menu,view:createFileFormMsg($fBasePath,$app,$msg))
        else
            appView:menuContent($menu,view:createFileForm($fBasePath,$app))
};

declare function controller:upload() as item() {
    let $butClick := req:get-parameter("sub"),
        $fBasePath := req:get-parameter("name"),
        $app := req:get-parameter("app"),
        $menu := appView:createMenu($app)
    return
        if (fn:string-length($butClick) > 0) then
            if (rsc:upload($fBasePath,"fName")) then
                appView:menuContent(appView:createMenu($app),view:msgSuccess("File uploaded sucessfully!"))
            else
                appView:menuContent($menu,view:msgFailure("Problems uploading file!"))
        else
            appView:menuContent($menu,view:createUploadForm($fBasePath,$app))
};

declare function controller:mkDir() as item() {
    let $butClick := req:get-parameter("sub"),
        $fBasePath := req:get-parameter("name"),
        $app := req:get-parameter("app"),
        $dirName := req:get-parameter("dir"),
        $menu := appView:createMenu($app)
    return
        if (fn:string-length($butClick) > 0) then
            if (model:validateDirName($dirName)) then 
                if (util:mk-dir(fn:concat($fBasePath,"/",$dirName))) then
                    appView:menuContent(appView:createMenu($app),view:msgSuccess("Directory created sucessfully!"))
                else
                    appView:menuContent($menu,view:msgFailure("Problems creating directory!"))
            else
                let $msg :=
                    view:msgFailure("Directory name cannot be empty or contain space")
                return
                    appView:menuContent($menu,view:createDirFormMsg($fBasePath,$app,$msg))
        else
            appView:menuContent($menu,view:createDirForm($fBasePath,$app))
};