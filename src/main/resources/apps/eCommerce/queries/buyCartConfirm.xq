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
import module namespace template="http://brackit.org/lib/eCommerce/template";
declare variable $cliName as xs:string external;
declare variable $cliAddress as xs:string external;

let 
    $content := 
    <p>
    <h3> Buying confirmation: </h3>
    <form action="./buyCartExec.xq">
        <table style="width: 100%; background-color: #E0E0F0;">
            <tr>
                <td>Full name:</td>
                <td>{$cliName}<input type="hidden" name="cliName" value="{$cliName}"/></td>
            </tr>
            <tr>
                <td>Address:</td>
                <td>{$cliAddress}<input type="hidden" name="cliAddress" value="{$cliAddress}"/></td> 
            </tr>
            <tr>
                <td colspan="2">
                {
                    let 
                        $a := bit:loadFile('apps/eCommerce/queries/showCart.xq') 
                    return 
                        bit:eval($a)
                }
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input align="middle" type="submit" name="subButton" value="Buy it!"/>
                </td>
            </tr>
        </table>
    </form>
    </p>
return
    template:default($content)