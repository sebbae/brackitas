(:
 *
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 **
 * 
 * @author Roxana Zapata
 * 
 *
:)

module namespace eAuctionView="http://brackit.org/lib/eAuction/eAuctionView";
import module namespace eAuctionModel="http://brackit.org/lib/eAuction/eAuctionModel";
import module namespace template="http://brackit.org/lib/eAuction/template";

declare function eAuctionView:index() as item()* {
    let 
    $content :=
            <h2>
				Welcome to eAuction platform
			</h2>
return
    template:default($content)  
};

declare function eAuctionView:itemForm() as item()* {
let 
    $content :=
    		<div id="form">
            	<h3>
					Hi! Ready to sell with eAuction
				</h3>
				<form action="./registerItem.xq">
			 		<table border="0" cellpadding="5" cellspacing="0" bgcolor="#ccc">
			 			<tr>
                        	<td align="right">Name:</td>
                        	<td><input type="text" name="itemName" size="30"/></td>
                      	</tr>
              			<tr>
                        	<td align="right">Price:</td>
                        	<td><input type="text" name="itemPrice" size="30"/></td>
                      	</tr>
                      	<tr>
                        	<td align="right" valign="top">Description:</td>
                        	<td><textarea name="itemDescription" rows="10" cols="50"></textarea></td>
                      	</tr>
                      	<tr>
                      		<td></td>
                      		<td align="right">
                 			<input align="middle" type="submit" name="subButton" value="Register"/>
                    		</td>
                    	</tr>
			 		</table>
				</form>
			 </div>			 
return
    template:default($content)   
};

declare function eAuctionView:userForm() as item()* {
let 
    $content :=
    		<div id="form">
            	<h3>
					Hi! Ready to register with eAuction
				</h3>
				<form action="./registerUser">
			 		<table border="0" cellpadding="5" cellspacing="0" bgcolor="#ccc">
			 			<tr>
                        	<td align="right">User ID:</td>
                        	<td><input type="text" name="userID" size="30"/></td>
                        	<td></td>
                      	</tr>
			 			<tr>
                        	<td align="right">First name:</td>
                        	<td><input type="text" name="firstName" size="30"/></td>
                        	<td></td>
                      	</tr>
              			<tr>
                        	<td align="right">Last name:</td>
                        	<td><input type="text" name="lastName" size="30"/></td>
                        	<td></td>
                      	</tr>
                      	<tr>
                        	<td align="right">Street address:</td>
                        	<td><input type="text" name="streetAddress" size="30"/></td>
                        	<td></td>
                      	</tr>
                      	<tr>
                        	<td align="right">Email address:</td>
                        	<td><input type="text" name="emailAddress" size="30"/></td>
                        	<td></td>
                      	</tr>
                      	<tr>
                      		<td></td>
                      		<td></td>
                      		<td align="right">
                 			<input align="middle" type="submit" name="registerUser" value="Register"/>
                    		</td>
                    	</tr>
			 		</table>
				</form>
			 </div>			 
return
    template:default($content)   
};