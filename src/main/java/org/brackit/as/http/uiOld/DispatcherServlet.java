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
package org.brackit.as.http.uiOld;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.as.http.AbstractServlet;

/**
 * 
 * @author Henrique Valer
 * @author Sebastian Baechle
 * 
 */
public class DispatcherServlet extends AbstractServlet {
	/**
	 * The doGet dispatcher only select to which servlet the request will be
	 * dispatched. The selection in this case is made based on the parameter
	 * "payload". Each servlet is then responsible for the dispatched request.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext context = getServletContext();

		try {
			String strPayload = req.getParameter("payload"); // Choose witch
			// contains will be
			// show for the
			// page

			if (strPayload != null) {
				if (strPayload.equals("form_query"))
					doDispatch(req, resp, "/ui/query/");

				else if (strPayload.equals("form_upload"))
					doDispatch(req, resp, "/ui/upload/");

				else if (strPayload.equals("form_download"))
					doDispatch(req, resp, "/ui/formDownload/");

				else if (strPayload.equals("file_download"))
					doDispatch(req, resp, "/ui/fileDownload/");

				else if (strPayload.equals("form_procedure"))
					doDispatch(req, resp, "/ui/procedure/");
			} else {
				doDispatch(req, resp, "/ui/query/");
			}
		} catch (Exception e) {
			context.setAttribute("errorMsg", e.getMessage());
			doDispatch(req, resp, "/ui/error/");
		}
	}

	/**
	 * The doPost dispatcher only dispatche the request to the upload servlet,
	 * since it's the only case of post in this application. The upload servlet
	 * is then responsible for the dispatched request.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doDispatch(req, resp, "/ui/upload/");
	}
}
