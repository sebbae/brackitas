declare variable $itemName as xs:string external;

let 
    $a := fn:doc(fn:concat(http:getSessionAtt('appName'),'/items/',$itemName,'.xml'))
return
    let 
        $content :=
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
                                <table align="left" style="width:50%;">
                                    <tr>
                                        <td>
                                            <form action="../deleteItemExec/">
                                                <input type="hidden" name="itemName" value="{$a/item/data(name)}"/>
                                                <input align="middle" type="submit" name="subButton" value="Delete"/>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="../updateItemForm/">
                                                <input type="hidden" name="itemName" value="{$a/item/data(name)}"/>
                                                <input align="middle" type="submit" name="subButton" value="Update"/>
                                            </form>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
    return
        util:template($content)