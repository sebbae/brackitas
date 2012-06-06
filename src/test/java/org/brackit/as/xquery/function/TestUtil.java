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

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.as.xquery.function.base.BaseASQueryContext;
import org.brackit.xquery.QueryException;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class TestUtil extends BaseASQueryContext {

	@Before
	public void initFields() throws Exception {
		super.initFields();
	};

	@Test
	public void getMimeType() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:get-mime-type('test.txt')");
		x.serialize(ctx, buffer);
		assertEquals("text/plain", buffer.toString());
	}

	@Test
	public void listPredefinedFunctions() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:list-predefined-functions('util')");
		x.serialize(ctx, buffer);
		assertEquals(true, buffer.toString().contains(
				"list-predefined-functions") ? true : false);
	}

	@Test
	public void listPredefinedModules() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:list-predefined-modules()");
		x.serialize(ctx, buffer);
		assertEquals(true, buffer.toString().contains("util") ? true : false);
	}

	@Test
	public void mkDirectory() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:mk-dir('test')");
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void rmDirectory() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:rm-directory('test')");
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void plainPrint() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:plain-print('<p/>')");
		x.serialize(ctx, buffer);
		assertEquals(true, buffer.toString().contains("&lt;p/&gt") ? true
				: false);
	}

	@Test
	public void getASProperty() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"util:get-property(\"apps.directory\")");
		x.serialize(ctx, buffer);
		assertEquals(
				"/home/zidane/workspace/brackitas_mig/src/main/resources/apps/",
				buffer.toString());
	}

}
