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
module namespace appView="http://brackit.org/lib/appServer/appView";
import module namespace template="http://brackit.org/lib/appServer/template";

declare function appView:default($content as item()) as item() {
    template:base(template:head("Brackit Application Server"),
                  template:header(),
                  template:teaser(),
                  template:menu(),
                  $content,
                  template:footerBrackit(),
                  template:footerYAML())
};

declare function appView:editXQuery($menu as item(), $content as item()) as item() {
    template:baseFooterScript(template:head("Brackit Application Server"),
                              template:header(),
                              template:teaser(),
                              $menu,
                              $content,
                              template:footerBrackit(),
                              template:footerYAML(), 
                              template:footerScript())
};

declare function appView:menuContent($menu as item(), $content as item()) as item() {
    template:base(template:head("Brackit Application Server"),
                  template:header(),
                  template:teaser(),
                  $menu,
                  $content,
                  template:footerBrackit(),
                  template:footerYAML())
};

declare function appView:appOptions($app as xs:string) as item() {
    <tr>
        <td>{$app}</td>
        <td><a href="./edit?app={$app}">Edit</a></td>
        <td><a href="./statistics?app={$app}">Statistics</a></td>
        <td><a href="./terminate?app={$app}" onclick="return confirm('Are you sure you want to terminate the application?')">Terminate</a></td>
        <td><a href="./delete?app={$app}" onclick="return confirm('Are you sure you want to delete the application?')">Delete</a></td>
        <td>Status: {
            if (app:is-running($app)) then
                <font color="#008000"> Running </font>
            else
                <font color="#FF0000"> Terminated </font>
            }</td>
        <td><a href="./deploy?app={$app}">Deploy</a></td>
    </tr>
};

declare function appView:listApps($apps as item()*) as item()* {
    let $content := 
        <form action="./create">
        <table style="width: 100%;">
        {   
            for $app in $apps
            return
                appView:appOptions($app)
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
        appView:default($content)    
};

declare function appView:delete($result as xs:boolean) as item() {
    let $content := 
        if ($result) then
            <p>Application deleted sucessfully.</p>
        else
            <p>Problems while deleting application.</p>
    return
        appView:default($content)
};

declare function appView:listing($dir as item()*, $app as xs:string, $base as xs:string) as item()* {
    <div>
        <li>
          <zu>
            <a>{fn:data($dir/@name)}</a>
            <a href="../fileController/mkDir?app={$app}&amp;name={$base}">
                <img align="right" width="16" height="16" alt="Create a new folder" title="Create a new folder" src="http://localhost:8080/apps/appServer/resources/images/03_folder.gif"/>            
            </a>
            <a href="../fileController/upload?app={$app}&amp;name={$base}">
                <img align="right" width="16" height="16" alt="Upload file" title="Upload file" src="http://localhost:8080/apps/appServer/resources/images/02_upload.gif"/>
            </a>
            <a href="../fileController/create?app={$app}&amp;name={$base}">
                <img align="right" width="16" height="16" alt="Create new XQuery file" title="Create new XQuery file" src="http://localhost:8080/apps/appServer/resources/images/01_create.gif"/>
            </a>
          </zu>
        </li>
        <li>
            {
                for $sub
                in $dir/dir
                return
                    <ul>{appView:listing($sub,$app,fn:concat($base,"/",fn:data($sub/@name)))}</ul>
                ,
                for $content
                in $dir
                return 
                    <ul>
                    {
                        for $file
                        in $dir/file
                        let $name := fn:data($file/@name),
                            $error := fn:data($file/@compError)
                        return
                            <li>
                                <zu>
                                    <a href="{fn:concat("../appController/load?app=",
                                                        $app,
                                                        "&amp;name=",
                                                        $base,"/",
                                                        $name)}">
                                                        {
                                                            if (fn:string-length($error) gt 1) then
                                                                <font color="#ff0000"> {$name} </font>
                                                            else
                                                                $name
                                                        }
                                    </a>
                                </zu>
                            </li>
                    }
                    </ul>
            }
        </li>
    </div>
};

