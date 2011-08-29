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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.HttpConnector;
import org.brackit.server.session.Session;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class FrontController extends BaseServlet {

	private static final long serialVersionUID = 4156829801096332265L;

	public static final String APP_SESSION_ATT = "appName";

	public static final String PAGE_SESSION_ATT = "pageName";

	public static final String HTTP_URI_REQ = "httpUriReq";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {

		ServletContext context = getServletContext();

		// TODO:
		// Resolve Application
		try {
			String[] URI = req.getRequestURI().split("/");
			/*
			 * http://localhost:24280/app/eCommerce/css/1/a.css for (int i = 0;
			 * i < URI.length; i++) { System.out.println("i: " + i + " ::: " +
			 * URI[i]); }
			 */
			// TODO: Improve get naming
			String appName = URI[2];
			String pageName = URI[3];

			req.setAttribute(APP_SESSION_ATT, appName);
			req.setAttribute(PAGE_SESSION_ATT, pageName);
			req.setAttribute(HTTP_URI_REQ, req.getRequestURI());

			// Resolve resource
			String docName = req.getRequestURI();
			docName = docName.substring(docName.lastIndexOf("/"));

			String a1 = getMimeType("");
			String a2 = getMimeType(docName);
			if (a1 != a2) {
				RequestDispatcher dispatcher = context
						.getRequestDispatcher(HttpConnector.APP_RESOURCE_DISP_TARGET);
				dispatcher.forward(req, resp);
			}

			// Compile query

			// Execute it

		} catch (Exception e) {
			req.setAttribute(ErrorHandler.ERROR_ATT, e.getMessage());
			RequestDispatcher dispatcher = context
					.getRequestDispatcher(HttpConnector.APP_ERROR_DISP_TARGET);
			dispatcher.forward(req, resp);
		}
	}
}
