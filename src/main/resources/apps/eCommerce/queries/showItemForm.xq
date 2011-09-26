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
declare variable $itemName as xs:string external;

let 
	$a := 
        for 
            $doc 
        in 
            fn:collection(session:getAttribute('appName'))
        let 
            $docName := $doc/item/data(name)
        where
            $itemName = $docName 
        return 
            $doc
return
	let 
	    $content :=
                    <form action="./addToCartExec.xq">
    	                <table style="width: 100%; background-color: #E0E0F0;">
    	                    <tr>
    	                        <td>
    	                          <h5>Name</h5>
    	                          <b>{$a/item/data(name)}</b>
    	                        </td>
    	                    </tr>
    	                    <tr>
    	                        <td>
    	                          <h5>Description</h5>
    	                          {$a/item/data(description)}
    	                        </td>
    	                    </tr>
                            <tr>
                                <td>
                                  <h5>Quantity</h5>
                                  <input type="text" name="itemQuant"/>
                                </td>
                            </tr>
    	                    <tr>
    	                        <td>
                                    <input type="hidden" name="itemName" value="{$a/item/data(name)}"/>
                                    <input align="middle" type="submit" name="subButton" value="Add to Cart"/>
    	                        </td>
    	                    </tr>
    	                </table>
                    </form>
	return
	    template:default($content)