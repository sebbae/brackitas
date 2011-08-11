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

import java.io.PrintStream;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.TxException;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class QueryServlet extends UIServlet {
	/**
	 * Executes a query on the database to select the HTML form, then takes the
	 * query parameter from the user and executes it on the database also.
	 * Insert the result from the query on the HTML form, with it's time and
	 * more details and return the page form the Servlet handler.
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @return String with the web page containing form for Xquery
	 * @throws XQueryException
	 * @throws SessionException
	 * @throws TxException
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		resp.setContentType("application/xhtml+xml; charset=UTF-8");
		String vReturn = null;
		String queryParameter = req.getParameter("query"); // query parameter
		String strResultEdited = null;
		String vFile = httpQuery(session, "fn:doc('form.html')", req
				.getSession());
		long procTime;

		if (queryParameter != null) {
			try {
				// executes, calculating times
				procTime = System.currentTimeMillis();
				String result = httpQuery(session, queryParameter, req
						.getSession());

				procTime = System.currentTimeMillis() - procTime;
				// retrieves results
				strResultEdited = result.replaceAll("<", "&lt;").replaceAll(
						">", "&gt;");

				// replace results
				vReturn = vFile.replaceAll(helper.FORM_RESULT_AREA,
						"<textarea cols=\"100\" name=\"result\" rows=\"6\">"
								+ strResultEdited + "</textarea>");
				queryParameter = queryParameter.replaceAll("\\$", "\\\\\\$")
						.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				vReturn = vReturn.replaceAll(helper.FORM_QUERY_AREA,
						"<textarea cols=\"100\" name=\"query\" rows=\"6\">"
								+ queryParameter + "</textarea>");
				vReturn = vReturn.replaceAll(helper.FORM_SECONDS_AREA, "In "
						+ procTime + " milliseconds.");
			} catch (Exception e) {
				String strExcMessage = Matcher.quoteReplacement(e.getMessage());
				vReturn = vFile.replaceAll(helper.FORM_RESULT_AREA,
						"<textarea cols=\"100\" name=\"result\" rows=\"6\">"
								+ strExcMessage + "</textarea>");
				vReturn = vReturn.replaceAll(helper.FORM_QUERY_AREA,
						"<textarea cols=\"100\" name=\"query\" rows=\"6\">"
								+ queryParameter + "</textarea>");
			}
		} else {
			vReturn = vFile.replaceAll(helper.FORM_SECONDS_AREA, "");
		}

		// result output
		new PrintStream(resp.getOutputStream()).append(vReturn);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
