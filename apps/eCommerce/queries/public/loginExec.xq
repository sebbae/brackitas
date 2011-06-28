declare variable $login as xs:string external;
declare variable $pass as xs:string external;

let $content := 
    if ((http:setSessionAtt('login',$login)) and (http:setSessionAtt('pass',$pass))) then
        <p> User {$login} logged sucessfully </p>
    else
        <p> User {$login} not logged. Loggin problems. </p>
return 
    util:template($content)