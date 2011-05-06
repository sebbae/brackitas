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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.HttpConnector;
import org.brackit.server.metadata.TXQueryContext;
import org.brackit.server.procedure.ProcedureUtil;
import org.brackit.server.session.Session;

/**
 * 
 * @author Max Bechtold
 * 
 */
public class ProcedureServlet extends RPCServlet {
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
			Session session) throws Exception {
		String procedure = req.getRequestURI();
		procedure = procedure.substring(HttpConnector.PROC_PREFIX.length() - 1);

		// Parse parameters
		List<String> params = new ArrayList<String>();
		String param = req.getParameter("param1");

		int i = 1;
		while (param != null) {
			params.add(param);
			param = req.getParameter("param" + ++i);
		}

		if (procedure == null || procedure.isEmpty()) {
			throw new ServletException(
					"Missing URL appendix containing procedure to be executed!");
		}

		resp.setContentType("text/xml; charset=UTF-8");
		resp.setHeader("Content-disposition", "inline;");
		ProcedureUtil.execute(new TXQueryContext(session.getTX(), metaDataMgr),
				resp.getOutputStream(), procedure, params
						.toArray(new String[0]));
	}
}
