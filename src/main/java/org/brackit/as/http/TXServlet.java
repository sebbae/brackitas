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
package org.brackit.as.http;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.brackit.as.xquery.HttpSessionTXQueryContext;
import org.brackit.as.xquery.compiler.ASCompileChain;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.TXQueryContext;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.IsolationLevel;
import org.brackit.server.tx.Tx;
import org.brackit.xquery.XQuery;
import org.brackit.xquery.compiler.CompileChain;
import org.brackit.xquery.util.log.Logger;

/**
 * 
 * @author Sebastian Baechle
 * 
 */
public abstract class TXServlet extends AbstractServlet {
	protected static final Logger log = Logger.getLogger(TXServlet.class);

	public static final String SESSION = "_session";

	protected String query(Session session, String query) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		CompileChain chain = new ASCompileChain(metaDataMgr, session.getTX());
		new XQuery(chain, query).serialize(new TXQueryContext(session.getTX(),
				metaDataMgr), new PrintStream(buf));
		return buf.toString("UTF-8");
	}

	protected String httpQuery(Session session, String query,
			HttpSession httpSession) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		CompileChain chain = new ASCompileChain(metaDataMgr, session.getTX());
		XQuery x = new XQuery(chain, query);
		x.setPrettyPrint(true);
		x.serialize(new HttpSessionTXQueryContext(session.getTX(), metaDataMgr,
				httpSession), new PrintStream(buf));
		return buf.toString("UTF-8");
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		super.doDelete(req, resp);
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		super.doPut(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		super.doPost(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		super.doGet(req, resp);
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		super.service(req, resp);
	}

	protected void cleanup(Session session, Tx tx) {
		try {
			if (tx == null)
				session.commit();
			else
				session.rollback();
		} catch (ServerException e1) {
			log.error(e1);
		}
	}

	protected Session getSession(HttpServletRequest req)
			throws ServletException {
		try {
			HttpSession httpSession = (HttpSession) req.getSession();
			Session session = (Session) httpSession.getAttribute(SESSION);
			if (session == null) {
				session = sessionMgr.getSession(sessionMgr.login());
				session.setIsolationLevel(IsolationLevel.NONE);
				httpSession.setAttribute(SESSION, session);
			}
			return session;
		} catch (SessionException e) {
			throw new ServletException(e);
		}
	}
}
