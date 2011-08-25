let $content := 
    if ((session:rmAtt('login')) and (session:rmAtt('pass'))) then
        <p> Loged out sucessfully </p>
    else
        <p> Not loged out. Logout problems. </p>
return 
    util:template($content)