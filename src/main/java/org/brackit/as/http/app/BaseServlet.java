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
package org.brackit.as.http.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.TXServlet;
import org.brackit.as.xquery.ASQueryContext;
import org.brackit.server.ServerException;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class BaseServlet extends TXServlet {

	private static final long serialVersionUID = 5318557027440254730L;

	protected ASQueryContext ctx;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = null;
		Tx tx = null;
		ctx = null;
		try {
			session = getSession(req);
			tx = session.checkTX();
			doGet(req, resp, session);
			session.commit();
		} catch (Exception e) {
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			log.error(e);
			showError(req, resp, "Get error: ", e);
		} finally {
			if (session != null)
				cleanup(session, tx);
			if (ctx != null)
				ctx.setMultiPartParams(null);
		}
	};

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = null;
		Tx tx = null;
		ctx = null;
		try {
			session = getSession(req);
			tx = session.checkTX();
			doPost(req, resp, session);
			session.commit();
		} catch (Exception e) {
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			log.error(e);
			showError(req, resp, "Post error: ", e);
		} finally {
			if (session != null)
				cleanup(session, tx);
			if (ctx != null)
				ctx.setMultiPartParams(null);
		}
	};

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doPut(req, resp, session);
		} catch (Exception e) {
			log.error(e);
			showError(req, resp, "Put error:", e);
		}
	};

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doDelete(req, resp, session);
		} catch (Exception e) {
			log.error(e);
			showError(req, resp, "Delete error:", e);
		}
	};

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			service(req, resp, session);
		} catch (Exception e) {
			log.error(e);
			showError(req, resp, "Service error: ", e);
		}
	}

	protected void showError(HttpServletRequest req, HttpServletResponse resp,
			String msg, Exception e) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(resp
					.getOutputStream(), "utf-8"));
			writer
					.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
			writer.println("    <head>");
			writer
					.println("        <title>Brackit Application Server error</title>");
			writer
					.println("        <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\"></meta>");
			writer.println("    </head>");
			writer.println("    <body>");
			writer.println("    <h1>Oops:</h1>");
			writer.println("    <p>");
			writer.println("    " + msg);
			writer.println("    </p>");
			if (e != null) {
				writer.println("    <p>");
				e.printStackTrace(writer);
				writer.println("    </p>");
			}
			writer.println("    </body>");
			writer.println("</html>");
			writer.flush();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
