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
module namespace controller="http://brackit.org/lib/brackitQuery/brackitController";
import module namespace model="http://brackit.org/lib/brackitQuery/brackitModel";
import module namespace view="http://brackit.org/lib/brackitQuery/brackitView";

declare variable $controller:query as xs:string external;

declare function controller:getSecondsFromDayTimeDuration($d as xs:dayTimeDuration) as xs:integer
{
    fn:days-from-duration($d) * 24 * 60 * 60 +
    fn:hours-from-duration($d) * 60 * 60 +
    fn:minutes-from-duration($d) * 60 +
    fn:seconds-from-duration($d)
};

declare function controller:query() as item()* 
{
    if (fn:string-length($controller:query) = 0) then
        view:showQueryResultTime("","",0)
    else
        let
            $oldTime := fn:current-time(),
            $result := bit:eval($controller:query)
        return
            view:showQueryResultTime($controller:query, $result, controller:getSecondsFromDayTimeDuration(fn:current-time() - $oldTime))
};

declare function controller:procedures() as item()* 
{
    "TODO"
};

declare function controller:upload() as item()*
{
    view:showUpload()
};

declare function controller:download() as item()*
{
    "TODO"
};