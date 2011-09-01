let 
    $content :=
            <form action="./createItemExec.xq">
                <table style="width: 100%; background-color: #E0E0F0;">
                    <tr>
                        <td>
                          <h5>Name</h5>
                          <input type="text" name="itemName"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                          <h5>Description</h5>
                          <textarea cols="100" name="itemDescription" rows="6"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>
                          <input align="middle" type="submit" name="subButton" value="Submit"/>
                        </td>
                    </tr>
                </table>
            </form>
return
    util:template($content)