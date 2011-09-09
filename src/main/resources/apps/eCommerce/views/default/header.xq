declare variable $login := session:getAtt('login');
declare variable $pass := session:getAtt('pass');
<table>
    <tr>
        <td>
            <img style="width:200px; height:75px; padding: 10px;" align="middle" src="http://localhost:8080/apps/eCommerce/resources/images/ecommerce.jpg" />
        </td>
        <td>
            {
                if ((fn:string-length($login) > 0) and (fn:string-length($pass) > 0)) then
                    <p> Welcome {$login} <a href="./logoutExec.xq">Logout</a> </p> 
                else
                    <form action="./loginExec.xq">
                        <table style="width: 100%;">
                            <tr>
                                <td>
                                  <b>Login</b>
                                  <input type="text" name="login" value="{if ($login) then $login else ''}"/>
                                </td>
                                <td>
                                  <b>Pass</b>
                                  <input type="text" name="pass" value="{if ($pass) then $pass else ''}"/>
                                </td>
                                <td>
                                  <input align="middle" type="submit" name="subButton" value="Login"/>
                                </td>
                            </tr>
                        </table>
                    </form>
            }
        </td>
    </tr>
</table>