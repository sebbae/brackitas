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

import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.server.BrackitDB;
import org.brackit.server.ServerException;
import org.brackit.server.io.manager.impl.SlimBufferMgr;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.metadata.manager.impl.MetaDataMgrImpl;
import org.brackit.server.tx.DummyTX;
import org.brackit.server.tx.impl.TX;
import org.brackit.server.tx.impl.TaMgrImpl;
import org.brackit.server.tx.log.Log;
import org.brackit.server.tx.log.impl.DefaultLog;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.node.SimpleStore;
import org.brackit.xquery.xdm.Store;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 *
 */
public class Session {

	private static QueryContext ctx;

	private static Store store;

	private static MetaDataMgr metaDataMgr;

	private static Log transactionLog;

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
	public void testGetAtt() throws Exception {
		ASXQuery x = new ASXQuery("let "
				+ "  $a := session:setAtt('teste',<p>Test Attribute</p>) "
				+ "return " + "  session:getAtt('teste')");
		x.setPrettyPrint(true);
		x.serialize(ctx, System.out);
	}

	// @Test
	// public void testClear() throws Exception {
	//		
	// ASXQuery x = new ASXQuery(""
	// );
	// if (ASXQuery.DEBUG){
	//			
	// }
	// x.setPrettyPrint(true);
	// x.serialize(ctx, System.out);
	// assertEquals(expected, actual);
	// Functions.predefine(new Eval(new QNm(Namespaces.BIT_NSURI,
	// Namespaces.BIT_PREFIX, "eval"), new Signature(new SequenceType(
	// AtomicType.STR, Cardinality.ZeroOrOne), // result
	// new SequenceType(AtomicType.STR, Cardinality.One))));
	//		
	// }

}
