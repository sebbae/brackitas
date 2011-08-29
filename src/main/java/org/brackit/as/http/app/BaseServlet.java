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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.HttpConnector;
import org.brackit.as.http.TXServlet;
import org.brackit.server.session.Session;

/**
 * 
 * @author Henrique Valer
 *
 */
public class BaseServlet extends TXServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doGet(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	};

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Session session = getSession(req);
		try {
			doPost(req, resp, session);
		} catch (Throwable e) {
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
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
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
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
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
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
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
			doDispatch(req, resp, HttpConnector.APP_ERROR_DISP_TARGET);
		}
	}
}
