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
package org.brackit.as.xquery.function;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.brackit.as.http.app.FrontController;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.as.xquery.function.base.NullHttpSession;
import org.brackit.server.BrackitDB;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.tx.Tx;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 *
 */
public class ECommerce {

	protected static PrintStream createBuffer() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new PrintStream(out) {
			final OutputStream baos = out;

			public String toString() {
				return baos.toString();
			}
		};
	}

	private static PrintStream buffer;

	private static ASQueryContext ctx;

	private static MetaDataMgr metaDataMgr;

	private static BrackitDB db;

	private static Tx tx;


	@Test
	public void listItems() throws Exception {
		try {
			buffer = createBuffer();
			db = new BrackitDB(true);
			metaDataMgr = db.getMetadataMgr();
			tx = db.getTaMgr().begin();
			ctx = new ASQueryContext(tx, metaDataMgr, new NullHttpSession());
			ctx.getHttpSession().setAttribute(FrontController.APP_SESSION_ATT, "eCommerce");
			ASXQuery x = new ASXQuery(
					new ASCompileChain(metaDataMgr, tx),
					getClass().getClassLoader().getResourceAsStream("apps/eCommerce/queries/test.xq"));
			x.setPrettyPrint(true);
			x.serialize(ctx, buffer);
			
			System.out.println(buffer.toString());
			x.serialize(ctx, buffer);
			
			System.out.println(buffer.toString());			
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}	
	
}
