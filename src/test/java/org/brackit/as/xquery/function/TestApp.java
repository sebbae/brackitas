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

import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.as.xquery.function.base.BaseASQueryContextTest;
import org.brackit.as.xquery.function.base.NullAppServer;
import org.brackit.as.xquery.function.base.NullHttpServletRequest;
import org.brackit.as.xquery.function.base.NullHttpSession;
import org.brackit.xquery.QueryException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class TestApp extends BaseASQueryContextTest {

	public static NullAppServer appServer;

	@Before
	public void initFields() throws Exception {
		super.initFields();
		if (appServer == null)
			appServer = new NullAppServer();
		ctx = new ASQueryContext(tx, metaDataMgr, new NullHttpSession(),
				new NullHttpServletRequest());
	};

	@After
	public void removeApp() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"app:delete('a')");
		x.execute(ctx);
	}

	@Test
	public void delete() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (app:delete('a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"app:generate('a','MVC')");
		x.execute(ctx);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void deploy() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (app:deploy('a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void exists() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (app:exist('a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void generate() throws Exception {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then 'OK' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void getNames() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (not(app:get-names() = 'a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void getStructure() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (app:get-structure('a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void isRunning() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (not(app:is-running('a'))) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

	@Test
	public void terminate() throws Exception {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"let $a := app:generate('a','MVC') return if ($a) then if (app:terminate('a')) then 'OK' else 'ERROR' else 'ERROR'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("OK", buffer.toString());
	}

}