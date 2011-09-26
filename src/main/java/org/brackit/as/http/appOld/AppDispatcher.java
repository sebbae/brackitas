/*
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.brackit.as.http.appOld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.xquery.ASXQuery;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Una;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.expr.Cast;
import org.brackit.xquery.expr.ExtVariable;
import org.brackit.server.session.Session;
import org.brackit.xquery.sequence.type.AtomicType;
import org.brackit.xquery.sequence.type.SequenceType;
import org.brackit.xquery.xdm.Item;
import org.brackit.xquery.xdm.Type;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class AppDispatcher extends AppServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		// authentication error
		String authError = ((String) req.getAttribute("authError"));
		if (authError != null) {
			ServletContext context = getServletContext();
			req
					.setAttribute("errorMsg", (String) req
							.getAttribute("authError"));
			RequestDispatcher dispatcher = context
					.getRequestDispatcher("/app/error/");
			dispatcher.forward(req, resp);
		}

		// Usage: http://localhost:8080/app/appName/queryName/
		String appName = ((Atomic) req.getSession().getAttribute("appName"))
				.stringValue();
		String pageName = ((Atomic) req.getSession().getAttribute("pageName"))
				.stringValue();

		// was HttpSessionQueryContext
		QueryContext ctx = new ASQueryContext(session.checkTX(),
				metaDataMgr, req.getSession(),req);
		try {
			// Dinamic binding of parameters: name = variable name
			CompileChain chain = new ASCompileChain(metaDataMgr, session.checkTX());			
			ASXQuery x = new ASXQuery(chain, getQueryFile(appName, pageName));
			for (ExtVariable var : x.getModule().getVariables()
					.getDeclaredVariables()) {
				SequenceType type = var.getType();
				if ((type != null) && (var.getType().getItemType().isAtomic())) {
					Type expectedAtomicType = ((AtomicType) var.getType()
							.getItemType()).type;
					String param = req.getParameter(var.getName()
							.getLocalName());
					if ((param != null) && (!(param = param.trim()).isEmpty())) {
						Item item = new Una(param);
						item = Cast.cast(item, expectedAtomicType, false);
						ctx.bind(var.getName(), item);
					}
				}
			}
			x.setPrettyPrint(true);
			x.serialize(ctx, new PrintStream(resp.getOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			ServletContext context = getServletContext();
			req.setAttribute("errorMsg", e.getMessage());
			RequestDispatcher dispatcher = context
					.getRequestDispatcher("/app/error/");
			dispatcher.forward(req, resp);
		}
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Search for a query file within the public or private sections
	 * 
	 * @param appName
	 * @param pageName
	 * @return
	 * @throws FileNotFoundException
	 */
	private InputStream getQueryFile(String appName, String pageName)
			throws FileNotFoundException {
		try {
			ClassLoader cl = getClass().getClassLoader();
			InputStream in = cl.getResourceAsStream(String.format(
					"apps/%s/queries/private/%s.xq", appName, pageName));
			return (in != null) ? in : cl.getResourceAsStream(String.format(
					"apps/%s/queries/public/%s.xq", appName, pageName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new FileNotFoundException();
	}
}
