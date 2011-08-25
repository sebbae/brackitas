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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.brackit.server.metadata.TXQueryContext;
import org.brackit.server.procedure.Procedure;
import org.brackit.server.procedure.ProcedureUtil;
import org.brackit.server.session.Session;
import org.brackit.server.session.SessionException;
import org.brackit.server.tx.TxException;

/**
 * 
 * @author Henrique Valer
 * 
 */
public class ProcedureServlet extends UIServlet {

	/**
	 * Shows to the user a list with all the available procedures to be used
	 * from the XTC Database.
	 * 
	 * @param req
	 *            HTTP request
	 * @param resp
	 *            HTTP response
	 * @return String with the web page containing form for upload files to
	 *         Brackit database
	 * @throws SessionException
	 * @throws TxException
	 * @throws XQueryException
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws Exception {
		String vReturn = null;
		String strProceduresSelectList = getProceduresSelectList();
		String strProceduresDivLis = getProceduresDivList();

		// Add list of procedures and procedure div to the page
		vReturn = httpQuery(session, "fn:doc('procedure.html')", req
				.getSession());
		vReturn = vReturn.replaceAll(helper.FORM_PROCEDURE_SELECT,
				strProceduresSelectList);
		vReturn = vReturn.replaceAll(helper.FORM_PROCEDURE_DIV_LIST,
				strProceduresDivLis);

		// Search for queried procedure
		String queryProcedure = req.getParameter("select_procedure");

		// Search for procedure parameters
		String[] strParameters = null;
		if ((queryProcedure != null) && (!queryProcedure.equals("null"))) {
			Procedure procedure = ProcedureUtil.getProcedure(queryProcedure);
			strParameters = new String[procedure.getParameter().length];

			for (int i = 0; i < procedure.getParameter().length; i++)
				strParameters[i] = req.getParameter("param"
						+ procedure.getName() + i);

			try {
				// executes, calculating times
				long procTime = System.currentTimeMillis();
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				ProcedureUtil.execute(new TXQueryContext(session.getTX(),
						metaDataMgr), buf, queryProcedure, strParameters);
				procTime = System.currentTimeMillis() - procTime;
				// replace results
				String strResultEdited = buf.toString("UTF-8").replaceAll("<",
						"&lt;").replaceAll(">", "&gt;");
				vReturn = vReturn.replaceAll(helper.FORM_RESULT_AREA,
						"<textarea cols=\"100\" name=\"result\" rows=\"6\">"
								+ strResultEdited + "</textarea>");
				vReturn = vReturn.replaceAll(helper.FORM_SECONDS_AREA, "In "
						+ procTime + " milliseconds.");
			} catch (Exception e) {
				String strExcMessage = Matcher.quoteReplacement(e.getMessage());
				vReturn = vReturn.replaceAll(helper.FORM_RESULT_AREA,
						"<textarea cols=\"100\" name=\"result\" rows=\"6\">"
								+ strExcMessage + "</textarea>");
			}
		}
		vReturn = vReturn.replaceAll(helper.FORM_PROCEDURE_ONLOAD, Integer
				.toString(this.getProceduresCount()));
		session.commit();
		// result output
		new PrintStream(resp.getOutputStream()).append(vReturn);
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * getProceduresDivList: List the HTML divs of all procedures. On each div
	 * there is the description of each procedure, together with the fields to
	 * read the procedure parameters
	 * 
	 * @return a string with this list of div, one for each procedure.
	 */
	private String getProceduresDivList() {
		int i = 0;
		StringBuffer strSelectProcDetails = new StringBuffer();
		for (Procedure procedure : ProcedureUtil.getPlans()) {
			strSelectProcDetails.append(String.format("<div id=\"proc_%s\">\n",
					i++));
			strSelectProcDetails
					.append("  <table style=\"width: 100%; background-color: #E0E0F0;\">\n");
			strSelectProcDetails.append("    <thead>\n");
			strSelectProcDetails.append("      <tr>\n");
			strSelectProcDetails.append("        <th colspan=\"2\">\n");
			strSelectProcDetails.append(String.format("        %s: %s<br />\n",
					procedure.getName(), procedure.getInfo().replaceAll("<",
							"&lt;").replaceAll(">", "&gt;")));
			strSelectProcDetails.append("        </th>\n");
			strSelectProcDetails.append("      </tr>\n");
			strSelectProcDetails.append("    </thead>\n");
			strSelectProcDetails.append("    <tbody>\n");
			String[] param = procedure.getParameter();
			for (int i1 = 0; i1 < procedure.getParameter().length; i1++) {
				strSelectProcDetails.append("      <tr>\n");
				strSelectProcDetails.append("        <td width=\"30%\">\n");
				strSelectProcDetails.append(String.format("%s\n", param[i1]
						.replaceAll("<", "&lt;").replaceAll(">", "&gt;")));
				strSelectProcDetails.append("        </td>\n");
				strSelectProcDetails.append("        <td width=\"70%\">\n");
				strSelectProcDetails
						.append(String
								.format(
										"<input type=\"text\" name=\"param%s%s\" value=\"\" /><br />\n",
										procedure.getName(), i1));
				strSelectProcDetails.append("        </td>\n");
				strSelectProcDetails.append("      </tr>\n");
			}
			strSelectProcDetails.append("    </tbody>\n");
			strSelectProcDetails.append("  </table>\n");
			strSelectProcDetails.append("</div>\n");
		}
		return strSelectProcDetails.toString();
	}

	/**
	 * getProceduresSelectList: List a HTML select like of the procedures
	 * available under Brackit database.
	 * 
	 * @return a string with this HTML select list of procedures.
	 */
	private String getProceduresSelectList() {
		int i = 0;
		StringBuffer strSelectProcedures = new StringBuffer();

		strSelectProcedures
				.append("<select id=\"select_procedure\" name=\"select_procedure\" onchange=\"HideMultipleIdFields('proc_',"
						+ this.getProceduresCount()
						+ ");ShowItemFromSelect('proc_','select_procedure')\">\n");
		strSelectProcedures
				.append("  <option id=\"null\" value=\"null\">---</option>\n");

		for (Procedure procedure : ProcedureUtil.getPlans())
			strSelectProcedures.append(String.format(
					"  <option id=\"%s\" value=\"%s\">%s</option>\n", i++,
					procedure.getName(), procedure.getName()));
		strSelectProcedures.append("</select>\n");

		return strSelectProcedures.toString();
	}

	/**
	 * getProceduresCount: Returns the number of procedures under Brackit
	 * database.
	 * 
	 * @return integer
	 */
	private int getProceduresCount() {
		int vReturn = 0;
		for (Procedure procedure : ProcedureUtil.getPlans())
			vReturn++;
		return vReturn - 1;
	}
}
