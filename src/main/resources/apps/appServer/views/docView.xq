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
 * @author Roxana Zapata
 *
:)
module namespace docView="http://brackit.org/lib/appServer/docView";
import module namespace template="http://brackit.org/lib/appServer/template";

declare function docView:default($content as item()) as item() {
    template:base(template:head("Brackit Application Server"),
                  template:header(),
                  template:teaser(),
                  template:menu(),
                  $content,
                  template:footerBrackit(),
                  template:footerYAML())
};

declare function docView:browserModules($results as item()*) as item()* {
    let $content := 
        <table>
        
        	<tr>
    			<td>Name</td>
    			<td>Description</td>
         		<td>NamespaceURL</td>
        	</tr> 
        	{
            for $module 
            in $results/Module
            return
                <tr>
                	<td><a href="./listFunctions?module={$module/Name/text()}">{$module/Name/text()}</a></td>
                	<td>{$module/Description/text()}</td>
                    <td>{$module/NsURI/text()}</td>
                </tr>
        	}
        </table>   
    return
        docView:default($content)     
};

declare function docView:browserFunctionModules($results as item()*) as item()* {
     let $content := 
        <table>
        	<tr>
        		<td><b>{fn:concat("Modules", " > ", $results/@name)}</b></td>
        	</tr>
        	{
            for $function 
            in $results/Function
            return
            <tr>
                <tr>
                	<td><b>{$function/Name/text()}</b></td>
                </tr>
				<tr>
            		<td>
						{fn:data($results/@name)} ( 
						{for $parameter 
            			in $function/Signature/Parameters/Parameter
            			return 
            				fn:concat($parameter/@description, " as ", $parameter,",")
            			}
            			 ) as {$function/Signature/Return}
            		</td>
                </tr>
                <tr>
                	<td>{$function/Description/text()}</td>
                </tr>
             </tr>
        }
        </table>
    return
        docView:default($content)   
};

