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
package org.brackit.as.xquery.function.base;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.brackit.as.xquery.ASQueryContext;
import org.brackit.server.BrackitDB;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.manager.MetaDataMgr;
import org.brackit.server.tx.Tx;
import org.brackit.server.tx.TxException;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class BaseASQueryContextTest {

	protected static PrintStream buffer;

	protected static ASQueryContext ctx;

	protected static MetaDataMgr metaDataMgr;

	protected static BrackitDB db;

	protected static Tx tx;

	protected static PrintStream createBuffer() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return new PrintStream(out) {
			final OutputStream baos = out;

			public String toString() {
				return baos.toString();
			}
		};
	}

	protected void initFields() throws ServerException, TxException {
		db = new BrackitDB(true);
		metaDataMgr = db.getMetadataMgr();
		tx = db.getTaMgr().begin();
		buffer = createBuffer();
	}

}
