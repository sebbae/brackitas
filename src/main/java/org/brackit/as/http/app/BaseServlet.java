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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.http.TXServlet;
import org.brackit.server.ServerException;
import org.brackit.server.session.Session;
import org.brackit.server.tx.Tx;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Str;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class BaseServlet extends TXServlet {

	private static final long serialVersionUID = 1L;

	protected static String URI;

	protected String APP;

	protected String RESOURCE;	
	
	protected void resolveApplication(HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setContentType("text/html;charset=UTF-8");
		URI = req.getRequestURI();
		String[] URIParts = URI.split("/");
		APP = URIParts[2];
		RESOURCE = URI.substring(URI.lastIndexOf("/") + 1);
		req.getSession().setAttribute(FrontController.APP_SESSION_ATT, (Atomic) new Str(APP));
	}	
	
	public void processResourceRequest(String app, String resource,
			HttpServletResponse resp) throws StreamCorruptedException,
			FileNotFoundException {
		try {
			String contentType = getMimeType(resource);
			resp.setContentType(contentType);
			InputStream in = getClass().getResourceAsStream(resource);
			BufferedOutputStream out = new BufferedOutputStream(resp
					.getOutputStream());
			try {
				byte[] buffer = new byte[1024*16];
				int bytesRead = 0;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
			} catch (Exception e) {
				throw new StreamCorruptedException();
			} finally {
				out.close();
				in.close();
				resp.getOutputStream().flush();
				resp.setStatus(HttpServletResponse.SC_OK);
			}
		} catch (StreamCorruptedException e) {
			throw new StreamCorruptedException(String
					.format("Error while reading inputStream of resource %s.",
							resource));
		} catch (Exception e) {
			throw new FileNotFoundException(String.format(
					"File %s does not exist under the application resources.",
					resource));
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = null;
		Tx tx = null;
		try {
			resolveApplication(req, resp);
			if (!FrontController.UNKNOWN_MIMETYPE.equals(getMimeType(URI))) {
				processResourceRequest(APP, URI, resp);
				return;
			} else {
				session = getSession(req);
				tx = session.checkTX();
				doGet(req, resp, session);
				session.commit();
			}
		} catch (Throwable e) {
			// TODO: Erase it
			e.printStackTrace();
			try {
				if (tx == null) {
					session.rollback();
				}
			} catch (ServerException e1) {
				e1.printStackTrace();
				log.error(e1);
			}
		} finally {
			if (session != null)
				cleanup(session, tx);
		}
	};

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doPost(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	};

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doPut(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	};

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doDelete(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	};

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			service(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorServlet.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	}
}
