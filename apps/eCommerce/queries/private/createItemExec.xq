declare variable $itemName as xs:string external;
declare variable $itemDescription as xs:string external;

declare function local:Item($name as xs:string, 
                            $description as xs:string) as item()+
    {
				<item>
					<name>
						{$name}
					</name>
					<description>
						{$description}
					</description>
				</item>
    };
let $content := 
    if ((string-length($itemName) > 0) and 
        (string-length($itemDescription) > 0) and 
        (not(contains($itemName,' ')))) then
    	let 
    		$a := bit:storeFile(concat(http:getSessionAtt('appName'),'/items/',$itemName),
    		                    local:Item($itemName,$itemDescription))
    	return
    		<p> Item {$a/item/data(name)} created sucessfully </p>
    else
    	<p> Item {$itemName} not created. Validation problems. </p>
return 
	util:template($content)

	