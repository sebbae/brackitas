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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
public class TestResource extends BaseASQueryContext {

	private static String path = "src/main/resources/apps/resourceTestFile.xq";

	@Before
	public void initFields() throws Exception {
		super.initFields();
		new File(path).createNewFile();
	};

	@Test
	public void deleteResource() throws QueryException, FileNotFoundException,
			IOException {
		ASXQuery x = new ASXQuery(new ASCompileChain(metaDataMgr, tx),
				"if (rsc:delete(\"resourceTestFile.xq\")) then \"true\" else \"false\"");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void renameResource() throws QueryException, FileNotFoundException,
			IOException {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"if (rsc:rename('resourceTestFile.xq','resource')) then if (rsc:rename('resource','resourceTestFile.xq')) then 'true' else 'false' else 'false'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@Test
	public void uploadResource() throws QueryException, FileNotFoundException,
			IOException {
		ASXQuery x = new ASXQuery(
				new ASCompileChain(metaDataMgr, tx),
				"if (rsc:upload('appServer','http://www.ebookpdf.net/screen/cover2/51fvzphaxml_aa240_.jpg')) then if (rsc:delete('appServer/51fvzphaxml_aa240_.jpg')) then 'true' else 'false' else 'false'");
		x.setPrettyPrint(true);
		x.serialize(ctx, buffer);
		assertEquals("true", buffer.toString());
	}

	@After
	public void removeFile() throws QueryException {
		new File(path).delete();
	}

}
