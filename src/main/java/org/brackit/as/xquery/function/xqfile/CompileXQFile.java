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
package org.brackit.as.xquery.function.xqfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.brackit.xquery.util.annotation.FunctionAnnotation;
import org.brackit.as.context.BaseAppContext;
import org.brackit.as.http.HttpConnector;

import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.ASUncompiledQuery;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Bool;
import org.brackit.xquery.atomic.QNm;
import org.brackit.xquery.function.AbstractFunction;
import org.brackit.xquery.module.StaticContext;
import org.brackit.xquery.xdm.Signature;
import org.brackit.xquery.xdm.Sequence;

/**
 * 
 * @author Henrique Valer
 * 
 */
@FunctionAnnotation(description = "Compiles the given query ($query) and stores "
		+ "it at the given file path name destination. The file path name "
		+ "starts from the applications directory, by default: src/main/resources/apps."
		+ " The actual process first saves the query on the file path name, then "
		+ "compiles it. On success, the query is directly available for access "
		+ "(execution) over HTTP.", parameters = { "$filePathName", "$query" })
public class CompileXQFile extends AbstractFunction {

	public CompileXQFile(QNm name, Signature signature) {
		super(name, signature, true);
	}

	@Override
	public Sequence execute(StaticContext sctx, QueryContext ctx,
			Sequence[] args) throws QueryException {
		try {
			String fPathName = ((Atomic) args[0]).atomize().stringValue()
					.trim();
			fPathName = (fPathName.startsWith("/")) ? fPathName.substring(1)
					: fPathName;
			String fQuery = ((Atomic) args[1]).atomize().stringValue().trim();
			String app = fPathName.split("/")[0];
			String base = String.format("%s/%s", HttpConnector.APPS_PATH,
					fPathName);
			// saving step
			FileWriter f = new FileWriter(base);
			BufferedWriter out = new BufferedWriter(f);
			out.write(fQuery.replaceAll("&", "&amp;"));
			out.close();
			// compilation step
			try {
				HttpConnector.compileApplication(new File(String.format(
						"%s/%s", HttpConnector.APPS_PATH, app)));
			} catch (NullPointerException e) {
			}
			ServletContext servletCtx = ((ASQueryContext) ctx).getReq()
					.getServletContext();
			BaseAppContext bac;
			try {
				bac = (BaseAppContext) servletCtx.getAttribute(app);
			} catch (Exception e) {
				bac = new BaseAppContext(app, new ASCompileChain(
						((ASQueryContext) ctx).getMDM(), ((ASQueryContext) ctx)
								.getTX()));
			}
			List<ASUncompiledQuery> l = bac.getUncompiledQueries();
			Iterator<ASUncompiledQuery> i = l.iterator();
			while (i.hasNext()) {
				ASUncompiledQuery a = i.next();
				if (a.getPath().contains(fPathName))
					throw new QueryException(a.getE(),
							XqfileFun.XQFILE_COMPILE_INT_ERROR, a.getE()
									.getMessage());
			}
			return Bool.TRUE;
		} catch (Exception e) {
			throw new QueryException(e, XqfileFun.XQFILE_COMPILE_INT_ERROR,
					e.getMessage());
		}
	}
}