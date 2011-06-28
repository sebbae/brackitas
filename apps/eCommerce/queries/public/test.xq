declare variable $extTest as xs:string external;
let 
    $content := fn:concat($extTest, ' is an external variable!')
return 
	util:template($content)
