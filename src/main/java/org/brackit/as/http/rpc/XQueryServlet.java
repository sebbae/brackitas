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
package org.brackit.as.http.rpc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.xquery.QueryContext;
import org.brackit.xquery.QueryException;
import org.brackit.xquery.XQuery;

/**
 * 
 * @author Max Bechtold
 * @author Sebastian Baechle
 * 
 */
public class XQueryServlet extends RPCServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		process(req, resp, session);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		process(req, resp, session);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws ServletException, QueryException,
			IOException, SessionException {
		String query = null;
		boolean indent = true;
		boolean materialize = true;

		// Parse parameters
		query = req.getParameter("query");

		String param = req.getParameter("indent");
		if (param != null) {
			indent = Boolean.parseBoolean(param);
		}

		param = req.getParameter("materialize");
		if (param != null) {
			materialize = Boolean.parseBoolean(param);
		}

		if (query == null) {
			throw new ServletException("Missing parameter 'query'!");
		}

		resp.setContentType("text/xml; charset=UTF-8");
		resp.setHeader("Content-disposition", "inline;");
		XQuery xq = new XQuery(query);
		xq.setPrettyPrint(false);
		PrintStream out = new PrintStream(new BufferedOutputStream(resp
				.getOutputStream()));
		xq.serialize(new QueryContext(), out);
		out.flush();
	}
}
