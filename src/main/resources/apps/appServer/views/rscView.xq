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
 :)
(: TODO Auto-generated XQuery block :) 
module namespace rscView="http://brackit.org/lib/appServer/rscView";

declare function fileForm($fPathName as xs:string,
                          $app as xs:string) as item() {
    <form action="../rscController/action">                            
      <div class="hlist">
        <ul>
          <li>
            <strong> File: {$fPathName}  </strong>
          </li>        
          <li>
            <input align="middle" type="submit" name="action" value="rename"/>
          </li>
          <li>
            <input align="middle" type="submit" name="action" value="test it"/>
          </li>
          <li>
            <input align="middle" type="submit" name="action" value="delete" onclick="return confirm('Are you sure you want to delete?')"/>
            <input type="hidden" name="name" value="{$fPathName}"/>
            <input type="hidden" name="app" value="{$app}"/>        
          </li>
        </ul>
      </div>
    </form>
};

declare function renameFileForm($fPathName as xs:string,
                                $app as xs:string) as item() {
    <form action="./rename">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td colspan="3" style="width: 20%;"><b>Rename file</b></td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>New name</h5></td>
                <td><input type="text" name="newName"/></td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Old path name</h5></td>
                <td><input type="text" size="30" readonly="readonly" name="name" value="{$fPathName}"/></td>
            </tr>            
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Rename file"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};