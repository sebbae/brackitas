let $content := 
    if ((http:removeSessionAtt('login')) and (http:removeSessionAtt('pass'))) then
        <p> Loged out sucessfully </p>
    else
        <p> Not loged out. Logout problems. </p>
return 
    xtc:template($content)