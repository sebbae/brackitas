/*
 * [New BSD License]
 * Copyright (c) 2011-2012, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Brackit Project Team nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.xquery.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.as.xquery.function.base.BaseASQueryContextTest;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class TestSession extends BaseASQueryContextTest {

	@Before
	public void initFields() throws Exception {
		super.initFields();
	};

	@Test
	public void clear() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := session:set-attribute('test',<a/>) return if (session:clear()) then session:get-attribute('test') else <info> Session clear problems </info>");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("", buffer.toString());
	}

	@Test
	public void getAttributeNames() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"if (session:set-attribute('test',<p/>) and session:set-attribute('test2',<p/>)) then session:get-attribute-names() else <info/>");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("test test2", buffer.toString());
	}

	@Test
	public void getCreationTime() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		Date resultdate = new Date(System.currentTimeMillis());
		new org.brackit.xquery.atomic.Date(sdf.format(resultdate));
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"session:get-creation-time()");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals(sdf.format(resultdate), buffer.toString());
	}

	@Test
	public void getLastAccessedTime() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"if (session:get-creation-time() eq session:get-last-accessed-time()) then 'true' else 'false'");
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void getAndSetMaxInactiveInterval() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := 50 return if (session:set-max-inactive-interval($a)) then if (session:get-max-inactive-interval() eq $a) then '<true/>' else '<false/>' else <info> Problem with setMaxInactiveInterval() </info>");
		x.serialize(ctx, buffer);
		assertEquals("<true/>", buffer.toString());
	}

	@Test
	public void getSessionAtt() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := session:set-attribute('teste',<p>Test Attribute</p>) return session:get-attribute('teste')");
		x.serialize(ctx, buffer);
		assertEquals("<p>Test Attribute</p>", buffer.toString());
	}

	@Test
	public void invalidate() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := session:set-attribute('test',<info/>) return let $b := session:invalidate() return session:get-attribute('teste')");
		x.serialize(ctx, buffer);
		assertEquals("", buffer.toString());
	}

	@Test
	public void removeSessionAtt() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := session:set-attribute('test',<p>Test Attribute</p>) return session:remove-attribute('test')");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertTrue(new Boolean(buffer.toString()));
	}
}