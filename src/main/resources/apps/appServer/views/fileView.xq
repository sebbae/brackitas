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
module namespace view="http://brackit.org/lib/appServer/fileView";
import module namespace template="http://brackit.org/lib/appServer/template";

declare function msgSuccess($msg as xs:string) as item() {
    <font color="#008000">{$msg}</font>
};

declare function msgFailure($msg as xs:string) as item() {
    <font color="#ff0000">{$msg}</font>
};

declare function createFileForm($fPath as xs:string,
                                $app as xs:string) as item() {
    <form action="./create">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td style="width: 20%;"><h5>Name</h5></td>
                <td><input type="text" name="fName"/></td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Under</h5></td>
                <td><input type="text" readonly="readonly" name="name" value="{$fPath}"/></td>
            </tr>            
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Create file"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};