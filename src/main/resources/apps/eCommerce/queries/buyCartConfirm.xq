declare variable $cliName as xs:string external;
declare variable $cliAddress as xs:string external;

let 
    $content := 
    <p>
    <h3> Buying confirmation: </h3>
    <form action="../buyCartExec/">
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
                        $a := bit:loadFile('apps/eCommerce/queries/public/showCart.xq') 
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
    util:template($content)