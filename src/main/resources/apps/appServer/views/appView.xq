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
module namespace view="http://brackit.org/lib/appServer/appView";
import module namespace template="http://brackit.org/lib/appServer/template";

declare function default($content as item()) as item() {
    template:base(template:head("Brackit Application Server"),
                  template:header(),
                  template:teaser(),
                  template:menu(),
                  $content,
                  template:footerBrackit(),
                  template:footerYAML())
};

declare function editXQuery($menu as item(), $content as item()) as item() {
    template:baseFooterScript(template:head("Brackit Application Server"),
                              template:header(),
                              template:teaser(),
                              $menu,
                              $content,
                              template:footerBrackit(),
                              template:footerYAML(), 
                              template:footerScript())
};

declare function menuContent($menu as item(), $content as item()) as item() {
    template:base(template:head("Brackit Application Server"),
                  template:header(),
                  template:teaser(),
                  $menu,
                  $content,
                  template:footerBrackit(),
                  template:footerYAML())
};

declare function listApps($apps as item()*) as item()* {
    let $content := 
        <form action="./create">
        <table style="width: 100%;">
        {
            for $app 
            in $apps
            return
                <tr>
                    <td>{$app}</td>
                    <td><a href="./edit?app={$app}">Edit</a></td>
                    <td><a href="./statistics?app={$app}">Statistics</a></td>
                    <td><a href="./terminate?app={$app}" onclick="return confirm('Are you sure you want to terminate the application?')">Terminate</a></td>
                    <td><a href="./delete?app={$app}" onclick="return confirm('Are you sure you want to delete the application?')">Delete</a></td>
                    <td>Status: {
                        if (app:isRunning($app)) then
                            <font color="#008000"> Running </font>
                        else
                            <font color="#FF0000"> Terminated </font>
                        }</td>
                    <td><a href="./deploy?app={$app}">Deploy</a></td>
                </tr>
        }
        </table>
        <table style="width: 100%;">
            <tr >
                <td width="100%">
                    <a href="./create"> Create new application </a>                            
                </td>
            </tr>
        </table>
        </form>
    return
        default($content)    
};

declare function delete($result as xs:boolean) as item() {
    let $content := 
        if ($result) then
            <p>Application deleted sucessfully.</p>
        else
            <p>Problems while deleting application.</p>
    return
        default($content)
};

declare function listing($dir as item()*, $app as xs:string, $base as xs:string) as item()* {
    <li><a>{fn:data($dir/@name)}</a>
        <ul> {
            for $sub
            in $dir/dir
            return
                <li>{listing($sub,$app,fn:concat($base,"/",fn:data($sub/@name)))}</li>
        }{
            for $file
            in $dir/file
            return
                <li><a href="./load?app={$app}&amp;name={fn:concat($base,"/",$file/@name)}">{fn:data($file/@name)}</a></li>
        }            
        </ul>
    </li>
};

declare function createMenu($app as xs:string) as item() {
    <ul class="vlist">
        <li><h6 class="vlist">{$app}</h6></li>
        {
        for $a
        in app:getStructure($app)/app/dir
        return <li>{listing($a,$app,fn:concat($app,"/",fn:data($a/@name)))}</li>
        }
    </ul>
};

declare function createAppForm() as item() {
    <form action="./create">
        <table style="width: 100%; background-color: rgb(224, 224, 240);">
            <tr>
                <td style="width: 20%;"><h5>Name</h5></td>
                <td><input type="text" name="app"/></td>
                <td><input type="hidden" name="appMsg"/></td>
            </tr>
            <tr>
                <td style="width: 20%;"><h5>Application Model</h5></td>
                <td>
                  Create MVC application <input type="radio" name="model" value="MVC" checked="checked"/><br></br>
                  Create personalized application <input type="radio" name="model" value="REG"/>
                </td>
                <td><input type="hidden" name="modelMsg"/></td>                    
            </tr>
            <tr>
                <td colspan="3" align="center">
                  <input align="center" type="submit" name="sub" value="Create application"/>
                </td>
            </tr>
        </table>
    </form>
};

declare function createAppFormError($msg as xs:string) as item() {
    <table>
      <tr>
        <td>
        {createAppForm()}
        </td>
      </tr>
      <tr>
        <td>
          <font color="#ff0000">{$msg}</font>
        </td>
      </tr>        
    </table>
};

declare function generateFileOptions($file as xs:string) as item() {
  <div class="hlist">
    <ul>
      <li>
        <strong> File: {$file}  </strong>
      </li>        
      <li>
        <a href="../fileController/save?file={$file}">Save</a>  
      </li>
      <li>
        <a href="../fileController/compile?file={$file}">Compile</a>
      </li>
      <li>
        <a href="../fileController/delete?file={$file}" onclick="return confirm('Are you sure you want to delete?')">Delete</a>
      </li>
      <li>
        <a href="../fileController/run?file={$file}">Try it out!</a>
      </li>
    </ul>
  </div>
};

declare function editMVC ($model as xs:string,
                          $view as xs:string,
                          $controller as xs:string) as item () {
    <table style="width:100%;">
      <tr>
        <td>
          {generateFileOptions($model)}
        </td>
      </tr>
      <tr>
        <td>
          <div id="coll_intern">
            <div class="textwrapper">
              {fn:concat("<textarea id='code' name='modelContent' rows='20'>",util:plainPrint(bit:loadFile(fn:concat("apps/",$model))),"</textarea>")}
            </div>
          </div>
        </td>
      </tr>
      <tr>
        <td>
          {generateFileOptions($view)}
        </td>
      </tr>
      <tr>
        <td>
          <div id="coll_intern">
            <div class="textwrapper">
              {fn:concat("<textarea id='code2' name='viewContent' rows='20'>",util:plainPrint(bit:loadFile(fn:concat("apps/",$view))),"</textarea>")}
            </div>
          </div>
        </td>
      </tr>
      <tr>
        <td>
          {generateFileOptions($controller)}
        </td>
      </tr>
      <tr>
        <td>
          <div id="coll_intern">
            <div class="textwrapper">
              {fn:concat("<textarea id='code3' name='controllerContent' rows='20'>",util:plainPrint(bit:loadFile(fn:concat("apps/",$controller))),"</textarea>")}
            </div>
          </div>
        </td>
      </tr>      
    </table>
};

declare function editQuery($resource as xs:string) as item() {
    <table style="width:100%;">
      <tr>
        <td>
          {generateFileOptions($resource)}
        </td>
      </tr>
      <tr>
        <td>
          <div class="textwrapper">
            {fn:concat("<textarea id='code' rows='50'>",util:plainPrint(bit:loadFile(fn:concat("apps/",$resource))),"</textarea>")}
          </div>
        </td>
      </tr>
    </table>
};