declare variable $itemName as xs:string external;
declare variable $itemDescription as xs:string external;

let 
    (: TODO: improve searching mechanism with better xpath :)
    $docItem := fn:doc('_master.xml')/xtc//doc[(xs:string(@name))=fn:concat('/',http:getSessionAtt('appName'),'/items/',$itemName,'.xml')] 
return
    replace value of node 
        fn:doc($docItem/@name)/item/description 
    with 
        $itemDescription