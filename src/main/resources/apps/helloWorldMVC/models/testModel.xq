module namespace model="http://brackit.org/lib/modelTest";

declare function addItem($name as xs:string, 
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
    
declare function echo($s as item()*) as item()* 
{ 
    $s
};    