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

declare function view:msgSuccess($msg as xs:string) as item() {
    <font color="#008000">{$msg}</font>
};

declare function view:msgFailure($msg as xs:string) as item() {
    <font color="#ff0000">{$msg}</font>
};

declare function view:createFileForm($fPath as xs:string,
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

declare function view:createFileFormMsg($fPath as xs:string,
                                        $app as xs:string,
                                        $msg as xs:string) as item() {
    <form action="./create">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td colspan="2">
                    <div align="center">
                        {$msg}
                    </div>
                </td>
            </tr>        
            <tr>
                <td style="width: 20%;"><h5>Name</h5></td>
                <td><input type="text" name="fName"/></td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Under</h5></td>
                <td><input type="text" readonly="readonly" name="name" value="{$fPath}"/></td>
            </tr>            
            <tr>
                <td colspan="2" align="center">
                  <input align="center" type="submit" name="sub" value="Create file"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};

declare function view:createFormForm() as item() {
    let $name := req:get-parameter("name"),
        $app := req:get-parameter("app")
    return
        <form action="../../controllers/fileController/createForm">
            <table style="width: 100%; background-color: rgb(224, 224, 240);">
                <tr>
                    <td style="width: 20%;"><h5>Form function name</h5></td>
                    <td><input type="text" name="formName"/></td>
                </tr>
                <tr>
                    <td style="width: 20%;"><h5>Under</h5></td>
                    <td><input type="text" readonly="readonly" name="name" value="{$name}"/></td>
                </tr>            
                <tr>
                    <td colspan="3" align="center">
                      <input align="center" id="createFormButton" type="submit" name="sub" value="Create form" onclick="alert($('#formPreview').html"/>
                      <input type="hidden" name="app" value="{$app}"/>
                      <input type="hidden" name="createFormPayload" id="createFormPayload" value=""/>
                    </td>
                </tr>
            </table>
            <table style="width: 100%; background-color: rgb(224, 224, 240);">
                <tr>
                    <td colspan="2">
                        <div>
                            Preview
                        </div>
                    </td>                        
                </tr>
            </table>
            <ul id="formPreview"/>
        </form>
};

declare function view:createFormOptions() as item() {
    <div>
        <div id="textInput" class="button blue">
            <a>Text input</a>
        </div>
        <div id="paragraphInput" class="button blue">
            Paragraph input  
        </div>
        <div id="multipleChoiceInput" class="button blue">
            Multiple choice input  
        </div>
        <div id="dateInput" class="button blue">
            Date input  
        </div>
        <div id="fileUpload" class="button blue">
            File upload  
        </div>
    </div>
};

declare function view:createUploadForm($fPath as xs:string,
                                  $app as xs:string) as item() {
    <form action="./upload" enctype="multipart/form-data" method="post">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td style="width: 20%;"><h5>Select file</h5></td>
                <td><input type="file" name="fName"/></td>
                
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Under</h5></td>
                <td><input type="text" readonly="readonly" name="name" value="{$fPath}"/></td>
            </tr>            
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Upload file"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};

declare function view:createDirForm($fPath as xs:string,
                               $app as xs:string) as item() {
    <form action="./mkDir">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td style="width: 20%;"><h5>Directory name</h5></td>
                <td><input type="text" name="dir"/></td>
                
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Under</h5></td>
                <td><input type="text" readonly="readonly" name="name" value="{$fPath}"/></td>
            </tr>            
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Create directory"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};

declare function view:createDirFormMsg($fPath as xs:string,
                                  $app as xs:string,
                                  $msg as xs:string) as item() {
    <form action="./mkDir">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td>
                    <div align="center">
                        {$msg}
                    </div>
                </td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Directory name</h5></td>
                <td><input type="text" name="dir"/></td>
                
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Under</h5></td>
                <td><input type="text" readonly="readonly" name="name" value="{$fPath}"/></td>
            </tr>            
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Create directory"/>
                  <input type="hidden" name="app" value="{$app}"/>
                </td>
            </tr>
        </table>
    </form>
};