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
package org.brackit.as.http.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.TXServlet;
import org.brackit.server.ServerException;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class AppServlet extends TXServlet {

	protected static final String errorServ = "/app/error/";

	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doGet(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doPost(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doPut(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected final void service(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			service(req, resp, session);
		} catch (Throwable e) {
			log.error(e);
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				log.error(e1);
			}
			req.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, errorServ);
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	public final void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
	}

}
