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

import static org.junit.Assert.*;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.as.xquery.function.base.BaseASQueryContext;
import org.brackit.xquery.QueryException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class TestXqfile extends BaseASQueryContext {

	public static String fileName = "test.xq";

	@Before
	public void initFields() throws Exception {
		super.initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:create('%s')", fileName));
		x.execute(ctx);
	}

	@Test
	public void compileXQFile() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:compile('%s','1')", fileName));
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void createXQFile() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"if (xqfile:create('test2.xq')) then xqfile:delete('test2.xq') else fn:false()");
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void deleteXQFile() throws QueryException {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				String
						.format(
								"if (xqfile:delete('%s')) then xqfile:create('%s') else fn:false()",
								fileName, fileName));
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void getCompilationResult() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:get-compilation-error('%s')", fileName));
		x.serialize(ctx, buffer);
		assertEquals("", buffer.toString());
	}

	@Test
	public void isLibrary() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:is-library('%s')", fileName));
		x.serialize(ctx, buffer);
		assertEquals("false", buffer.toString());
	}

	@Test
	public void saveXQFile() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:save('%s','2')", fileName));
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@After
	public void removeFields() throws QueryException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx), String
				.format("xqfile:delete('%s')", fileName));
		x.execute(ctx);
	}
}
