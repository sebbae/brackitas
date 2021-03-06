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
package org.brackit.as.xquery.function.util;

import java.io.PrintStream;

import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.atomic.Str;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.util.annotation.FunctionAnnotation;
import org.brackit.xquery.util.io.IOUtils;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Iter;
import org.brackit.xquery.xdm.Sequence;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.type.AnyItemType;
import org.brackit.xquery.xdm.type.AtomicType;
import org.brackit.xquery.xdm.type.Cardinality;
import org.brackit.xquery.xdm.type.SequenceType;

/**
 * 
 * @author Henrique Valer
 * 
 */
@FunctionAnnotation(description = "Prints a plain version of any given string"
		+ "value to the default output. Solves problems like printing \"&lt;\" "
		+ "or \"&gt;\" that would be interpreted by the browser as HTML content.", parameters = "$string")
public class PlainPrint extends AbstractFunction {

	public static final QNm DEFAULT_NAME = new QNm(UtilFun.UTIL_NSURI,
			UtilFun.UTIL_PREFIX, "plain-print");

	public PlainPrint() {
		this(DEFAULT_NAME);
	}

	public PlainPrint(QNm name) {
		super(name, new Signature(new SequenceType(AtomicType.STR,
				Cardinality.ZeroOrOne), new SequenceType(AnyItemType.ANY,
				Cardinality.One)), true);
	}

	public PlainPrint(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		String vQuery = null;
		PrintStream buf = IOUtils.createBuffer();
		try {
			Iter it = args[0].iterate();
			try {
				Item item;
				while ((item = it.next()) != null) {
					buf.append(item.toString());
					buf.append('\n');
				}
				vQuery = buf.toString();
			} catch (Exception e) {
				vQuery = ((Atomic) args[0]).stringValue().trim();
			} finally {
				it.close();
			}
		} catch (Exception e) {
			throw new QueryException(e, UtilFun.UTIL_PLAINPRINT_INT_ERROR, e
					.getMessage());
		} finally {
			buf.close();
		}
		vQuery = vQuery.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		return new Str(vQuery);
	}
}