let 
    $content := 
    let
        $cart := http:getSessionAtt('cart')
    return
        <form action="../buyCartForm/">
            <table>
                <tr>
                    {
                        let 
                            $a := xtc:loadFile('apps/eCommerce/queries/public/showCart.xq') 
                        return 
                            xtc:eval($a)
                    }
                </tr>
                <tr>
                    <td>
                        <input align="middle" type="submit" name="subButton" value="Buy Cart"/>
                    </td>                
                </tr>
            </table> 
        </form>
return
    util:template($content)
