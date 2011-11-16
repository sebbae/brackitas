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
module namespace view="http://brackit.org/lib/brackitQuery/brackitView";
import module namespace template="http://brackit.org/lib/brackitQuery/template";

declare function view:showQueryResultTime($query as item()*, $result as item()*, $time) as item()* 
{
    let
        $content :=   
            <form action="./query">
                <table style="width: 100%; background-color: #E0E0F0;">
                    <tr>
                        <td>
                            <h5>Query</h5>
                            <textarea cols="100" name="query" rows="6">
                                {$query}
                            </textarea>
                        </td>
                    </tr>                 
                    <tr>
                        <td>
                            <input align="middle" type="submit" name="subButton" value="Submit" />                            
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <h5>Result</h5>
                            <textarea cols="100" name="result" rows="6">{util:plain-print($result)}</textarea>
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                            <input type="text" value="In {$time} seconds" />
                        </td>
                    </tr>
                </table>
            </form>
    return
        template:default($content)
};

declare function view:showUpload() as item()*
{
let
    $content := 
        <form action="./uploadExec" enctype="multipart/form-data" method="post">
            <table style="width: 100%; background-color: #E0E0F0;">
                <tr>
                    <td>
                        <h5>
                            Select file <input type="file" name="file" size="30"/>
                        </h5>
                    </td>
                </tr>                 
                <tr>
                    <td>
                        <input type="hidden" name="payload" value="form_upload"/>
                        <input align="middle" type="submit" name="subButton" value="Submit" />                            
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="hidden" name="result" />
                    </td>
                </tr>
            </table>
        </form>
return
    template:default($content)
};