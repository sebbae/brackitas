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
import org.brackit.as.xquery.function.base.NullHttpServletRequest;
import org.brackit.as.xquery.function.base.NullHttpSession;
import org.brackit.server.ServerException;
import org.brackit.server.tx.TxException;
import org.junit.Test;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class Request extends BaseASQueryContextTest {

	@Override
	protected void initFields() throws ServerException, TxException {
		super.initFields();
		ctx = new ASQueryContext(tx, metaDataMgr, new NullHttpSession(),
				new NullHttpServletRequest());
	};

	@Test
	public void getAttribute() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-attribute('1')");
		x.serialize(ctx, buffer);
		assertEquals("c1", buffer.toString());
	}

	@Test
	public void getAttributeNames() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-attribute-names()");
		x.serialize(ctx, buffer);
		assertEquals("1 2", buffer.toString());
	}

	@Test
	public void getCookie() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-cookie('1')");
		x.serialize(ctx, buffer);
		assertEquals("c1", buffer.toString());
	}

	@Test
	public void getCookieNames() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-cookie-names()");
		x.serialize(ctx, buffer);
		assertEquals("1 2", buffer.toString());
	}

	@Test
	public void getParameter() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-parameter('1')");
		x.serialize(ctx, buffer);
		assertEquals("c1", buffer.toString());
	}

	@Test
	public void getParameterNames() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:get-parameter-names()");
		x.serialize(ctx, buffer);
		assertEquals("1 2", buffer.toString());
	}

	@Test
	public void isMultipartContent() throws Exception {
		initFields();
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"req:is-multipart-content()");
		x.serialize(ctx, buffer);
		assertEquals("false", buffer.toString());
	}

}
