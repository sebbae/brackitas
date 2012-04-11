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
declare variable $itemQuant as xs:string external;

declare function local:cartItem($name as xs:string, 
                                $quantity as xs:string) as item()
{
    <item>
        <name>
            {$name}
        </name>
        <quantity>
            {$quantity}
        </quantity>
    </item>
}
;
if (
    if (exists(session:get-attribute('cart'))) then
        (: already something on the cart session :)
        let
            $newCart :=
            <cart>
            {
                (  
                for $itemN in session:get-attribute('cart')/item
                    return $itemN
                    ,
                    local:cartItem($itemName,$itemQuant)
                )
            }
            </cart> 
        return
            session:set-attribute('cart',$newCart)
    else
        (: first item to the cart :)
        let 
            $item := template:getItemFromCollection($itemName) 
        return 
            let
                $newCart := <cart>{local:cartItem($item/item/data(name),$itemQuant)}</cart>
            return 
                session:set-attribute('cart',$newCart)
    )
then
    bit:eval(io:read('src/main/resources/apps/eCommerce/queries/showCartForm.xq'))
else
    template:default(<p>Error adding item to cart!</p>)