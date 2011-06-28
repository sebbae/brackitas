declare variable $itemName as xs:string external;
declare variable $itemQuant as xs:string external;

declare function local:cartItem($name as item(), 
                                $quantity as xs:string) as item()+
{
    <item>
        {$name}
        <quantity>
            {$quantity}
        </quantity>
    </item>
}
;

let 
    $content :=
        <p> 
            <h5> Shopping Cart </h5>
            {
            if (fn:string-length(http:getSessionAtt('cart')) > 0) then
                (: already something on the cart session :)
                if 
                (
                    let
                        $newCart :=
                        <cart>
                        {  
                            let 
                                $cart := http:getSessionAtt('cart')
                            return
                                (
                                    for $itemN in $cart//item 
                                    return
                                        $itemN
                                    ,
                                    local:cartItem(<name>{$itemName}</name>,$itemQuant)
                                )
                        }
                        </cart> 
                    return
                        http:setSessionAtt('cart',$newCart)
                ) then
                    http:getSessionAtt('cart')
                else
                    ''
            else
                (: first item to the cart :)
                let 
                    (: TODO: improve searching mechanism with better xpath :)
                    $docItem := fn:doc('_master.xml')/bit//doc[(xs:string(@name))=fn:concat('/',http:getSessionAtt('appName'),'/items/',$itemName,'.xml')] 
                return 
                    let 
                        $item := fn:doc($docItem/@name) 
                    return 
                        if 
                        (
                            let 
                                $newCart := <cart>{local:cartItem($item//name,$itemQuant)}</cart>
                            return 
                                http:setSessionAtt('cart',$newCart)
                        ) then
                            http:getSessionAtt('cart')
                        else
                            ''
            }
        </p>
return
    (: $content :) 
    (:  :)
    if (fn:string-length($content) > 0) then
        let 
            $a := bit:loadFile('apps/eCommerce/queries/public/showCartForm.xq') 
        return 
            bit:eval($a)
    else
        util:template(<p>Error adding item to cart!</p>) 

