declare variable $cliName as xs:string external;
declare variable $cliAddress as xs:string external;

declare function local:cart($name as xs:string, 
                            $address as xs:string) as item()+
    {
        let 
            $vReturn :=
                <order>
                    <name>
                        {fn:concat($name,$address)}
                    </name>
                    <cliName>
                        {$name}
                    </cliName>
                    <cliAddress>
                        {$address}
                    </cliAddress>
                    {session:getAtt('cart')}
                </order>
        return
            $vReturn
    }
;

let $content := 
    let 
        $a := bit:storeFile(concat(session:getAtt('appName'),'/carts/',fn:concat($cliName,$cliAddress)),
                            local:cart($cliName,$cliAddress))
    return
        if (session:remAtt('cart')) then
            <p> Buy order for the given cart added sucessfully. </p>
        else
            <p> Problems with buying order. </p>
return 
    util:template($content)