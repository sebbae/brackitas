/*
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
 */
package org.brackit.as.xquery.function.session;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.server.BrackitDB;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.xquery.QueryContext;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class Session {

	private static QueryContext ctx;

	private static MetaDataMgr metaDataMgr;

	private static BrackitDB db;

	static {
		try {
			db = new BrackitDB(true);
			metaDataMgr = db.getMetadataMgr();
			ctx = new HttpSessionTXQueryContext(db.getTaMgr().begin(),
					metaDataMgr, new NullHttpSession());
		} catch (ServerException e) {
			// TODO Remove it
			e.printStackTrace();
		}
	}

	@Test
	public void clear() throws Exception {
		ASXQuery x = new ASXQuery("session:clear()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void getAttributeNames() throws Exception {
		ASXQuery x = new ASXQuery("session:getAttributeNames()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void getCreationTime() throws Exception {
		ASXQuery x = new ASXQuery("session:getCreationTime()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void getLastAccessedTime() throws Exception {
		ASXQuery x = new ASXQuery("session:getlastAccessedTime()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void getMaxInactiveInterval() throws Exception {
		ASXQuery x = new ASXQuery("session:getMaxInactiveInterval()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void getSessionAtt() throws Exception {
		ASXQuery x = new ASXQuery("let "
				+ "  $a := session:setAtt('teste',<p>Test Attribute</p>) "
				+ "return " + "  session:getAtt('teste')");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void invalidate() throws Exception {
		ASXQuery x = new ASXQuery("session:invalidate()");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void removeSessionAtt() throws Exception {
		ASXQuery x = new ASXQuery("let "
				+ "  $a := session:setAtt('teste',<p>Test Attribute</p>) "
				+ "return " + "  session:removeAtt('teste')");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	@Test
	public void setMaxInactiveInterval() throws Exception {
		ASXQuery x = new ASXQuery("session:setMaxInactiveInterval(50)");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}
}