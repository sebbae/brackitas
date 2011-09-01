declare variable $itemName as xs:string external;

let 
	$a := fn:doc(fn:concat('/',session:getAtt('appName'),'/items/',$itemName,'.xml'))
return
	let 
	    $content :=
                    <form action="./addToCartExec.xq">
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
                                  <h5>Quantity</h5>
                                  <input type="text" name="itemQuant"/>
                                </td>
                            </tr>
    	                    <tr>
    	                        <td>
                                    <input type="hidden" name="itemName" value="{$a/item/data(name)}"/>
                                    <input align="middle" type="submit" name="subButton" value="Add to Cart"/>
    	                        </td>
    	                    </tr>
    	                </table>
                    </form>
	return
	    util:template($content)