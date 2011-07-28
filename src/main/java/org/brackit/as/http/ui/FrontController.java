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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.AbstractServlet;
import org.brackit.server.session.SessionException;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class FrontController extends AbstractServlet {
	/**
	 * Both get and post request are processed equally. The login is made and
	 * the request is then sent to the dispatcher servlet. In case of errors,
	 * the page is dispatched to the error servlet.
	 * 
	 * @param request
	 *            : HTTP request
	 * @param response
	 *            : HTTP response
	 * @throws ServletException
	 * @throws IOException
	 * @throws SessionException
	 * @throws ServletException
	 */
	private void process(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		ServletContext context = getServletContext();
		try {
			RequestDispatcher dispatcher = context
					.getRequestDispatcher("/ui/dispatcher/");
			response.setContentType("application/xhtml+xml; charset=UTF-8");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			context.setAttribute("errorMsg", e.getMessage());
			doDispatch(request, response, "/ui/error/");
		}
	}

	/**
	 * The doPost method only redirect to be processed, so the request can be
	 * send to the right servlet.
	 * 
	 * @param request
	 *            : HTTP request
	 * @param response
	 *            : HTTP response
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}

	/**
	 * The doPost method only redirect to be processed, so the request can be
	 * send to the right servlet.
	 * 
	 * @param request
	 *            : HTTP request
	 * @param response
	 *            : HTTP response
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}

}
