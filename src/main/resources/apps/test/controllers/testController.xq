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
 :)
module namespace controller="http://brackit.org/lib/test/testController"; 
import module namespace model="http://brackit.org/lib/test/testModel"; 
import module namespace view="http://brackit.org/lib/test/testView"; 

declare function controller:index() as item() {
  let $msg :=
    "Hello World!"
  return
    view:default($msg)
};

declare function controller:test2() as item() {
    fn:doc("/test")
};
    
declare function controller:test() as item() {
    try {
        let $req := 
            <request method = "get"
                     href = "http://www.google.de"
                     status-only = "false"
                     timeout = "1000">
                <header name="name1" value="value1"/>     
                <header name="name2" value="value2"/>
            </request>
        return
            http:send-request($req)
    } catch * {
        $err:code, $err:description, $err:line-number
    }
};