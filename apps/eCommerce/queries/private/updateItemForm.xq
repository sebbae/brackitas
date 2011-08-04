declare variable $itemName as xs:string external;

let 
    $a := fn:doc(fn:concat(session:getAtt('appName'),'/items/',$itemName,'.xml'))
return
    let 
        $content :=
        <form action="../updateItemExec/">        
            <table style="width: 100%; background-color: #E0E0F0;">
                <tr>
                    <td>
                        <h5>Name</h5>
                        <input readonly="true" type="text" name="itemNewName" value="{$a/item/data(name)}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <h5>Description</h5>
                        <input type="text" name="itemDescription" value="{$a/item/data(description)}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="hidden" name="itemName" value ="{$itemName}"/>
                        <input align="middle" type="submit" name="subButton" value="Update"/>
                    </td>
                </tr>
            </table>
        </form>
    return 
        util:template($content)