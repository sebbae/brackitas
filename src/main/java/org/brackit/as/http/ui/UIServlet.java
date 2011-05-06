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
package org.brackit.as.http.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.TXServlet;
import org.brackit.server.ServerException;
import org.brackit.server.metadata.manager.impl.ItemNotFoundException;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.Tx;
import org.brackit.xquery.node.parser.DocumentParser;
import org.brackit.xquery.xdm.DocumentException;

/**
 * 
 * @author Sebastian Baechle
 * 
 */
public abstract class UIServlet extends TXServlet {
	protected static final Helper helper = new Helper();

	private static boolean initialized;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		checkDefaultDocuments();
	}

	private synchronized void checkDefaultDocuments() {
		if (initialized) {
			return;
		}

		Session session = null;
		Tx tx = null;

		try {
			session = sessionMgr.getSession(sessionMgr.login());
			tx = session.getTX();
			try {
				metaDataMgr.lookup(tx, "/form.html");
			} catch (ItemNotFoundException e) {
				createDefaultDocuments(tx);
			}
			session.commit();
			initialized = true;
		} catch (Throwable e) {
			log.error(e);
			if (tx != null) {
				try {
					session.rollback();
				} catch (SessionException e1) {
					log.error(e1);
				}
			}
		} finally {
			if (session != null) {
				sessionMgr.logout(session.getSessionID());
			}
		}
	}

	private void createDefaultDocuments(Tx tx) throws ServerException,
			IOException, DocumentException {
		// store files for http access
		metaDataMgr.create(tx, "form.html", new DocumentParser(new File(
				"html/form.html")));
		metaDataMgr.create(tx, "upload.html", new DocumentParser(new File(
				"html/upload.html")));
		metaDataMgr.create(tx, "download.html", new DocumentParser(new File(
				"html/download.html")));
		metaDataMgr.create(tx, "procedure.html", new DocumentParser(new File(
				"html/procedure.html")));
		metaDataMgr.create(tx, "error.html", new DocumentParser(new File(
				"html/error.html")));
		InputStream in = new FileInputStream("html/css/XTCcss.css");
		metaDataMgr.putBlob(tx, in, "/XTCcss.css", -1);
		in = new FileInputStream("html/js/XTCjs.js");
		metaDataMgr.putBlob(tx, in, "/XTCjs.js", -1);
		in = new FileInputStream("html/images/xtc.png");
		metaDataMgr.putBlob(tx, in, "/xtc.png", -1);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		Tx tx = session.checkTX();

		try {
			doDelete(req, resp, session);
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
			doDispatch(req, resp, "/ui/error/");
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
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
			doDispatch(req, resp, "/ui/error/");
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
			doDispatch(req, resp, "/ui/error/");
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
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
			doDispatch(req, resp, "/ui/error/");
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext context = getServletContext();
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
			doDispatch(req, resp, "/ui/error/");
		} finally {
			cleanup(session, tx);
		}
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		super.service(req, resp);
	}
}
