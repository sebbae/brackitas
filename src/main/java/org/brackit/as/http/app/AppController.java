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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.TXServlet;
import org.brackit.xquery.atomic.Atomic;
import org.brackit.xquery.atomic.Str;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class AppController extends TXServlet {

	/**
	 * Both get and post request are processed equally. The login is made and
	 * the request is then sent to the dispatcher servlet.
	 */
	private void process(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		ServletContext context = getServletContext();
		try {
			String[] URI = req.getRequestURI().split("/");
			/*
			 * for (int i = 0; i < URI.length; i++) { System.out.println("i: " +
			 * i + " ::: " + URI[i]); }
			 */
			String appName = URI[2];
			String pageName = URI[3];

			// Stored as xquery types, cause then expressions have also access
			// to them
			req.getSession().setAttribute("appName", (Atomic) new Str(appName));
			req.getSession().setAttribute("pageName",
					(Atomic) new Str(pageName));
			// grab values from appProperties file.
			req.getSession().setAttribute("loginMD5",
					(Atomic) new Str("EE11CBB19052E40B07AAC0CA060C23EE"));
			req.getSession().setAttribute("passMD5",
					(Atomic) new Str("1A1DC91C907325C69271DDF0C944BC72"));

			RequestDispatcher dispatcher;
			if ((String) req.getAttribute("errorMsg") != null) {
				dispatcher = context.getRequestDispatcher("/app/error/");
			} else {
				dispatcher = context.getRequestDispatcher("/app/dispatcher/");
			}
			resp.setContentType("application/xhtml+xml; charset=UTF-8");
			dispatcher.forward(req, resp);
		} catch (Exception e) {
			req.setAttribute("errorMsg", e.getMessage());
			RequestDispatcher dispatcher = context
					.getRequestDispatcher("/app/error/");
			dispatcher.forward(req, resp);
		}
	}

	/**
	 * The doGet and doPost method only redirect to be processed, so the request
	 * can be send to the right servlet.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}

}
