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
package org.brackit.as.xquery.function.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.brackit.as.context.BaseAppContext;
import org.brackit.as.http.HttpConnector;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.ASUncompiledQuery;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.node.d2linked.D2NodeFactory;
import org.brackit.xquery.node.parser.DocumentParser;
import org.brackit.xquery.util.annotation.FunctionAnnotation;
import org.brackit.xquery.util.io.IOUtils;
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
@FunctionAnnotation(description = "Returns the physical structural representation"
		+ "of the application folders and files. This representation is returned as "
		+ "an XML document.", parameters = "$applicationName")
public class GetStructure extends AbstractFunction {

	public static final QNm DEFAULT_NAME = new QNm(AppFun.APP_NSURI,
			AppFun.APP_PREFIX, "get-structure");

	public GetStructure() {
		this(DEFAULT_NAME);
	}

	public GetStructure(QNm name) {
		super(name, new Signature(new SequenceType(AnyItemType.ANY,
				Cardinality.ZeroOrOne), new SequenceType(AtomicType.STR,
				Cardinality.One)), true);
	}

	public GetStructure(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		try {
			String app = ((Atomic) args[0]).atomize().stringValue().trim();
			File f = new File(String.format("%s/%s", HttpConnector.APPS_PATH,
					app));
			List<ASUncompiledQuery> luq;
			try {
				ServletContext s = ((ASQueryContext) ctx).getReq()
						.getServletContext();
				luq = ((BaseAppContext) s.getAttribute(app))
						.getUncompiledQueries();
			} catch (Exception e) {
				luq = new ArrayList<ASUncompiledQuery>();
			}
			StringBuffer sb = listStructure(f, luq);
			return new D2NodeFactory().build(new DocumentParser(sb.toString()));
		} catch (Exception e) {
			throw new QueryException(e, AppFun.APP_GETSTRUCTURE_INT_ERROR, e
					.getMessage());
		}
	}

	private StringBuffer listStructure(File f, List<ASUncompiledQuery> luq)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("<app name=\"%s\">\n", f.getName()));
		for (File c : f.listFiles())
			readStructure(c, sb, luq);
		sb.append("</app> \n");
		return sb;
	}

	private void readStructure(File f, StringBuffer sb,
			List<ASUncompiledQuery> luq) throws IOException {
		if (f.isDirectory()) {
			sb.append(String.format("<dir name=\"%s\">\n", f.getName()));
			for (File c : f.listFiles())
				readStructure(c, sb, luq);
			sb.append("</dir> \n");
		} else {
			for (ASUncompiledQuery uq : luq) {
				if (IOUtils.getNormalizedPath(f).contains(uq.getPath())) {
					sb.append(String.format(
							"<file name=\"%s\" compError=\"true\"/>\n", f
									.getName()));
					return;
				}
			}
			sb.append(String.format("<file name=\"%s\"/>\n", f.getName()));
		}
	}
}