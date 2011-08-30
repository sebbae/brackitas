declare variable $itemName as xs:string external;

let $content := 
    if (bit:deleteFile(fn:concat('items/', $itemName))) then
  		<p> Item {$itemName} deleted sucessfully </p>
    else
    	<p> Item {$itemName} not deleted. Deletion problems. </p>
return 
	util:template($content)