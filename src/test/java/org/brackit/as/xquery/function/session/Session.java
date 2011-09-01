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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.server.BrackitDB;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.xquery.XQuery;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class Session {

	protected static PrintStream createBuffer() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new PrintStream(out) {
			final OutputStream baos = out;

			public String toString() {
				return baos.toString();
			}
		};
	}

	private static HttpSessionTXQueryContext ctx;

	private static MetaDataMgr metaDataMgr;

	private static BrackitDB db;

	private static PrintStream buffer;

	static {
		try {
			buffer = createBuffer();
			db = new BrackitDB(true);
			metaDataMgr = db.getMetadataMgr();
			ctx = new HttpSessionTXQueryContext(db.getTaMgr().begin(),
					metaDataMgr, new NullHttpSession());
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void clear() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"let $a := session:setAtt('test',<a/>) return if (session:clear()) then session:getAtt('test') else <info> Session clear problems </info>");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("", buffer.toString());
	}

	@Test
	public void getAttributeNames() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"if (session:setAtt('test',<p/>) and session:setAtt('test2',<p/>)) then session:getAttributeNames() else <info/>");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("test test2", buffer.toString());
	}

	@Test
	public void getCreationTime() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		Date resultdate = new Date(System.currentTimeMillis());
		new org.brackit.xquery.atomic.Date(sdf.format(resultdate));
		XQuery x = new XQuery(new ASCompileChain(metaDataMgr, ctx.getTX()),
				"session:getCreationTime()");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals(sdf.format(resultdate), buffer.toString());
	}

	@Test
	public void getLastAccessedTime() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"if (session:getCreationTime() eq session:getLastAccessedTime()) then <true/> else <false/>");
		x.serialize(ctx, buffer);
		assertEquals("<true/>", buffer.toString());
	}

	@Test
	public void getAndSetMaxInactiveInterval() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"let $a := 50 return if (session:setMaxInactiveInterval($a)) then if (session:getMaxInactiveInterval() eq $a) then <true/> else <false/> else <info> Problem with setMaxInactiveInterval() </info>");
		x.serialize(ctx, buffer);
		assertEquals("<true/>", buffer.toString());
	}

	@Test
	public void getSessionAtt() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"let $a := session:setAtt('teste',<p>Test Attribute</p>) return session:getAtt('teste')");
		x.serialize(ctx, buffer);
		assertEquals("<p>Test Attribute</p>", buffer.toString());
	}

	@Test
	public void invalidate() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"let $a := session:setAtt('test',<info/>) return let $b := session:invalidate() return session:getAtt('teste')");
		x.serialize(ctx, buffer);
		assertEquals("", buffer.toString());
	}

	@Test
	public void removeSessionAtt() throws Exception {
		XQuery x = new XQuery(
				new ASCompileChain(metaDataMgr, ctx.getTX()),
				"let $a := session:setAtt('test',<p>Test Attribute</p>) return session:rmAtt('test')");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertTrue(new Boolean(buffer.toString()));
	}
}