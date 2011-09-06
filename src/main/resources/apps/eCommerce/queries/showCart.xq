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
declare function local:getItemFromCollection ($name as xs:string) as item()+
{
    for 
        $doc 
    in 
        fn:collection(session:getAtt('appName'))
    let 
        $docName := $doc/item/data(name)
    where
        $docName = $name 
    return 
        $doc    
}
;

let
    $cart := session:getAtt('cart')
return
        <table>
            <tr>
                <td>
                    Name
                </td>
                <td>
                    Description
                </td>
                <td>
                    Quantity
                </td>
            </tr>
            {
                for 
                    $cartItem 
                in 
                    $cart/item
                let
                    $docName := $cartItem/data(name),
                    $quantity := $cartItem/data(quantity),
                    $description := local:getItemFromCollection($docName)/item/data(description)
                return 
                    <tr>
                        <td>
                            <a href="./showItemForm.xq?itemName={$docName}">{$docName}</a>
                        </td> 
                        <td>
                            {$description}
                        </td>
                        <td>
                            {$quantity}
                        </td>
                    </tr>
            }
        </table>    