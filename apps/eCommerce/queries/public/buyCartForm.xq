let 
    $content := 
    <p>
    <h3> Client details: </h3>
        <form action="../buyCartConfirm/">
            <table style="width: 100%; background-color: #E0E0F0;">
                <tr>
                    <td>Full name:</td>
                    <td><input type="text" name="cliName" /></td>
                </tr>
                <tr>
                    <td>Address:</td>
                    <td><input type="text" name="cliAddress" /></td> 
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
                        <input align="middle" type="submit" name="subButton" value="Procced"/>
                    </td>
                </tr>
            </table>
        </form>
    </p>
return
    util:template($content)