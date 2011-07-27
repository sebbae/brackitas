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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class ErrorServlet extends UIServlet {
	/**
	 * Both get and post errors are processed equally. The message is displayed
	 * in a proper page Since there will came every type of erros, we cannot
	 * rely on querying the page from the database, so the error file is taken
	 * directly.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void process(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String errorMsg = (String) req.getAttribute("errorMsg");
		String vReturn = "";
		String str;

		BufferedReader in = new BufferedReader(new FileReader(
				"src/main/html/error.html"));

		while ((str = in.readLine()) != null) {
			vReturn = vReturn + str;
		}
		in.close();

		vReturn = vReturn.replaceAll(helper.FORM_ERROR_MESSAGE, errorMsg);

		// output
		new PrintStream(resp.getOutputStream()).append(vReturn);
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * The doGet method only redirect to be processed, so the error can be
	 * displayed
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
	 * The doPost method only redirect to be processed, so the error can be
	 * displayed
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