declare function appView:createMenu2() as item() {
    let $app := "eCommerce"
    return
    <ul class="vlist">
        <li><h6 class="vlist">{$app}</h6></li>
        {
        for 
            $a
        in 
            app:get-structure($app)/app/dir
        return 
            <li>
              <ul>
                {appView:listing($a,$app,fn:concat($app,
                                           "/",
                                           fn:data($a/@name)))}
              </ul>
            </li>
        }
    </ul>
};

declare function appView:createMenu($app as xs:string) as item() {
    <ul class="vlist">
        <li><h6 class="vlist">{$app}</h6></li>
        {
        for 
            $a
        in 
            app:get-structure($app)/app/dir
        return 
            <li>
              <ul>
                {appView:listing($a,$app,fn:concat($app,
                                           "/",
                                           fn:data($a/@name)))}
              </ul>
            </li>
        }
    </ul>
};

declare function appView:createAppForm() as item() {
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

declare function appView:createAppFormError($msg as xs:string) as item() {
    <table>
      <tr>
        <td>
        {appView:createAppForm()}
        </td>
      </tr>
      <tr>
        <td>
          <font color="#ff0000">{$msg}</font>
        </td>
      </tr>        
    </table>
};

declare function appView:generateFileOptions($fPathName as xs:string,
                                             $app as xs:string) as item() {
  <div class="hlist">
    <ul>
      <li>
        <strong> File: {$fPathName}  </strong>
      </li>        
      <li>
          <input align="middle" type="submit" name="action" value="save"/>
      </li>
      <li>
        <input align="middle" type="submit" name="action" value="compile"/>
      </li>
      <li>
        <input align="middle" type="submit" name="action" value="delete" onclick="return confirm('Are you sure you want to delete?')"/>
      </li>
      <li>
        { 
        let $xqf := fn:substring-before($fPathName, ".xq"),
            $button := 
            if (xqfile:is-library($xqf)) then 
                 1
            else 
                 0
        return
            <button type="button" onClick="{fn:concat("testIt(new Boolean(",$button,"),'",$xqf,"')")}">test it</button>
        }
        <input type="hidden" name="name" value="{$fPathName}"/>
        <input type="hidden" name="app" value="{$app}"/>        
      </li>
    </ul>
  </div>
};

declare function appView:generateTextArea($fPathName as xs:string, $num as xs:string) as item() {
    fn:concat("<textarea id='code",
              $num,
              "' name='query' rows='20'>",
              util:plain-print(
                  if (fn:compare(req:get-parameter("name"),$fPathName) eq 0 and
                      fn:string-length(req:get-parameter("query")) > 0) then
                      req:get-parameter("query")
                  else
                      bit:load-file($fPathName)),
              "</textarea>")
};

declare function appView:editQuery($resource as xs:string,
                           $app as xs:string) as item() {
  <form action="../fileController/action" method="post">
    <table style="width:100%;">
      <tr>
        <td>
          {appView:generateFileOptions($resource,$app)}
        </td>
      </tr>
      <tr>
        <td>
          <div class="textwrapper">
            {appView:generateTextArea($resource,"")}            
          </div>
        </td>
      </tr>
    </table>
  </form>  
};

declare function appView:editQueryAfterAction($resource as xs:string,
                                      $app as xs:string,
                                      $msg as xs:string) as item() {
  <form action="../fileController/action" method="post">
    <table style="width:100%;">
      <tr>
        <td>
          {appView:generateFileOptions($resource,$app)}
        </td>
      </tr>
      <tr>
        <td>
          <div align="center">
            {$msg}
          </div>
        </td>
      </tr>
      <tr>
        <td>
          <div class="textwrapper">
            {appView:generateTextArea($resource,"")}            
          </div>
        </td>
      </tr>
    </table>
  </form>  
};