let
    $cart := http:getSessionAtt('cart')
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
                    $i 
                in 
                    $cart//item
                return 
                    let 
                        $docN := fn:doc(fn:concat(http:getSessionAtt('appName'),'/items/',$i/data(name),'.xml'))
                    return 
                    <tr>
                        <td>
                            <a href="../showItemForm/?itemName={$docN/item/data(name)}">{$docN/item/data(name)}</a>
                        </td> 
                        <td>
                            {$docN/item/data(description)}
                        </td>
                        <td>
                            {$i/data(quantity)}
                        </td>
                    </tr>
            }
        </table>