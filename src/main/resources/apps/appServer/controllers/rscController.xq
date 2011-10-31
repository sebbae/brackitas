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
module namespace rscController="http://brackit.org/lib/appServer/rscController";
import module namespace rscView="http://brackit.org/lib/appServer/rscView";
import module namespace view="http://brackit.org/lib/appServer/fileView";
import module namespace appView="http://brackit.org/lib/appServer/appView";
import module namespace rscModel="http://brackit.org/lib/appServer/rscModel";

declare function load() as item() {
    let $app := req:getParameter("app"),
        $base := req:getParameter("name"),
        $butClick := req:getParameter("renBut")
    return
        rscView:fileForm($base,$app)
};

declare function rename() as item() {
    let $butClick := req:getParameter("sub"),
        $fPathName := req:getParameter("name"),
        $app := req:getParameter("app"),
        $newName := req:getParameter("newName")
    return
        let 
            $content :=
            if (fn:string-length($butClick) > 0) then
                if (rscModel:validateFileName($newName)) then
                    if (rsc:rename($fPathName,$newName)) then
                        view:msgSuccess("Renamed sucessfully!")
                    else
                        view:msgSuccess("Problems while renaming!")
                else
                    view:msgSuccess("The new name cannot contain space, slash or be empty!")
            else
                rscView:renameFileForm($fPathName,$app)
        return
            appView:menuContent(appView:createMenu($app),$content)            
};

declare function delete() as item() {
    let $fPathName := req:getParameter("name"),
        $app := req:getParameter("app"),
        $menu := appView:createMenu($app) 
    return
        let $msg := 
            if (rsc:delete($fPathName)) then
                view:msgSuccess("Deleted sucessfully!")
            else
                view:msgSuccess("Deletion failed!")
        return
            appView:menuContent($menu,$msg)
};

declare function action() as item() {
    let $action := fn:normalize-space(req:getParameter("action"))
    return
        if (fn:compare($action,"rename") eq 0) then
            rename()
        else 
            if (fn:compare($action,"delete") eq 0) then
                delete()
            else
                "ops"
};
