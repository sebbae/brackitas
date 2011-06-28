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
                            $a := bit:loadFile('apps/eCommerce/queries/public/showCart.xq') 
                        return 
                            bit:eval($a)
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
