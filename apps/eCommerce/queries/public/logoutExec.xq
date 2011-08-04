let $content := 
    if ((session:remAtt('login')) and (session:remAtt('pass'))) then
        <p> Loged out sucessfully </p>
    else
        <p> Not loged out. Logout problems. </p>
return 
    util:template($content)